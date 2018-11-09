package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.key.DevicePropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class HardwareSyncLogic {

    private HardwareSyncLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx,
                                       HardwareStateHolder state,
                                       StringMessage message) {
        Device device = state.device;

        if (message.body.length() == 0) {
            syncAll(ctx, message.id, device);
        } else {
            syncSpecificPins(ctx, message.body, message.id, device);
        }
    }

    private static void syncAll(ChannelHandlerContext ctx, int msgId, Device device) {
        for (Map.Entry<DeviceStorageKey, PinStorageValue> entry : device.pinStorage.values.entrySet()) {
            DeviceStorageKey key = entry.getKey();
            if (!(key instanceof DevicePropertyStorageKey) && ctx.channel().isWritable()) {
                for (String value : entry.getValue().values()) {
                    String body = key.makeHardwareBody(value);
                    ctx.write(makeUTF8StringMessage(HARDWARE, msgId, body), ctx.voidPromise());
                }
            }
        }

        ctx.flush();
    }

    //message format is "vr 22 33"
    //return specific widget state
    private static void syncSpecificPins(ChannelHandlerContext ctx, String messageBody,
                                         int msgId, Device device) {
        String[] bodyParts = messageBody.split(StringUtils.BODY_SEPARATOR_STRING);

        if (bodyParts.length < 2 || bodyParts[0].isEmpty()) {
            ctx.writeAndFlush(illegalCommand(msgId), ctx.voidPromise());
            return;
        }

        PinType pinType = PinType.getPinType(bodyParts[0].charAt(0));

        if (StringUtils.isReadOperation(bodyParts[0])) {
            for (int i = 1; i < bodyParts.length; i++) {
                short pin = NumberUtil.parsePin(bodyParts[i]);
                if (ctx.channel().isWritable()) {
                    PinStorageValue pinStorageValue = device.pinStorage.get(pin, pinType);
                    if (pinStorageValue != null) {
                        for (String value : pinStorageValue.values()) {
                            String body = DataStream.makeHardwareBody(pinType, pin, value);
                            ctx.write(makeUTF8StringMessage(HARDWARE, msgId, body), ctx.voidPromise());
                        }
                    }
                }
            }
            ctx.flush();
        }
    }

}
