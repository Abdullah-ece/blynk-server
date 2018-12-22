package cc.blynk.server.application.handlers.sharing.auth;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.MobileGetServerHandler;
import cc.blynk.server.application.handlers.main.auth.MobileLoginHandler;
import cc.blynk.server.application.handlers.main.auth.MobileRegisterHandler;
import cc.blynk.server.application.handlers.sharing.MobileShareHandler;
import cc.blynk.server.common.handlers.UserNotLoggedHandler;
import cc.blynk.server.core.dao.SharedTokenValue;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.appllication.sharing.ShareLoginMessage;
import cc.blynk.server.core.session.mobile.Version;
import cc.blynk.server.internal.ReregisterChannelUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * Handler responsible for managing apps sharing login messages.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class MobileShareLoginHandler extends SimpleChannelInboundHandler<ShareLoginMessage> {

    private static final Logger log = LogManager.getLogger(MobileShareLoginHandler.class);

    private final Holder holder;

    public MobileShareLoginHandler(Holder holder) {
        this.holder = holder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ShareLoginMessage message) {
        String[] messageParts = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        if (messageParts.length < 2) {
            log.error("Wrong income message format.");
            ctx.writeAndFlush(json(message.id, "Wrong income message format."), ctx.voidPromise());
        } else {
            //var uid = messageParts.length == 5 ? messageParts[4] : null;
            var version = messageParts.length > 3
                    ? new Version(messageParts[2], messageParts[3])
                    : Version.UNKNOWN_VERSION;
            appLogin(ctx, message.id, messageParts[0], messageParts[1], version);
        }
    }

    private void appLogin(ChannelHandlerContext ctx, int messageId, String email,
                          String token, Version version) {
        ///.trim() is not used for back compatibility
        String userName = email.toLowerCase();

        //todo move shared token manager away
        SharedTokenValue tokenValue = holder.sharedTokenManager.getUserByToken(token);

        if (tokenValue == null || !tokenValue.user.email.equals(userName)) {
            log.debug("Share token is invalid. User : {}, token {}, {}",
                    userName, token, ctx.channel().remoteAddress());
            throw new JsonException("Share token is invalid.");
        }

        User user = tokenValue.user;
        int dashId = tokenValue.dashId;

        DashBoard dash = user.profile.getDashById(dashId);
        if (!dash.isShared) {
            log.debug("Project is not shared. User : {}, token {}, {}",
                    userName, token, ctx.channel().remoteAddress());
            throw new JsonException("Project is not shared.");
        }

        cleanPipeline(ctx.pipeline());
        Role role = holder.organizationDao.getRole(user);
        MobileShareStateHolder mobileShareStateHolder =
                new MobileShareStateHolder(user, role, version, token, dashId);
        ctx.pipeline().addLast("AAppSHareHandler", new MobileShareHandler(holder, mobileShareStateHolder));

        Session session = holder.sessionDao.getOrCreateSessionForOrg(
                user.orgId, ctx.channel().eventLoop());

        if (session.isSameEventLoop(ctx)) {
            completeLogin(ctx.channel(), session, user.email, messageId);
        } else {
            log.debug("Re registering app channel. {}", ctx.channel());
            ReregisterChannelUtil.reRegisterChannel(ctx, session, channelFuture ->
                    completeLogin(channelFuture.channel(), session, user.email, messageId));
        }
    }

    private void completeLogin(Channel channel, Session session, String userName, int msgId) {
        session.addAppChannel(channel);
        channel.writeAndFlush(ok(msgId), channel.voidPromise());
        log.info("Shared {} app joined.", userName);
    }

    private void cleanPipeline(ChannelPipeline pipeline) {
        pipeline.remove(this);
        pipeline.remove(UserNotLoggedHandler.class);
        pipeline.remove(MobileRegisterHandler.class);
        pipeline.remove(MobileLoginHandler.class);
        pipeline.remove(MobileGetServerHandler.class);
    }

}
