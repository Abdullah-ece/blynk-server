package cc.blynk.server.web.handlers.logic.organization.users;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.PermissionBasedLogic;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW_USERS;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrgUsersLogic implements PermissionBasedLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrgUsersLogic.class);

    private final OrganizationDao organizationDao;
    private final UserDao userDao;

    public WebGetOrgUsersLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.userDao = holder.userDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewOrgUsers();
    }

    @Override
    public int getPermission() {
        return ORG_VIEW_USERS;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = Integer.parseInt(message.body);

        User user = state.user;
        if (!organizationDao.hasAccess(user, orgId)) {
            log.error("User {} tries to access organization he has no access.");
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
            return;
        }

        if (ctx.channel().isWritable()) {
            List<User> users = userDao.getUsersByOrgId(orgId, user.email);
            String usersString = JsonParser.toJson(users);
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, usersString),
                    ctx.voidPromise());
        }
    }

}
