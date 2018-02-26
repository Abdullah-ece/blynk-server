package cc.blynk.server.application.handlers.main.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.WebAppHandler;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.appllication.LoginMessage;
import cc.blynk.server.handlers.DefaultReregisterHandler;
import cc.blynk.server.handlers.common.UserNotLoggedHandler;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.IPUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;

import static cc.blynk.server.core.protocol.handlers.DefaultExceptionHandler.handleGeneralException;
import static cc.blynk.server.internal.CommonByteBufUtil.facebookUserLoginWithPass;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.notAuthenticated;
import static cc.blynk.server.internal.CommonByteBufUtil.notRegistered;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
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
public class WebAppLoginHandler extends SimpleChannelInboundHandler<LoginMessage>
        implements DefaultReregisterHandler {

    private static final Logger log = LogManager.getLogger(WebAppLoginHandler.class);

    private final Holder holder;

    public WebAppLoginHandler(Holder holder) {
        this.holder = holder;
    }

    private static void cleanPipeline(ChannelPipeline pipeline) {
        try {
            //common handlers for websockets and app pipeline
            pipeline.remove(WebAppLoginHandler.class);
            pipeline.remove(UserNotLoggedHandler.class);
            pipeline.remove(GetServerHandler.class);
        } catch (NoSuchElementException e) {
            //this case possible when few login commands come at same time to different threads
            //just do nothing and ignore.
            //https://github.com/blynkkk/blynk-server/issues/224
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) throws Exception {
        String[] messageParts = message.body.split(BODY_SEPARATOR_STRING);

        if (messageParts.length < 2) {
            log.error("Wrong income message format.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String email = messageParts[0].toLowerCase();

        Version version = messageParts.length > 3
                ? new Version(messageParts[2], messageParts[3])
                : Version.UNKNOWN_VERSION;

        String appName =  messageParts.length > 4 ? messageParts[4] : AppNameUtil.BLYNK;

        blynkLogin(ctx, message.id, email, messageParts[1], version, appName);
    }

    private void blynkLogin(ChannelHandlerContext ctx, int msgId, String email, String pass,
                            Version version, String appName) {
        User user = holder.userDao.getByName(email, appName);

        if (user == null) {
            log.warn("User '{}' not registered. {}", email, ctx.channel().remoteAddress());
            ctx.writeAndFlush(notRegistered(msgId), ctx.voidPromise());
            return;
        }

        if (user.pass == null) {
            log.warn("Facebook user '{}' tries to login with pass. {}", email, ctx.channel().remoteAddress());
            ctx.writeAndFlush(facebookUserLoginWithPass(msgId), ctx.voidPromise());
            return;
        }

        if (!user.pass.equals(pass)) {
            log.warn("User '{}' credentials are wrong. {}", email, ctx.channel().remoteAddress());
            ctx.writeAndFlush(notAuthenticated(msgId), ctx.voidPromise());
            return;
        }

        login(ctx, msgId, user, version);
    }

    private void login(ChannelHandlerContext ctx, int messageId, User user, Version version) {
        ChannelPipeline pipeline = ctx.pipeline();
        cleanPipeline(pipeline);

        AppStateHolder appStateHolder = new AppStateHolder(user, version);
        pipeline.addLast("AWebAppHandler", new WebAppHandler(holder, appStateHolder));

        Channel channel = ctx.channel();

        Session session = holder.sessionDao.getOrCreateSessionByUser(appStateHolder.userKey, channel.eventLoop());
        if (session.initialEventLoop != channel.eventLoop()) {
            log.debug("Re registering websocket app channel. {}", ctx.channel());
            reRegisterChannel(ctx, session, channelFuture ->
                    completeLogin(channelFuture.channel(), session, user, messageId, version));
        } else {
            completeLogin(channel, session, user, messageId, version);
        }
    }

    private void completeLogin(Channel channel, Session session, User user, int msgId, Version version) {
        user.lastLoggedIP = IPUtils.getIp(channel.remoteAddress());
        user.lastLoggedAt = System.currentTimeMillis();

        session.addAppChannel(channel);
        channel.writeAndFlush(ok(msgId), channel.voidPromise());

        log.info("{} {}-app ({}) joined.", user.email, user.appName, version);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }

}
