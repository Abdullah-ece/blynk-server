package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_EDIT_USERS;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 */
public final class WebEditUserInfoLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final UserDao userDao;

    public WebEditUserInfoLogic(Holder holder) {
        this.userDao = holder.userDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canEditOrgUsers();
    }

    @Override
    public int getPermission() {
        return ORG_EDIT_USERS;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        UserInviteDTO userInviteDTO;
        if (split.length == 2) {
            userInviteDTO = JsonParser.readAny(split[1], UserInviteDTO.class);
        } else  {
            userInviteDTO = JsonParser.readAny(split[0], UserInviteDTO.class);
        }

        User user = state.user;
        int orgId = state.selectedOrgId;

        if (userInviteDTO == null || userInviteDTO.isNotValid()) {
            log.error("Bad data for user info update for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Bad data for user info update."), ctx.voidPromise());
            return;
        }

        User userToUpdate = userDao.getByName(userInviteDTO.email);

        if (orgId != userToUpdate.orgId) {
            throw new NoPermissionException("User is from another organization.");
        }

        log.info("Updating {} user for .", userToUpdate.email, user.email);
        userToUpdate.update(userInviteDTO);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
