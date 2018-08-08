package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.server.internal.WebByteBufUtil.userHasNoAccessToOrg;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebUpdateOrganizationLogic {

    private static final Logger log = LogManager.getLogger(WebUpdateOrganizationLogic.class);

    private final OrganizationDao organizationDao;

    public WebUpdateOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
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

        if (!organizationDao.hasAccess(user, newOrganization.id)) {
            log.error("User {} tries to update organization he has no access.", user.email);
            ctx.writeAndFlush(userHasNoAccessToOrg(message.id), ctx.voidPromise());
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
        createProductsFromParentOrg(newOrganization.id, newOrganization.name, addedProducts);

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
                        organizationDao.deleteProduct(org, product.id);
                        log.debug("Product was removed.");
                    } catch (ForbiddenWebException e) {
                        log.debug("Cannot delete product. {}", e.getMessage());
                    }

                }
            }
        }
    }

    private void createProductsFromParentOrg(int orgId, String orgName, int[] selectedProducts) {
        for (int productId : selectedProducts) {
            if (organizationDao.hasNoProductWithParent(orgId, productId)) {
                log.debug("Cloning product for org {} and parentProductId {}.", orgName, productId);
                Product parentProduct = organizationDao.getProductByIdOrThrow(productId);
                Product newProduct = new Product(parentProduct);
                newProduct.parentId = parentProduct.id;
                organizationDao.createProduct(orgId, newProduct);
            } else {
                log.debug("Already has product for org {} with product parent id {}.", orgName, productId);
            }
        }
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.isEmptyName();
    }
}
