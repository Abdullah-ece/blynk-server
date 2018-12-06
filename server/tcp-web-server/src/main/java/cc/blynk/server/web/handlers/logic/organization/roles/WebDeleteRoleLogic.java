package cc.blynk.server.web.handlers.logic.organization.roles;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.PermissionsTable;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.11.18.
 */
public class WebDeleteRoleLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;

    public WebDeleteRoleLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canDeleteRole();
    }

    @Override
    public int getPermission() {
        return PermissionsTable.ROLE_DELETE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        String[] messageParts = split2(message.body);

        if (messageParts.length != 2) {
            throw new JsonException("Delete role command body is wrong.");
        }

        int orgId = Integer.parseInt(messageParts[0]);
        int roleId = Integer.parseInt(messageParts[1]);

        log.debug("{} deletes role {} for orgId {}.", state.user.email, roleId, orgId);
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        org.deleteRole(roleId);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
