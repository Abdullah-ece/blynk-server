package cc.blynk.server.web.handlers.logic.organization.roles;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.PermissionsTable;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.PermissionBasedLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.11.18.
 */
public class WebGetRolesLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;

    public WebGetRolesLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewRole();
    }

    @Override
    public int getPermission() {
        return PermissionsTable.ROLE_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = Integer.parseInt(message.body);

        log.debug("{} gets roles orgId {}.", state.user.email, orgId);
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Role[] roles = org.roles;

        if (ctx.channel().isWritable()) {
            String roleString = JsonParser.toJson(roles);
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, roleString),
                    ctx.voidPromise());
        }
    }

}
