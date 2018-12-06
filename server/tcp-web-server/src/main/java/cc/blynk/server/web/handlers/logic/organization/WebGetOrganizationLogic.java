package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.core.PermissionBasedLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrganizationLogic implements PermissionBasedLogic {

    private final OrganizationDao organizationDao;
    private final WebGetOwnOrganizationLogic webGetOwnOrganizationLogic;

    public WebGetOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.webGetOwnOrganizationLogic = new WebGetOwnOrganizationLogic(holder);
    }

    @Override
    public boolean hasPermission(Role role) {
        return role.canViewOrg();
    }

    @Override
    public int getPermission() {
        return ORG_VIEW;
    }

    @Override
    public void noPermissionAction(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage msg) {
        webGetOwnOrganizationLogic.messageReceived(ctx, state, msg);
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = "".equals(message.body) ? state.user.orgId : Integer.parseInt(message.body);

        //todo refactor when permissions ready
        Organization organization = organizationDao.getOrgByIdOrThrow(orgId);

        User user = state.user;
        if (!user.isSuperAdmin()) {
            if (orgId != user.orgId) {
                log.error("User {} tries to access organization he has no access.", user.email);
                ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
                return;
            }
        }

        String parentOrgName = organizationDao.getParentOrgName(organization.parentId);

        if (ctx.channel().isWritable()) {
            String orgString = new OrganizationDTO(organization, parentOrgName).toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, orgString), ctx.voidPromise());
        }
    }

}
