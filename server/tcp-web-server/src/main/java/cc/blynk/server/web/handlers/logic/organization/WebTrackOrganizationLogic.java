package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_SWITCH;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebTrackOrganizationLogic {

    private static final Logger log = LogManager.getLogger(WebTrackOrganizationLogic.class);

    private final OrganizationDao organizationDao;
    private final SessionDao sessionDao;

    public WebTrackOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.sessionDao = holder.sessionDao;
    }

    //we do override basic method, because this is very special handler.
    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int requestedOrgId = Integer.parseInt(message.body);
        int userOrgId = state.user.orgId;

        //user is not in own organization, so security checks are required here
        String email = state.user.email;
        if (userOrgId != requestedOrgId) {
            Role role = state.role;
            //no permission to switch orgs
            if (!role.canSwitchOrg()) {
                log.warn("{} tries to switch org, but doesn't have ORG_SWITCH permission.", email);
                throw new NoPermissionException(email, ORG_SWITCH);
            }
            if (!role.canViewOrg()) {
                log.warn("{} tries to switch org, but doesn't have ORG_VIEW permission.", email);
                throw new NoPermissionException(email, ORG_VIEW);
            }

            organizationDao.checkInheritanceAccess(email, userOrgId, requestedOrgId);
        }

        organizationDao.getOrgByIdOrThrow(requestedOrgId);
        sessionDao.moveToAnotherSession(ctx.channel(), state.selectedOrgId, requestedOrgId);
        state.selectedOrgId = requestedOrgId;
        log.trace("Selecting webapp org {} for {}.", requestedOrgId, email);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
