package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.AppStateHolder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.IdNameDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.09.18.
 */
public final class GetDevicesByReferenceMetafieldLogic {

    private static final Logger log = LogManager.getLogger(GetDevicesByReferenceMetafieldLogic.class);

    private GetDevicesByReferenceMetafieldLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       AppStateHolder state, StringMessage message) {
        String[] split = StringUtils.split2(message.body);
        int deviceId = Integer.parseInt(split[0]);
        Device device = holder.deviceDao.getByIdOrThrow(deviceId);

        int metafieldId = Integer.parseInt(split[1]);
        MetaField metaField = device.findMetaFieldByIdOrThrow(metafieldId);

        if (!(metaField instanceof DeviceReferenceMetaField)) {
            log.debug("Wrong metafield type {} for {}", metaField, state.user.email);
            throw new IllegalCommandException("Metafield is not DeviceReferenceMetaField.");
        }

        DeviceReferenceMetaField deviceReferenceMetaField = (DeviceReferenceMetaField) metaField;
        List<IdNameDTO> result = new ArrayList<>();
        for (int productId : deviceReferenceMetaField.selectedProductIds) {
            List<Device> devices = holder.deviceDao.getAllByProductId(productId);
            for (Device tempDevice : devices) {
                result.add(new IdNameDTO(tempDevice));
            }
        }

        String jsonResponse = JsonParser.toJson(result);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(
                            MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD, message.id, jsonResponse), ctx.voidPromise());
        }
    }

}
