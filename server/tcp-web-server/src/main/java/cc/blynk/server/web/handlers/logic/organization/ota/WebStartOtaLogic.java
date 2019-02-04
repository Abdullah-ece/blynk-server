package cc.blynk.server.web.handlers.logic.organization.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OTADao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.Shipment;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.internal.token.OTADownloadToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OTA_START;
import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.StateHolderUtil.getHardState;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.12.18.
 */
public final class WebStartOtaLogic implements PermissionBasedLogic<WebAppStateHolder> {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final OTADao otaDao;
    private final SessionDao sessionDao;
    private final TokensPool tokensPool;
    private final ServerProperties props;

    public WebStartOtaLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.otaDao = holder.otaDao;
        this.sessionDao = holder.sessionDao;
        this.tokensPool = holder.tokensPool;
        this.props = holder.props;
    }

    @Override
    public int getPermission() {
        return OTA_START;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        ShipmentDTO shipmentDTO = JsonParser.readAny(message.body, ShipmentDTO.class);

        if (shipmentDTO == null || shipmentDTO.isNotValid()) {
            log.error("Wrong data for OTA start {}.", shipmentDTO);
            throw new JsonException("Wrong data for OTA start.");
        }

        int orgId = state.selectedOrgId;
        User user = state.user;
        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                orgId, shipmentDTO.productId, shipmentDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", shipmentDTO.productId);
            throw new JsonException("No devices for provided productId " + shipmentDTO.productId);
        }

        log.info("Initiating OTA for {}. {}", user.email, shipmentDTO);

        long now = System.currentTimeMillis();
        Organization org = organizationDao.getOrgByIdOrThrow(shipmentDTO.orgId);
        Product product = org.getProductOrThrow(shipmentDTO.productId);
        boolean isSecure = product.isSecureOTA();
        Shipment shipment = new Shipment(shipmentDTO, isSecure, now);
        org.addShipment(shipment);

        for (Device device : filteredDevices) {
            DeviceOtaInfo deviceOtaInfo = new DeviceOtaInfo(shipment.id, user.email, now,
                    -1L, -1L, -1L, -1L,
                    shipmentDTO.pathToFirmware, shipmentDTO.firmwareInfo.buildDate,
                    OTADeviceStatus.STARTED, 0, shipmentDTO.attemptsLimit);
            device.setDeviceOtaInfo(deviceOtaInfo);
        }

        Session session = sessionDao.getOrgSession(orgId);
        String serverUrl = props.getServerUrl(isSecure);
        if (session != null) {
            for (Channel channel : session.hardwareChannels) {
                HardwareStateHolder hardwareState = getHardState(channel);
                if (hardwareState != null
                        && hardwareState.contains(shipmentDTO.deviceIds)
                        && channel.isWritable()) {

                    String downloadToken = TokenGeneratorUtil.generateNewToken();
                    tokensPool.addToken(downloadToken, new OTADownloadToken(hardwareState.device.id));
                    String body = StringUtils.makeHardwareBody(serverUrl, shipmentDTO.pathToFirmware, downloadToken);
                    StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777, body);
                    channel.writeAndFlush(msg, channel.voidPromise());
                    hardwareState.device.requestSent();
                }
            }
        }

        String firmwareParamsString = shipment.toString();
        StringMessage response = makeUTF8StringMessage(message.command, message.id, firmwareParamsString);
        ctx.writeAndFlush(response, ctx.voidPromise());
    }
}
