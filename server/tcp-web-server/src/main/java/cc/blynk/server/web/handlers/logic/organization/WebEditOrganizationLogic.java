package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_EDIT;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebEditOrganizationLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebEditOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public int getPermission() {
        return ORG_EDIT;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        Organization newOrganization = JsonParser.parseOrganization(message.body, message.id);

        User user = state.user;
        if (isEmpty(newOrganization)) {
            log.error("Organization is empty for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Organization is empty."), ctx.voidPromise());
            return;
        }

        Organization existingOrganization = organizationDao.getOrgById(newOrganization.id);
        if (existingOrganization == null) {
            log.error("Organization {} for {} not found.", newOrganization.id, user.email);
            ctx.writeAndFlush(json(message.id, "Organization not found."), ctx.voidPromise());
            return;
        }

        if (organizationDao.checkNameExists(newOrganization.id, newOrganization.name)) {
            log.error("Organization {} with this name already exists for {}", newOrganization, user.email);
            ctx.writeAndFlush(json(message.id, "Organization with this name already exists."), ctx.voidPromise());
            return;
        }

        existingOrganization.update(newOrganization);

        int[] addedProducts = ArrayUtil.substruct(
                newOrganization.selectedProducts, existingOrganization.selectedProducts);
        organizationDao.createProductsFromParentOrg(newOrganization.id, addedProducts);

        int[] removedProducts = ArrayUtil.substruct(
                existingOrganization.selectedProducts, newOrganization.selectedProducts);
        deleteRemovedProducts(newOrganization.id, newOrganization.name, removedProducts);

        existingOrganization.selectedProducts = newOrganization.selectedProducts;

        if (ctx.channel().isWritable()) {
            String orgString = JsonParser.toJson(existingOrganization);
            ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, orgString),
                    ctx.voidPromise());
        }
    }

    private void deleteRemovedProducts(int orgId, String orgName, int[] deletedProducts) {
        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        for (int parentProductId : deletedProducts) {
            log.debug("Deleting product for org {} and parentProductId {}.", orgName, parentProductId);
            for (Product product : org.products) {
                if (product.parentId == parentProductId) {
                    try {
                        int productId = product.id;
                        if (deviceDao.productHasDevices(productId)) {
                            log.error("You are not allowed to remove product with devices.");
                            throw new ForbiddenWebException("You are not allowed to remove product with devices.");
                        }
                        org.deleteProduct(productId);
                        log.debug("Product was removed.");
                    } catch (ForbiddenWebException e) {
                        log.debug("Cannot delete product. {}", e.getMessage());
                    }

                }
            }
        }
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.isEmptyName();
    }
}
