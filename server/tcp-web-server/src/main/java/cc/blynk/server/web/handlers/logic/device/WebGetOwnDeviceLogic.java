package cc.blynk.server.web.handlers.logic.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetOwnDeviceLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    WebGetOwnDeviceLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public int getPermission() {
        return OWN_DEVICES_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        int deviceId = Integer.parseInt(split[1]);

        User user = state.user;
        DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
        if (deviceValue == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Device not found."), ctx.voidPromise());
            return;
        }

        organizationDao.checkInheritanceAccess(user.email, user.orgId, deviceValue.orgId);

        Device device = deviceValue.device;
        if (!state.role.canViewOrgDevices() && !device.hasOwner(state.user.email)) {
            log.error("User {} is not owner of requested deviceId {}.", user.email, deviceId);
            throw new NoPermissionException("User is not owner of requested device.");
        }

        Organization org = organizationDao.getOrgByIdOrThrow(deviceValue.orgId);

        device.fillWebDashboardValues();
        if (ctx.channel().isWritable()) {
            String devicesString = new DeviceDTO(device,  deviceValue.product, org.name).toString();
            ctx.writeAndFlush(
                    makeUTF8StringMessage(message.command, message.id, devicesString), ctx.voidPromise());
        }
    }

}
