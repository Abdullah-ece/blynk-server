package cc.blynk.server.web.handlers.logic.organization.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.DeviceShipmentInfo;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_STOP;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.12.18.
 */
public final class WebStopShipmentLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebStopShipmentLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public int getPermission() {
        return OTA_STOP;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        ShipmentDTO shipmentDTO = JsonParser.readAny(message.body, ShipmentDTO.class);

        if (shipmentDTO == null || shipmentDTO.isDevicesEmpty()) {
            log.error("No devices to stop OTA. {}.", shipmentDTO);
            throw new JsonException("No devices to stop OTA.");
        }

        int orgId = state.selectedOrgId;

        /*
        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                orgId, shipmentDTO.productId, shipmentDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", shipmentDTO.productId);
            throw new JsonException("No devices for provided productId " + shipmentDTO.productId);
        }
        */

        log.info("Stopping OTA for {}. {}", state.user.email, shipmentDTO);

        for (int deviceId : shipmentDTO.deviceIds) {
            DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
            if (deviceValue != null) {
                Device device = deviceValue.device;
                DeviceShipmentInfo deviceShipmentInfo = device.deviceShipmentInfo;
                if (deviceShipmentInfo != null && deviceShipmentInfo.status.inProgress()) {
                    device.clearDeviceShipmentInfo();
                }
            }
        }

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        org.stopShipment(shipmentDTO.id);

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
