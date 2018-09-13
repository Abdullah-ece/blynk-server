package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.UserInviteDTO;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationUsersLogic;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;
import static cc.blynk.utils.StringUtils.split2;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class WebUpdateUserInfoLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationUsersLogic.class);

    private final UserDao userDao;

    public WebUpdateUserInfoLogic(Holder holder) {
        this.userDao = holder.userDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        User user = state.user;
        if (split.length < 2) {
            log.debug("Wrong update user info request {} for {}.", message.body, user.email);
            ctx.writeAndFlush(json(message.id, "Wrong update user info request."), ctx.voidPromise());
            return;
        }

        int orgId = Integer.parseInt(split[0]);
        UserInviteDTO userInviteDTO = JsonParser.readAny(split[1], UserInviteDTO.class);

        if (userInviteDTO == null || userInviteDTO.isNotValid()) {
            log.error("Bad data for user info update for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Bad data for user info update."), ctx.voidPromise());
            return;
        }

        //todo should be for admins only
        if (!user.isSuperAdmin()) {
            if (orgId != user.orgId) {
                log.warn("User {} tries to access organization he has no access.", user.email);
                ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
                return;
            }
        }

        User userToUpdate = userDao.getByName(userInviteDTO.name, user.appName);

        log.info("Updating {} user for .", userToUpdate.email, user.email);
        userToUpdate.name = userInviteDTO.name;
        userToUpdate.setRole(userInviteDTO.roleId);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
