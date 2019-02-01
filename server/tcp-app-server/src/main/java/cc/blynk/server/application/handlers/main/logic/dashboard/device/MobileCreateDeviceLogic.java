package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DEVICE;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileCreateDeviceLogic {

    private static final Logger log = LogManager.getLogger(MobileCreateDeviceLogic.class);

    private MobileCreateDeviceLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        String deviceString = message.body;

        if (deviceString.isEmpty()) {
            throw new JsonException("Income device message is empty.");
        }

        Device newDevice = JsonParser.parseDevice(deviceString, message.id);

        log.debug("Creating new device {}.", deviceString);

        if (newDevice.isNotValid()) {
            throw new JsonException("Income device message is not valid.");
        }

        int orgId = state.user.orgId;
        Organization org = holder.organizationDao.getOrgByIdOrThrow(orgId);
        Product product = holder.organizationDao.assignToOrgAndAddDevice(org, newDevice);
        holder.deviceDao.create(org, state.user.email, product, newDevice);

        log.debug("Device for orgId {} created {}.", orgId, newDevice);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(
                    makeUTF8StringMessage(MOBILE_CREATE_DEVICE, message.id, newDevice.toString()), ctx.voidPromise());
        }
    }

}
