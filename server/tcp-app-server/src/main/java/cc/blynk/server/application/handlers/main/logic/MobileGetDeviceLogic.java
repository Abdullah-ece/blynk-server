package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class MobileGetDeviceLogic {

    private static final Logger log = LogManager.getLogger(MobileGetDeviceLogic.class);

    private MobileGetDeviceLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);
        Device device = holder.deviceDao.getById(deviceId);

        Product product = holder.organizationDao.getProductByIdOrThrow(device.productId);
        MetaField[] filtered = MetaField.filter(device.metaFields).toArray(new MetaField[0]);
        String response = new DeviceDTO(device, product, filtered).toString();
        log.debug("Returning deviceId {} fpr mobile app.", device.id);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(MOBILE_GET_DEVICE, message.id, response), ctx.voidPromise());
        }
    }

}
