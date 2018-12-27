package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_ORG_EDIT;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebEditOwnOrganizationLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebEditOwnOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return OWN_ORG_EDIT;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        OrganizationDTO orgDTO = JsonParser.parseOrgDTO(message.body, message.id);

        User user = state.user;
        if (isEmpty(orgDTO)) {
            log.error("Organization is empty for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Organization is empty."), ctx.voidPromise());
            return;
        }

        int orgId = state.selectedOrgId;
        if (user.orgId != orgId) {
            log.error("You can't edit another organization from this view.");
            throw new NoPermissionException("You can't edit another organization from this view.");
        }

        Organization existingOrganization = organizationDao.getOrgById(orgId);
        if (existingOrganization == null) {
            log.error("Organization {} for {} not found.", orgId, user.email);
            ctx.writeAndFlush(json(message.id, "Organization not found."), ctx.voidPromise());
            return;
        }

        if (organizationDao.checkNameExists(orgId, orgDTO.name)) {
            log.error("Organization {} with this name already exists for {}", orgDTO, user.email);
            ctx.writeAndFlush(json(message.id, "Organization with this name already exists."), ctx.voidPromise());
            return;
        }

        existingOrganization.update(orgDTO);

        if (ctx.channel().isWritable()) {
            String orgString = new OrganizationDTO(existingOrganization).toString();
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, orgString),
                    ctx.voidPromise());
        }
    }

    private boolean isEmpty(OrganizationDTO organizationDTO) {
        return organizationDTO == null || organizationDTO.isEmptyName();
    }
}
