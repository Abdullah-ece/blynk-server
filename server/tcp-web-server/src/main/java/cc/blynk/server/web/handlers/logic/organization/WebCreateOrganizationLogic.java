package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebCreateOrganizationLogic {

    private static final Logger log = LogManager.getLogger(WebCreateOrganizationLogic.class);

    private final OrganizationDao organizationDao;

    public WebCreateOrganizationLogic(Holder holder) {
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

        newOrganization.parentId = user.orgId;

        Organization parentOrg = organizationDao.getOrgById(user.orgId);
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

        newOrganization = organizationDao.create(newOrganization);
        boolean isCreated = createProductsFromParentOrg(
                newOrganization.id, newOrganization.name, newOrganization.selectedProducts);

        if (isCreated) {
            if (ctx.channel().isWritable()) {
                String orgString = newOrganization.toString();
                ctx.writeAndFlush(makeUTF8StringMessage(message.command, message.id, orgString),
                        ctx.voidPromise());
            }
        } else {
            ctx.writeAndFlush(json(message.id, "Error creating new organization."), ctx.voidPromise());
        }
    }

    private boolean createProductsFromParentOrg(int orgId, String orgName, int[] selectedProducts) {
        for (int productId : selectedProducts) {
            if (organizationDao.hasNoProductWithParent(orgId, productId)) {
                log.debug("Cloning product for org {} (id={}) and parentProductId {}.", orgName, orgId, productId);
                Product parentProduct = organizationDao.getProductById(productId);
                if (parentProduct == null) {
                    log.error("Product with id {} not found.", productId);
                    return false;
                }
                Product newProduct = new Product(parentProduct);
                newProduct.parentId = parentProduct.id;
                organizationDao.createProduct(orgId, newProduct);
            } else {
                log.debug("Already has product for org {} with product parent id {}.", orgName, productId);
            }
        }
        return true;
    }

    private boolean isEmpty(Organization newOrganization) {
        return newOrganization == null || newOrganization.isEmptyName();
    }
}
