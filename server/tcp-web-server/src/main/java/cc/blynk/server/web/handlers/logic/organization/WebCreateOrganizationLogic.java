package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_CREATE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_PRODUCTS;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebCreateOrganizationLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;

    public WebCreateOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    @Override
    public int getPermission() {
        return ORG_CREATE;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int orgId = state.selectedOrgId;
        Organization newOrganization = JsonParser.parseOrganization(split[1], message.id);

        User user = state.user;
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Organization is empty."), ctx.voidPromise());
            return;
        }

        newOrganization.parentId = orgId;
        //products are created via selectedProducts field
        newOrganization.products = EMPTY_PRODUCTS;

        Organization parentOrg = organizationDao.getOrgById(orgId);
        if (parentOrg == null) {
            log.error("Organization for {} not found.", user.email);
            ctx.writeAndFlush(json(message.id, "Organization not found."), ctx.voidPromise());
            return;
        }

        if (!parentOrg.canCreateOrgs) {
            log.debug("Organization of {} cannot have sub organizations.", user.email);
            ctx.writeAndFlush(json(message.id, "Organization cannot have sub organizations."), ctx.voidPromise());
            return;
        }

        newOrganization.roles = Organization.createDefaultRoles(false);
        if (newOrganization.roles.length == 0) {
            log.debug("Parent org {} cannot has 0 roles.", orgId);
            ctx.writeAndFlush(json(message.id, "Parent organization has 0 roles."), ctx.voidPromise());
            return;
        }

        if (newOrganization.selectedProducts.length == 0) {
            newOrganization.selectedProducts = parentOrg.selectedProducts;
        }

        newOrganization = organizationDao.create(newOrganization);
        organizationDao.createProductsFromParentOrg(newOrganization.id, newOrganization.selectedProducts);

        if (ctx.channel().isWritable()) {
            String orgString = newOrganization.toString();
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, orgString),
                    ctx.voidPromise());
        }
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.isEmptyName();
    }
}
