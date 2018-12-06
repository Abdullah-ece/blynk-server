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
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.11.18.
 */
public class WebEditRoleLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;

    public WebEditRoleLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canEditRole();
    }

    @Override
    public int getPermission() {
        return PermissionsTable.ROLE_EDIT;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        String[] messageParts = split2(message.body);

        if (messageParts.length != 2) {
            throw new JsonException("Update role command body is wrong.");
        }

        int orgId = Integer.parseInt(messageParts[0]);
        RoleDTO roleDTO = JsonParser.readAny(messageParts[1], RoleDTO.class);
        if (roleDTO == null) {
            throw new JsonException("Could not parse the role.");
        }

        log.debug("{} updates role {} for orgId {}.", state.user.email, roleDTO, orgId);
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Role role = roleDTO.toRole();
        org.updateRole(role);

        if (ctx.channel().isWritable()) {
            String roleString = role.toString();
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, roleString),
                    ctx.voidPromise());
        }
    }

}
