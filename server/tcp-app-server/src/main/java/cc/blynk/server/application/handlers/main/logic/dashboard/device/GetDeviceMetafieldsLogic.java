package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.AppStateHolder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.GET_DEVICE_METAFIELDS;
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
        String response = JsonParser.toJson(device.metaFields);

        log.debug("Returning {} metafields for deviceId {}.", device.metaFields.length, deviceId);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(GET_DEVICE_METAFIELDS, message.id, response), ctx.voidPromise());
        }
    }

}
