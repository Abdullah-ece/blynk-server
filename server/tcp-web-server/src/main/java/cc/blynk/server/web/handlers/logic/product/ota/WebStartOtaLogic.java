package cc.blynk.server.web.handlers.logic.product.ota;

import cc.blynk.server.Holder;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.ota.OTAInfo;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.OtaProgress;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.internal.token.OTADownloadToken;
import cc.blynk.server.internal.token.TokensPool;
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
    private final SessionDao sessionDao;
    private final TokensPool tokensPool;
    private final ServerProperties props;

    public WebStartOtaLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
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
        OtaDTO otaDTO = JsonParser.readAny(message.body, OtaDTO.class);

        if (otaDTO == null || otaDTO.isNotValid()) {
            log.error("Wrong data for OTA start {}.", otaDTO);
            throw new JsonException("Wrong data for OTA start.");
        }

        int orgId = state.selectedOrgId;
        User user = state.user;
        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(orgId, otaDTO.productId, otaDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", otaDTO.productId);
            throw new JsonException("No devices for provided productId " + otaDTO.productId);
        }

        log.info("Initiating OTA for {}. {}", user.email, otaDTO);

        if (otaDTO.checkBoardType) {
            for (Device device : filteredDevices) {
                if (device.boardType == null || device.boardType != otaDTO.firmwareInfo.boardType) {
                    log.error("Device {} ({}) with id {} does't correspond to firmware {}.",
                            device.name, device.boardType, device.id, otaDTO.firmwareInfo.boardType);
                    throw new JsonException(device.name + " board type doesn't correspond to firmware board type.");
                }
            }
        }

        long now = System.currentTimeMillis();
        Product product = organizationDao.getProductByIdOrThrow(otaDTO.productId);
        product.setOtaProgress(new OtaProgress(otaDTO, now));

        for (Device device : filteredDevices) {
            DeviceOtaInfo deviceOtaInfo = new DeviceOtaInfo(user.email, now,
                    -1L, -1L, -1L, -1L,
                    otaDTO.pathToFirmware, otaDTO.firmwareInfo.buildDate,
                    OTAStatus.STARTED, 0, otaDTO.attemptsLimit, otaDTO.isSecure);
            device.setDeviceOtaInfo(deviceOtaInfo);
        }

        Session session = sessionDao.getOrgSession(orgId);
        String serverUrl = props.getServerUrl(otaDTO.isSecure);
        if (session != null) {
            for (Channel channel : session.hardwareChannels) {
                HardwareStateHolder hardwareState = getHardState(channel);
                if (hardwareState != null
                        && hardwareState.contains(otaDTO.deviceIds)
                        && channel.isWritable()) {

                    String downloadToken = TokenGeneratorUtil.generateNewToken();
                    tokensPool.addToken(downloadToken, new OTADownloadToken(hardwareState.device.id));
                    String body = OTAInfo.makeHardwareBody(serverUrl, otaDTO.pathToFirmware, downloadToken);
                    StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777, body);
                    channel.writeAndFlush(msg, channel.voidPromise());
                    hardwareState.device.requestSent();
                }
            }
        }

        String firmwareParamsString = product.otaProgress.toString();
        StringMessage response = makeUTF8StringMessage(message.command, message.id, firmwareParamsString);
        ctx.writeAndFlush(response, ctx.voidPromise());
    }
}
