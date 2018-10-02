package cc.blynk.server.web.handlers.logic.product;

import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import cc.blynk.utils.ArrayUtil;
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
public class WebUpdateDevicesMetaInProductLogic {

    private static final Logger log = LogManager.getLogger(WebUpdateDevicesMetaInProductLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebUpdateDevicesMetaInProductLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        ProductAndOrgIdDTO productAndOrgIdDTO = JsonParser.readAny(message.body, ProductAndOrgIdDTO.class);

        User user = state.user;
        if (productAndOrgIdDTO == null) {
            log.error("Wrong create product command for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Wrong create product command."), ctx.voidPromise());
            return;
        }

        Product updatedProduct = productAndOrgIdDTO.product;

        if (updatedProduct == null) {
            log.error("Product is empty for {}.", user.email);
            ctx.writeAndFlush(json(message.id, "Product is empty."), ctx.voidPromise());
            return;
        }

        updatedProduct.validate();

        Product existingProduct = organizationDao.getProductOrThrow(productAndOrgIdDTO.orgId, updatedProduct.id);

        int[] subProductIds = organizationDao.subProductIds(productAndOrgIdDTO.orgId, updatedProduct.id);

        WebDashboard changedWebDashboard = null;
        if (!existingProduct.webDashboard.equals(updatedProduct.webDashboard)) {
            log.debug("Dashboard was changed. Updating devices dashboard for {}.", user.email);
            changedWebDashboard = updatedProduct.webDashboard;
        }

        existingProduct.update(updatedProduct);

        updateDevice(updatedProduct.id, updatedProduct.metaFields, changedWebDashboard, user.email);

        for (int subProductId : subProductIds) {
            Product subProduct = organizationDao.getProductById(subProductId);
            if (subProduct != null) {
                subProduct.update(updatedProduct);
            }
            updateDevice(subProductId, updatedProduct.metaFields, changedWebDashboard, user.email);
        }

        if (ctx.channel().isWritable()) {
            String productString = existingProduct.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

    //todo optimize
    private void updateDevice(int productId, MetaField[] metaFields, WebDashboard webDashboard, String userEmail) {
        List<Device> devices = deviceDao.getAllByProductId(productId);
        log.debug("Updating {} devices meta for {}", devices.size(), userEmail);
        long now = System.currentTimeMillis();
        for (Device device : devices) {
            device.updateMetaFields(metaFields);

            MetaField[] addedMetaFields = ArrayUtil.substruct(metaFields, device.metaFields)
                    .toArray(new MetaField[0]);
            device.addMetaFields(addedMetaFields);

            MetaField[] deletedMetaFields = ArrayUtil.substruct(device.metaFields, metaFields)
                    .toArray(new MetaField[0]);
            device.deleteMetaFields(deletedMetaFields);

            if (webDashboard != null) {
                device.webDashboard.update(webDashboard);
            }

            device.updatedAt = now;
            device.metadataUpdatedAt = now;
            device.metadataUpdatedBy = userEmail;
        }
    }

}
