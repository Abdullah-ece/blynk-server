package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebGetMetaFieldLogic {

    private static final Logger log = LogManager.getLogger(WebGetMetaFieldLogic.class);

    private WebGetMetaFieldLogic() {
    }

    public static void messageReceived(Holder holder,
                                       ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = StringUtils.split2(message.body);

        int deviceId = Integer.parseInt(split[0]);
        int metaFieldId = Integer.parseInt(split[1]);

        //todo refactor when permissions ready
        //todo check access for the device
        Device device = holder.deviceDao.getById(deviceId);
        if (device == null) {
            log.error("Device {} not found for {}.", deviceId, state.user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        MetaField metaField = device.findMetaFieldById(metaFieldId);
        if (metaField == null) {
            log.error("Metafield {} not found for device {} and user {}.", metaFieldId, deviceId, state.user.email);
            ctx.writeAndFlush(json(message.id, "Metafield with passed id not found."), ctx.voidPromise());
            return;
        }

        if (ctx.channel().isWritable()) {
            String devicesString = metaField.toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesString), ctx.voidPromise());
        }
    }

}
