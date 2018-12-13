package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_SWITCH;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebTrackOrganizationLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebTrackOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canSwitchOrg();
    }

    @Override
    public int getPermission() {
        return ORG_SWITCH;
    }

    @Override
    //we do override basic method, because this is very special handler.
    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int requestedOrgId = Integer.parseInt(message.body);
        int userOrgId = state.orgId;

        if (hasPermission(state.role) || userOrgId == requestedOrgId) {
            organizationDao.getOrgByIdOrThrow(requestedOrgId);
            organizationDao.checkAccess(state.user.email, state.role, userOrgId, requestedOrgId);
            state.selectedOrgId = requestedOrgId;
            log.trace("Selecting webapp org {} for {}.", requestedOrgId, state.user.email);
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            noPermissionAction(ctx, state, message);
        }
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
    }
}
