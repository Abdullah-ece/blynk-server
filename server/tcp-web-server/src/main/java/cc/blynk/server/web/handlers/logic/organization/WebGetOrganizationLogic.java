package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.OrganizationDTO;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetOrganizationLogic {

    private static final Logger log = LogManager.getLogger(WebGetOrganizationLogic.class);

    private final OrganizationDao organizationDao;

    public WebGetOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = Integer.parseInt(message.body);

        //todo refactor when permissions ready
        Organization organization = organizationDao.getOrgByIdOrThrow(orgId);

        User user = state.user;
        if (!user.isSuperAdmin()) {
            if (orgId != user.orgId) {
                log.error("User {} tries to access organization he has no access.", user.email);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }
        }

        String parentOrgName = null;
        if (organization.hasParentOrg()) {
            Organization parentOrg = organizationDao.getOrgById(organization.parentId);
            if (parentOrg != null && parentOrg.name != null) {
                parentOrgName = parentOrg.name;
            }
        }

        if (ctx.channel().isWritable()) {
            String orgString = new OrganizationDTO(organization, parentOrgName).toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(WEB_GET_ORG, message.id, orgString), ctx.voidPromise());
        }
    }

}
