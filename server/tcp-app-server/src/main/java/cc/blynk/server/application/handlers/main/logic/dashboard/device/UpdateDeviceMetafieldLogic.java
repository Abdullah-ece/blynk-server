package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.auth.AppStateHolder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.09.18.
 */
public final class UpdateDeviceMetafieldLogic {

    private static final Logger log = LogManager.getLogger(UpdateDeviceMetafieldLogic.class);

    private UpdateDeviceMetafieldLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       AppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new IllegalCommandException("Wrong income message format.");
        }

        int deviceId = Integer.parseInt(split[0]);
        String metafieldString = split[1];
        MetaField metaField = JsonParser.parseMetafield(metafieldString, message.id);

        Device device = holder.deviceDao.getById(deviceId);
        log.trace("Updating metafield {} for device {}.", metafieldString, deviceId);
        device.updateMetafield(metaField);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
