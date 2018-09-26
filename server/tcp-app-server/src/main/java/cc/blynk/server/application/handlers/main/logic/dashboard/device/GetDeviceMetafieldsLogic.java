package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.AppStateHolder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE_METAFIELDS;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.09.18.
 */
public final class GetDeviceMetafieldsLogic {

    private static final Logger log = LogManager.getLogger(GetDeviceMetafieldsLogic.class);

    private GetDeviceMetafieldsLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       AppStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);
        Device device = holder.deviceDao.getById(deviceId);
        List<MetaField> filtered = filter(device.metaFields);
        String response = JsonParser.toJson(filtered);

        log.debug("Returning {} metafields for deviceId {}.", filtered.size(), deviceId);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(MOBILE_GET_DEVICE_METAFIELDS, message.id, response), ctx.voidPromise());
        }
    }

    private static List<MetaField> filter(MetaField[] metaFields) {
        var resultList = new ArrayList<MetaField>();
        for (MetaField metaField : metaFields) {
            //if (metaField.includeInProvision) {
                resultList.add(metaField);
            //}
        }
        return resultList;
    }

}
