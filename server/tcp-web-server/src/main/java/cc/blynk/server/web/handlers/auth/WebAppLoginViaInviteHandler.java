package cc.blynk.server.web.handlers.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.GetServerHandler;
import cc.blynk.server.application.handlers.main.auth.Version;
import cc.blynk.server.common.handlers.UserNotLoggedHandler;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.web.WebLoginViaInviteMessage;
import cc.blynk.server.internal.ReregisterChannelUtil;
import cc.blynk.server.internal.TokenUser;
import cc.blynk.server.web.handlers.WebAppHandler;
import cc.blynk.server.web.session.WebAppStateHolder;
import cc.blynk.utils.IPUtils;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.WEB_LOGIN_VIA_INVITE;
import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleGeneralException;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;


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
public class WebAppLoginViaInviteHandler extends SimpleChannelInboundHandler<WebLoginViaInviteMessage> {

    private static final Logger log = LogManager.getLogger(WebAppLoginViaInviteHandler.class);

    private final Holder holder;

    public WebAppLoginViaInviteHandler(Holder holder) {
        this.holder = holder;
    }

    private static void cleanPipeline(DefaultChannelPipeline pipeline) {
        pipeline.removeIfExists(WebAppLoginHandler.class);
        pipeline.removeIfExists(UserNotLoggedHandler.class);
        pipeline.removeIfExists(GetServerHandler.class);
        pipeline.removeIfExists(WebAppLoginViaInviteHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebLoginViaInviteMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length < 2) {
            log.error("Wrong income message format.");
            ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
            return;
        }

        String token = messageParts[0];
        String password = messageParts[1];

        if (token == null || password == null) {
            log.error("Empty token or password field.");
            ctx.writeAndFlush(json(message.id, "Empty token or password field."), ctx.voidPromise());
            return;
        }

        TokenUser tokenUser = holder.tokensPool.getUser(token);

        if (tokenUser == null) {
            log.error("Invitation expired or was used already for {}.", message.body);
            ctx.writeAndFlush(json(message.id, "Invitation expired or was used already."), ctx.voidPromise());
            return;
        }

        User user = holder.userDao.getByName(tokenUser.email, tokenUser.appName);
        if (user == null) {
            log.error("User {} not found.", tokenUser);
            ctx.writeAndFlush(json(message.id, "User not found."), ctx.voidPromise());
            return;
        }

        user.pass = password;
        user.status = UserStatus.Active;
        Organization org = holder.organizationDao.getOrgById(user.orgId);
        org.isActive = true;

        Version version = messageParts.length > 3
                ? new Version(messageParts[2], messageParts[3])
                : Version.UNKNOWN_VERSION;

        login(ctx, message.id, user, version, token);
    }

    private void login(ChannelHandlerContext ctx, int messageId, User user, Version version, String token) {
        DefaultChannelPipeline pipeline = (DefaultChannelPipeline) ctx.pipeline();
        cleanPipeline(pipeline);

        WebAppStateHolder appStateHolder = new WebAppStateHolder(user);
        pipeline.addLast("AWebAppHandler", new WebAppHandler(holder, appStateHolder));

        Channel channel = ctx.channel();

        Session session = holder.sessionDao.getOrCreateSessionByUser(appStateHolder.userKey, channel.eventLoop());
        if (session.initialEventLoop != channel.eventLoop()) {
            log.debug("Re registering websocket app channel. {}", ctx.channel());
            ReregisterChannelUtil.reRegisterChannel(ctx, session, channelFuture ->
                    completeLogin(channelFuture.channel(), session, user, messageId, version, token));
        } else {
            completeLogin(channel, session, user, messageId, version, token);
        }
    }

    private void completeLogin(Channel channel, Session session, User user, int msgId, Version version, String token) {
        user.lastLoggedIP = IPUtils.getIp(channel.remoteAddress());
        user.lastLoggedAt = System.currentTimeMillis();

        session.addWebChannel(channel);
        channel.writeAndFlush(makeUTF8StringMessage(WEB_LOGIN_VIA_INVITE,
                msgId, user.toString()), channel.voidPromise());
        holder.tokensPool.removeToken(token);

        log.info("{} {}-app ({}) joined via invite.", user.email, user.appName, version);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        handleGeneralException(ctx, cause);
    }

}
