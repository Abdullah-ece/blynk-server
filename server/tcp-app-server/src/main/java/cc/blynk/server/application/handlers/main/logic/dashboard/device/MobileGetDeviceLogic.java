package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceMobileDTO;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileGetDeviceLogic {

    private static final Logger log = LogManager.getLogger(MobileGetDeviceLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;

    public MobileGetDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, StringMessage message) {
        String[] split = StringUtils.split2(message.body);
        int deviceId = Integer.parseInt(split[0]);
        boolean needMetaFilter = split.length > 1 && Boolean.parseBoolean(split[1]);
        Device device = deviceDao.getByIdOrThrow(deviceId);

        Product product = organizationDao.getProductByIdOrThrow(device.productId);

        DeviceMobileDTO deviceDTO;
        if (needMetaFilter) {
            MetaField[] filtered = MetaField.filter(device.metaFields).toArray(new MetaField[0]);
            deviceDTO = new DeviceMobileDTO(device, product, filtered);
        } else {
            deviceDTO = new DeviceMobileDTO(device, product);
        }

        log.debug("Returning deviceId {} for mobile app.", device.id);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(MOBILE_GET_DEVICE, message.id, deviceDTO.toString()), ctx.voidPromise());
        }
    }

}
