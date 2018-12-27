package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_INVITE_USERS;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 */
public final class WebCanInviteUserLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final UserDao userDao;

    public WebCanInviteUserLogic(Holder holder) {
        this.userDao = holder.userDao;
    }

    @Override
    public int getPermission() {
        return ORG_INVITE_USERS;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        if (message.body.isEmpty()) {
            throw new JsonException("Invalid email.");
        }

        String userEMailToInvite = message.body.trim().toLowerCase();
        User userToInvite = userDao.getByName(userEMailToInvite);
        if (userToInvite == null) {
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            if (userToInvite.status == UserStatus.Active) {
                log.debug("User {}-{} already registered in the system for invite.",
                        userEMailToInvite, userToInvite.orgId);
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
