package cc.blynk.server.web.handlers.logic.product.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_STOP;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.12.18.
 */
public final class WebStopOtaLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public WebStopOtaLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public int getPermission() {
        return OTA_STOP;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        OtaDTO otaDTO = JsonParser.readAny(message.body, OtaDTO.class);

        if (otaDTO == null || otaDTO.isDevicesEmpty()) {
            log.error("No devices to stop OTA. {}.", otaDTO);
            throw new JsonException("No devices to stop OTA.");
        }

        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                otaDTO.orgId, otaDTO.productId, otaDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", otaDTO.productId);
            throw new JsonException("No devices for provided productId " + otaDTO.productId);
        }

        log.info("Stopping OTA for {}. {}", state.user.email, otaDTO);

        for (Device device : filteredDevices) {
            if (device.deviceOtaInfo != null && device.deviceOtaInfo.otaStatus != OTAStatus.SUCCESS
                    && device.deviceOtaInfo.otaStatus.isNotFailure()) {
                device.clearDeviceOtaInfo();
            }
        }

        Product product = organizationDao.getProductByIdOrThrow(otaDTO.productId);
        product.clearOtaProgress();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
