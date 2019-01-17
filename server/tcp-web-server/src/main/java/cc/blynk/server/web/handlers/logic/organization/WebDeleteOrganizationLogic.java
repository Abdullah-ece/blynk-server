package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DELETE;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebDeleteOrganizationLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebDeleteOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public int getPermission() {
        return ORG_DELETE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgIdForRemoval = Integer.parseInt(message.body);

        User user = state.user;
        if (orgIdForRemoval == OrganizationDao.DEFAULT_ORGANIZATION_ID) {
            log.error("Delete operation for initial organization (orgId = 1) is not allowed for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Base organization can't be removed."), ctx.voidPromise());
            return;
        }

        organizationDao.checkInheritanceAccess(user, orgIdForRemoval);

        Organization orgToDelete = organizationDao.getOrgById(orgIdForRemoval);
        if (orgToDelete == null) {
            log.error("Organization for removal for {} not found.", user.email);
            ctx.writeAndFlush(json(message.id, "Organization for removal not found."), ctx.voidPromise());
            return;
        }

        if (organizationDao.hasChilds(orgIdForRemoval)) {
            log.error("{} are not allowed to remove organization with sub organizations.", user.email);
            ctx.writeAndFlush(json(message.id,
                    "You are not allowed to remove organization with sub organizations."), ctx.voidPromise());
            return;
        }

        if (orgToDelete.parentId != state.selectedOrgId) {
            log.error("Removed organization should be a child of current organization. User {}.",
                    user.email);
            ctx.writeAndFlush(json(message.id,
                    "Removed organization should be a child of current organization."), ctx.voidPromise());
            return;
        }

        if (!organizationDao.delete(orgIdForRemoval)) {
            log.error("Wasn't able to remove organization with orgId {} for {}.", orgIdForRemoval, user.email);
            ctx.writeAndFlush(json(message.id, "Can't delete organization."), ctx.voidPromise());
            return;
        }

        deviceDao.deleteAllTokensForOrg(orgIdForRemoval);

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
