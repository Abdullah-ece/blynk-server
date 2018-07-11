package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebDeleteOrganizationLogic {

    private static final Logger log = LogManager.getLogger(WebDeleteOrganizationLogic.class);

    private final OrganizationDao organizationDao;

    public WebDeleteOrganizationLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int orgId = Integer.parseInt(message.body);

        User user = state.user;
        if (orgId == OrganizationDao.DEFAULT_ORGANIZATION_ID) {
            log.error("Delete operation for initial organization (orgId = 1) is not allowed for {}.", user.email);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        if (!user.isSuperAdmin()) {
            log.error("User {} is not superadmin and tries to delete the org {}.", user.email, orgId);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        Organization orgToDelete = organizationDao.getOrgById(orgId);
        if (orgToDelete == null) {
            log.error("Organization for removal for {} not found.", user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        if (!organizationDao.delete(orgId)) {
            log.error("Wasn't able to remove organization with orgId {} for {}.", orgId, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
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
