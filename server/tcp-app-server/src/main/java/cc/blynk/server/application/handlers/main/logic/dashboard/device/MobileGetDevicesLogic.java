package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceStatusDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.core.protocol.enums.Command.GET_DEVICES;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileGetDevicesLogic {

    private static final Logger log = LogManager.getLogger(MobileGetDevicesLogic.class);

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public MobileGetDevicesLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String devicesJson;

        List<Device> deviceList = deviceDao.getDevicesOwnedByUser(user.email);
        if (deviceList.size() == 0) {
            log.debug("No devices for user {}-{}.", user.email, user.orgId);
            devicesJson = "[]";
        } else {
            DeviceStatusDTO[] deviceStatusDTOS = DeviceStatusDTO.transform(deviceList);
            devicesJson = JsonParser.toJson(deviceStatusDTOS);
        }

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(GET_DEVICES, message.id, devicesJson), ctx.voidPromise());
        }
    }

}
