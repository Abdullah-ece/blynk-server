package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
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
public final class WebUpdateProductLogic {

    private static final Logger log = LogManager.getLogger(WebUpdateProductLogic.class);

    private WebUpdateProductLogic() {
    }

    public static void messageReceived(Holder holder,
                                       ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        ProductAndOrgIdDTO productAndOrgIdDTO = JsonParser.readAny(message.body, ProductAndOrgIdDTO.class);

        User user = state.user;
        if (productAndOrgIdDTO == null) {
            log.error("Couldn't parse passed product for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Couldn't parse passed product."), ctx.voidPromise());
            return;
        }

        Product product = productAndOrgIdDTO.product;

        if (product == null) {
            log.error("Product is empty for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Product is empty."), ctx.voidPromise());
            return;
        }

        product.validate();

        if (product.isSubProduct()) {
            log.error("Product {} is reference and can be updated only via parent product. {}.",
                    product.id, user.email);
            ctx.writeAndFlush(json(message.id,
                    "Sub Org can't do anything with the Product Templates created by Meta Org."), ctx.voidPromise());
            return;
        }

        Organization organization = holder.organizationDao.getOrgByIdOrThrow(productAndOrgIdDTO.orgId);

        if (organization.isSubOrg()) {
            log.error("User {} can't update products for sub organizations.", user.email);
            ctx.writeAndFlush(json(message.id, "User can't create products for sub organizations."), ctx.voidPromise());
            return;
        }

        if (!organization.isValidProductName(product)) {
            log.error("Organization {} already has product with name {} for {}.",
                    organization.name, product.name, user.email);
            ctx.writeAndFlush(json(message.id, "Organization already has product with this name."), ctx.voidPromise());
            return;
        }

        Product existingProduct = organization.getProductOrThrow(product.id);
        existingProduct.update(product);

        int[] subProductIds = holder.organizationDao.subProductIds(productAndOrgIdDTO.orgId, product.id);
        for (int productId : subProductIds) {
            Product subProduct = holder.organizationDao.getProductById(productId);
            if (subProduct != null) {
                subProduct.update(product);
            }
        }
        log.debug("Product with id {} and {} subProducts successfully updated for {}.",
                product.id, subProductIds.length, user.email);

        if (ctx.channel().isWritable()) {
            String productString = existingProduct.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }


}
