package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebUpdateProductLogic {

    private static final Logger log = LogManager.getLogger(WebUpdateProductLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebUpdateProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        ProductAndOrgIdDTO productAndOrgIdDTO = JsonParser.readAny(message.body, ProductAndOrgIdDTO.class);

        User user = state.user;
        if (productAndOrgIdDTO == null) {
            log.error("Couldn't parse passed product for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Couldn't parse passed product."), ctx.voidPromise());
            return;
        }

        Product product = productAndOrgIdDTO.product;

        if (product == null || product.notValid()) {
            log.error("Product is empty or has no name {} for {}.", product, user.email);
            ctx.writeAndFlush(json(message.id, "Product is empty or has no name."), ctx.voidPromise());
            return;
        }

        if (product.isSubProduct()) {
            log.error("Product {} is reference and can be updated only via parent product. {}.",
                    product.id, user.email);
            ctx.writeAndFlush(json(message.id,
                    "Sub Org can't do anything with the Product Templates created by Meta Org."), ctx.voidPromise());
            return;
        }

        Organization organization = organizationDao.getOrgById(productAndOrgIdDTO.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            ctx.writeAndFlush(json(message.id, "Cannot find organization."), ctx.voidPromise());
            return;
        }

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

        Product existingProduct = organizationDao.getProduct(productAndOrgIdDTO.orgId, product.id);

        if (!existingProduct.webDashboard.equals(product.webDashboard)) {
            log.debug("Dashboard was changed. Updating all devices for {}.", user.email);
            List<Device> devices = deviceDao.getAllByProductId(product.id);
            long now = System.currentTimeMillis();
            for (Device device : devices) {
                device.webDashboard.update(product.webDashboard);
                device.updatedAt = now;
            }
            log.debug("{} devices updated with new dashboard.", devices.size());
        }

        existingProduct.update(product);
        log.debug("Product {} successfully updated for {}.", product, user.email);

        if (ctx.channel().isWritable()) {
            String productString = existingProduct.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
