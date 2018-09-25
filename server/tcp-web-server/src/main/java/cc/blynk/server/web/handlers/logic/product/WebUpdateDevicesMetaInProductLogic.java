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

        if (updatedProduct == null || updatedProduct.notValid()) {
            log.error("Product is empty or has no name {} for {}.", updatedProduct, user.email);
            ctx.writeAndFlush(json(message.id, "Product is empty or has no name."), ctx.voidPromise());
            return;
        }

        Product existingProduct = organizationDao.getProductOrThrow(productAndOrgIdDTO.orgId, updatedProduct.id);

        if (updatedProduct.notValid()) {
            log.error("Product {} is not valid for {}.", updatedProduct, user.email);
            ctx.writeAndFlush(json(message.id, "Product is not valid."), ctx.voidPromise());
            return;
        }

        existingProduct.update(updatedProduct);

        List<Device> devices = deviceDao.getAllByProductId(updatedProduct.id);
        long now = System.currentTimeMillis();
        for (Device device : devices) {
            device.updateMetaFields(updatedProduct.metaFields);

            MetaField[] addedMetaFields = ArrayUtil.substruct(updatedProduct.metaFields, device.metaFields)
                    .toArray(new MetaField[0]);
            device.addMetaFields(addedMetaFields);

            MetaField[] deletedMetaFields = ArrayUtil.substruct(device.metaFields, updatedProduct.metaFields)
                    .toArray(new MetaField[0]);
            device.deleteMetaFields(deletedMetaFields);

            device.metadataUpdatedAt = now;
            device.metadataUpdatedBy = user.email;
        }

        if (ctx.channel().isWritable()) {
            String productString = existingProduct.toString();
            StringMessage response = makeUTF8StringMessage(message.command, message.id, productString);
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }

}
