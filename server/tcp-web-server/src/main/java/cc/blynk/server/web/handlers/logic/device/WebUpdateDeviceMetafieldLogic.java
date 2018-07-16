package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebUpdateDeviceMetafieldLogic {

    private static final Logger log = LogManager.getLogger(WebUpdateDeviceMetafieldLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;

    public WebUpdateDeviceMetafieldLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        User user = state.user;
        if (split.length < 2) {
            log.error("Body '{}' is wrong for update metafield for {}", message.body, user.email);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        int deviceId = Integer.parseInt(split[0]);

        Device existingDevice = deviceDao.getById(deviceId);
        organizationDao.verifyUserAccessToDevice(user, existingDevice);

        if (existingDevice == null) {
            log.error("Device with passed id {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        MetaField updatedMetaField = JsonParser.readAny(split[1], MetaField.class);

        if (updatedMetaField == null) {
            log.error("Couldn't parse meta {} for {}.", split[1], user.email);
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        int fieldIndex = existingDevice.findMetaFieldIndex(updatedMetaField.id);
        if (fieldIndex == -1) {
            log.error("MetaField with id {} not found for device id {} for {}.",
                    updatedMetaField.id, deviceId, user.email);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        MetaField[] updatedMetaFields = Arrays.copyOf(existingDevice.metaFields, existingDevice.metaFields.length);
        updatedMetaFields[fieldIndex] = updatedMetaField;
        existingDevice.metaFields = updatedMetaFields;
        existingDevice.metadataUpdatedAt = System.currentTimeMillis();
        existingDevice.metadataUpdatedBy = user.email;
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
