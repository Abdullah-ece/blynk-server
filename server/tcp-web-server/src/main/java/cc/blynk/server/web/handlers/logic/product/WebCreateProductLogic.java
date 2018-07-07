package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
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

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebCreateProductLogic {

    private static final Logger log = LogManager.getLogger(WebCreateProductLogic.class);

    private final OrganizationDao organizationDao;

    public WebCreateProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        ProductAndOrgIdDTO productAndOrgIdDTO = JsonParser.readAny(message.body, ProductAndOrgIdDTO.class);

        User user = state.user;
        if (productAndOrgIdDTO == null) {
            log.error("Wrong create product command for {}.", user.email);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        Product product = productAndOrgIdDTO.product;

        if (product == null || product.notValid()) {
            log.error("Product is empty or has no name {} for {}.", product, user.email);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        Organization organization = organizationDao.getOrgByIdOrThrow(productAndOrgIdDTO.orgId);

        if (organization.isSubOrg()) {
            log.error("User {} can't create products for sub organizations.", user.email);
            ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            return;
        }

        if (!organization.isValidProductName(product)) {
            log.error("Organization {} already has product with name {} for {}.",
                    organization.name, product.name, user.email);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        if (!product.isValidEvents()) {
            log.error("Events are not valid for the product {} for {}.", product.name, user.email);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        product = organizationDao.createProduct(productAndOrgIdDTO.orgId, product);
        log.debug("Product {} successfully created for {}.", product, user.email);

        if (ctx.channel().isWritable()) {
            String productString = product.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
