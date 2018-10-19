package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationUsersLogic;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class WebCanInviteUserLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationUsersLogic.class);

    private final UserDao userDao;

    public WebCanInviteUserLogic(Holder holder) {
        this.userDao = holder.userDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String userEMailToInvite = message.body;

        User userToInvite = userDao.getByName(userEMailToInvite);
        if (userToInvite == null) {
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            if (userToInvite.status == UserStatus.Active) {
                log.debug("User {}-{} already registered in the system for invite.",
                        userEMailToInvite, state.user.orgId);
                ctx.writeAndFlush(json(message.id, userEMailToInvite + " already registered in the system."),
                        ctx.voidPromise());
            } else {
                log.debug("Invitation for {} was already sent.", userEMailToInvite);
                ctx.writeAndFlush(json(message.id, "Invitation for " + userEMailToInvite + " was already sent."),
                        ctx.voidPromise());
            }
        }
    }

}
