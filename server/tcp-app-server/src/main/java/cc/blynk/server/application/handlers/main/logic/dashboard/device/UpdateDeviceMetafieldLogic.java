package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandBodyException;
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

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            log.error("Body '{}' is wrong for update metafield for {}", message.body, user.email);
            throw new IllegalCommandException("Wrong income message format for update metafield.");
        }

        int deviceId = Integer.parseInt(split[0]);
        String metafieldString = split[1];

        MetaField[] metaFields;
        //https://github.com/blynkkk/knight/issues/1498
        if (metafieldString.startsWith("{")) {
            metaFields = new MetaField[] {
                    JsonParser.parseMetafield(metafieldString, message.id)
            };
        } else {
            metaFields = JsonParser.readAny(metafieldString, MetaField[].class);
            if (metaFields == null) {
                throw new IllegalCommandBodyException("Error parsing metafields batch.");
            }
        }

        for (MetaField metaField : metaFields) {
            metaField.validate();
        }

        Device device = holder.deviceDao.getByIdOrThrow(deviceId);

        log.trace("Updating metafield {} for device {} and user {}.", metafieldString, deviceId, user.email);
        device.updateMetafields(metaFields);
        device.metadataUpdatedBy = user.email;
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
