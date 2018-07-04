package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationUsersLogic;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class CanInviteUserLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationUsersLogic.class);

    private final UserDao userDao;

    public CanInviteUserLogic(Holder holder) {
        this.userDao = holder.userDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String userEMailToInvite = message.body;
        if (userDao.contains(userEMailToInvite, state.userKey.appName)) {
            log.debug("User {}-{} already exists in system.", userEMailToInvite, state.userKey.appName);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
        } else {
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        }
    }

}
