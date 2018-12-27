package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetOwnOrganizationLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    WebGetOwnOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public boolean hasPermission(Role role) {
        //see own org is allowed to all
        return true;
    }

    @Override
    public int getPermission() {
        return -1;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = state.selectedOrgId;

        User user = state.user;
        if (orgId != user.orgId) {
            log.debug("User {} don't have permission to view another organizations.", user.email);
            throw new NoPermissionException(user.email, "view organization");
        }

        Organization organization = organizationDao.getOrgByIdOrThrow(orgId);

        String parentOrgName = organizationDao.getParentOrgName(organization.parentId);

        if (ctx.channel().isWritable()) {
            String orgString = new OrganizationDTO(organization, parentOrgName).toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, orgString), ctx.voidPromise());
        }
    }

}
