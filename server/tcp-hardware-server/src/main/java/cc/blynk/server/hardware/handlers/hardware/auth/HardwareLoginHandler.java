package cc.blynk.server.hardware.handlers.hardware.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.TemporaryTokenValue;
import cc.blynk.server.core.dao.TokenValue;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.appllication.LoginMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.hardware.handlers.hardware.HardwareHandler;
import cc.blynk.server.hardware.internal.ProvisionedDeviceAddedMessage;
import cc.blynk.server.internal.ReregisterChannelUtil;
import cc.blynk.utils.IPUtils;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.structure.LRUCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.RejectedExecutionException;

import static cc.blynk.server.core.protocol.enums.Command.CONNECT_REDIRECT;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_CONNECTED;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_DEVICE;
import static cc.blynk.server.internal.CommonByteBufUtil.invalidToken;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 * Handler responsible for managing hardware and apps login messages.
 * Initializes netty channel with a state tied with user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class HardwareLoginHandler extends SimpleChannelInboundHandler<LoginMessage> {

    private static final Logger log = LogManager.getLogger(HardwareLoginHandler.class);

    private final Holder holder;
    private final DBManager dbManager;
    private final ReportingDBManager reportingDBManager;
    private final BlockingIOProcessor blockingIOProcessor;
    private final String listenPort;
    private final boolean allowStoreIp;

    public HardwareLoginHandler(Holder holder, int listenPort) {
        this.holder = holder;
        this.reportingDBManager = holder.reportingDBManager;
        this.dbManager = holder.dbManager;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        boolean isForce80ForRedirect = holder.props.getBoolProperty("force.port.80.for.redirect");
        this.listenPort = isForce80ForRedirect ? "80" : String.valueOf(listenPort);
        this.allowStoreIp = holder.props.getAllowStoreIp();
    }

    private void completeLogin(Channel channel, Session session,
                               Device device, int msgId) {
        log.debug("completeLogin. {}", channel);

        session.addHardChannel(channel);
        channel.write(ok(msgId));

        /*
        String body = dash.buildPMMessage(device.id);
        if (dash.isActive && body != null) {
            channel.write(makeASCIIStringMessage(HARDWARE, HARDWARE_PIN_MODE_MSG_ID, body));
        }
        */

        log.trace("Device {} goes online DB event.", device.id);
        reportingDBManager.insertSystemEvent(device.id, EventType.ONLINE);
        session.sendToSelectedDeviceOnWeb(HARDWARE_LOG_EVENT, msgId, EventType.ONLINE.name(), device.id);

        channel.flush();

        String responseBody = "" + device.id;
        session.sendToApps(DEVICE_CONNECTED, msgId, responseBody);
        session.sendToWeb(DEVICE_CONNECTED, msgId, responseBody);
        log.trace("Connected device id {}", device.id);
        device.connected();
        if (device.firstConnectTime == 0) {
            device.firstConnectTime = device.connectTime;
        }
        if (allowStoreIp) {
            device.lastLoggedIP = IPUtils.getIp(channel.remoteAddress());
        }

        log.info("{} hardware joined.", device.id);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) {
        String token = message.body.trim();
        TokenValue tokenValue = holder.tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            //token should always be 32 chars and shouldn't contain invalid nil char
            if (token.length() != 32 || token.contains(StringUtils.BODY_SEPARATOR_STRING)) {
                log.debug("HardwareLogic token is invalid. Token '{}', '{}'", token, ctx.channel().remoteAddress());
                ctx.writeAndFlush(invalidToken(message.id), ctx.voidPromise());
            } else {
                //no user on current server, trying to find server that user belongs to.
                checkTokenOnOtherServer(ctx, token, message.id);
            }
            return;
        }

        int orgId = tokenValue.orgId;
        Device device = tokenValue.device;

        if (tokenValue.isTemporary()) {
            //this is special case for provisioned devices, we adding additional
            //handler in order to add product to the device
            TemporaryTokenValue temporaryTokenValue = (TemporaryTokenValue) tokenValue;
            ctx.pipeline().addBefore("H_Login", "HHProvisionedHardwareFirstHandler",
                    new ProvisionedHardwareFirstHandler(holder,
                            orgId, temporaryTokenValue.user, temporaryTokenValue.dash, device));
            ctx.writeAndFlush(ok(message.id));
            return;
        }

        createSessionAndReregister(ctx, orgId, device, message.id);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof ProvisionedDeviceAddedMessage) {
            ProvisionedDeviceAddedMessage msg = (ProvisionedDeviceAddedMessage) evt;
            log.debug("Triggered user event for provisioned device (id={}).", msg.device.id);
            Session session = createSessionAndReregister(ctx,
                    msg.orgId,
                    msg.device,
                    msg.msgId);
            String body = new DeviceDTO(msg.device, msg.product, msg.orgName).toString();
            session.sendToWeb(WEB_CREATE_DEVICE, msg.msgId, body);
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    private Session createSessionAndReregister(ChannelHandlerContext ctx, int orgId,
                                               Device device, int msgId) {
        HardwareStateHolder hardwareStateHolder = new HardwareStateHolder(orgId, device);

        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.replace(this, "HHArdwareHandler", new HardwareHandler(holder, hardwareStateHolder));

        Session session = holder.sessionDao.getOrCreateSessionForOrg(
                hardwareStateHolder.orgId, ctx.channel().eventLoop());

        if (session.isSameEventLoop(ctx)) {
            completeLogin(ctx.channel(), session, device, msgId);
        } else {
            log.debug("Re registering hard channel. {}", ctx.channel());
            ReregisterChannelUtil.reRegisterChannel(ctx, session, channelFuture ->
                    completeLogin(channelFuture.channel(), session, device, msgId));
        }
        return session;
    }

    private void checkTokenOnOtherServer(ChannelHandlerContext ctx, String token, int msgId) {
        //check cache first
        LRUCache.CacheEntry cacheEntry = LRUCache.LOGIN_TOKENS_CACHE.get(token);
        if (cacheEntry == null) {
            try {
                blockingIOProcessor.executeDBGetServer(() -> {
                    String server;
                    log.debug("Checking invalid token in DB.");
                    server = dbManager.getServerByToken(token);
                    LRUCache.LOGIN_TOKENS_CACHE.put(token, new LRUCache.CacheEntry(server));
                    // no server found, that's means token is wrong.
                    sendRedirectResponse(ctx, token, server, msgId);
                });
            } catch (RejectedExecutionException ree) {
                log.warn("Error in getServerByToken handler. Limit of tasks reached.");
                ctx.writeAndFlush(serverError(msgId), ctx.voidPromise());
            }
        } else {
            log.trace("Taking token {} from cache.", token);
            sendRedirectResponse(ctx, token, cacheEntry.value, msgId);
        }
    }

    private void sendRedirectResponse(ChannelHandlerContext ctx, String token, String server, int msgId) {
        MessageBase response;
        if (server == null || server.equals(holder.props.host)) {
            log.trace("HardwareLogic token is invalid. Token '{}', {}", token, ctx.channel().remoteAddress());
            response = invalidToken(msgId);
        } else {
            log.debug("Redirecting token '{}' to {}", token, server);
            response = makeASCIIStringMessage(CONNECT_REDIRECT, msgId, server + BODY_SEPARATOR + listenPort);
        }
        ctx.writeAndFlush(response, ctx.voidPromise());
    }

}
