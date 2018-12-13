package cc.blynk.server.web.handlers.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.MobileGetServerHandler;
import cc.blynk.server.common.handlers.UserNotLoggedHandler;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.appllication.LoginMessage;
import cc.blynk.server.core.session.mobile.Version;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.internal.ReregisterChannelUtil;
import cc.blynk.server.web.handlers.WebAppHandler;
import cc.blynk.utils.IPUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleGeneralException;
import static cc.blynk.server.core.session.mobile.OsType.WEB_SOCKET;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR_STRING;


/**
 * Handler responsible for managing apps login messages.
 * Initializes netty channel with a state tied with user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class WebAppLoginHandler extends SimpleChannelInboundHandler<LoginMessage> {

    private static final Logger log = LogManager.getLogger(WebAppLoginHandler.class);

    private final Holder holder;

    public WebAppLoginHandler(Holder holder) {
        this.holder = holder;
    }

    private static void cleanPipeline(DefaultChannelPipeline pipeline) {
        //common handlers for websockets and app pipeline
        pipeline.removeIfExists(WebAppLoginHandler.class);
        pipeline.removeIfExists(UserNotLoggedHandler.class);
        pipeline.removeIfExists(MobileGetServerHandler.class);
        pipeline.removeIfExists(WebAppLoginViaInviteHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) {
        String[] messageParts = message.body.split(BODY_SEPARATOR_STRING);

        if (messageParts.length < 2) {
            log.error("Wrong income message format.");
            ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
            return;
        }

        String email = messageParts[0].toLowerCase();

        Version version = messageParts.length > 3
                ? new Version(messageParts[2], messageParts[3])
                : Version.UNKNOWN_VERSION;

        blynkLogin(ctx, message.id, email, messageParts[1], version);
    }

    private void blynkLogin(ChannelHandlerContext ctx, int msgId,
                            String email, String pass,
                            Version version) {
        User user = holder.userDao.getByName(email);

        if (user == null) {
            log.warn("User '{}' not registered. {}", email, ctx.channel().remoteAddress());
            ctx.writeAndFlush(json(msgId, "User not registered."), ctx.voidPromise());
            return;
        }

        if (user.pass == null || !user.pass.equals(pass)) {
            log.warn("User '{}' credentials are wrong. {}", email, ctx.channel().remoteAddress());
            ctx.writeAndFlush(json(msgId, "User credentials are wrong."), ctx.voidPromise());
            return;
        }

        login(ctx, msgId, user, version);
    }

    private void login(ChannelHandlerContext ctx, int messageId, User user, Version version) {
        DefaultChannelPipeline pipeline = (DefaultChannelPipeline) ctx.pipeline();
        cleanPipeline(pipeline);

        Role role = holder.organizationDao.getRole(user.orgId, user.roleId);
        WebAppStateHolder appStateHolder = new WebAppStateHolder(user, role, new Version(WEB_SOCKET, 41));
        pipeline.addLast("AWebAppHandler", new WebAppHandler(holder, appStateHolder));

        Channel channel = ctx.channel();

        Session session = holder.sessionDao.getOrCreateSessionForOrg(appStateHolder.orgId, channel.eventLoop());
        if (session.initialEventLoop != channel.eventLoop()) {
            log.debug("Re registering websocket app channel. {}", ctx.channel());
            ReregisterChannelUtil.reRegisterChannel(ctx, session, channelFuture ->
                    completeLogin(channelFuture.channel(), session, user, messageId, version));
        } else {
            completeLogin(channel, session, user, messageId, version);
        }
    }

    private void completeLogin(Channel channel, Session session, User user, int msgId, Version version) {
        user.lastLoggedIP = IPUtils.getIp(channel.remoteAddress());
        user.lastLoggedAt = System.currentTimeMillis();

        session.addWebChannel(channel);
        channel.writeAndFlush(ok(msgId), channel.voidPromise());

        log.info("{} orgId={} ({}) joined.", user.email, user.orgId, version);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        handleGeneralException(ctx, cause);
    }

}
