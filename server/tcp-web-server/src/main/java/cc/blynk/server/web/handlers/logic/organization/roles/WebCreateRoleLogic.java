package cc.blynk.server.web.handlers.logic.organization.roles;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.RoleDTO;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.PermissionsTable;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.11.18.
 */
public final class WebCreateRoleLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebCreateRoleLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canCreateRole();
    }

    @Override
    public int getPermission() {
        return PermissionsTable.ROLE_CREATE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = state.selectedOrgId;
        RoleDTO roleDTO = JsonParser.readAny(message.body, RoleDTO.class);
        if (roleDTO == null) {
            throw new JsonException("Could not parse the role.");
        }

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);

        log.debug("{} creates new role {} for {}.", state.user.email, roleDTO, orgId);
        Role role = new Role(org.getIdForRole(), roleDTO.name, roleDTO.permissions1, roleDTO.permissions2);
        org.addRole(role);

        String roleString = role.toString();
        ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, roleString), ctx.voidPromise());
    }

}
