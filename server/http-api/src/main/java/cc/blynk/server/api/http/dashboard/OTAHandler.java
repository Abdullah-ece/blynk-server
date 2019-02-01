package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.ContextUser;
import cc.blynk.core.http.annotation.DELETE;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.OtaProgress;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.internal.token.OTADownloadToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.utils.FileUtils;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.http.MediaType;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.ok;
import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.StateHolderUtil.getHardState;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/ota")
@ChannelHandler.Sharable
public class OTAHandler extends BaseHttpHandler {

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final SessionDao sessionDao;
    private final String staticFilesFolder;
    private final TokensPool tokensPool;
    private final ServerProperties props;

    public OTAHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.tokensPool = holder.tokensPool;
        this.staticFilesFolder = holder.props.jarPath;
        this.props = holder.props;
    }

    @GET
    @Path("/firmwareInfo")
    public Response getFirmwareInfo(@ContextUser User user, @QueryParam("file") String pathToFirmware) {
        if (pathToFirmware == null) {
            log.error("No path to firmware.");
            return badRequest("No path to firmware.");
        }

        java.nio.file.Path path = Paths.get(staticFilesFolder, pathToFirmware);
        Map<String, String> firmwareInfoDTO = FileUtils.getPatternFromString(path);
        return ok(new FirmwareInfo(firmwareInfoDTO));
    }

    @POST
    @Path("/start")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response startOTA(@ContextUser User user, OtaDTO otaDTO) {
        if (otaDTO == null || otaDTO.isNotValid()) {
            log.error("Wrong data for OTA start {}.", otaDTO);
            return badRequest("Wrong data for OTA start.");
        }

        //todo add tes for filter
        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                otaDTO.orgId, otaDTO.productId, otaDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", otaDTO.productId);
            return badRequest("No devices for provided productId " + otaDTO.productId);
        }

        log.info("Initiating OTA for {}. {}", user.email, otaDTO);

        if (otaDTO.checkBoardType) {
            for (Device device : filteredDevices) {
                if (device.boardType == null || device.boardType != otaDTO.firmwareInfo.boardType) {
                    log.error("Device {} ({}) with id {} does't correspond to firmware {}.",
                            device.name, device.boardType, device.id, otaDTO.firmwareInfo.boardType);
                    return badRequest(device.name + " board type doesn't correspond to firmware board type.");
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

        Session session = sessionDao.getOrgSession(otaDTO.orgId);
        String serverUrl = props.getServerUrl(otaDTO.isSecure);
        if (session != null) {
            for (Channel channel : session.hardwareChannels) {
                HardwareStateHolder hardwareState = getHardState(channel);
                if (hardwareState != null
                        && hardwareState.contains(otaDTO.deviceIds)
                        && channel.isWritable()) {

                    String downloadToken = TokenGeneratorUtil.generateNewToken();
                    tokensPool.addToken(downloadToken, new OTADownloadToken(hardwareState.device.id));
                    String body = StringUtils.makeHardwareBody(serverUrl, otaDTO.pathToFirmware, downloadToken);
                    StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777, body);
                    channel.writeAndFlush(msg, channel.voidPromise());
                    hardwareState.device.requestSent();
                }
            }
        }

        return ok(product.otaProgress);
    }

    @POST
    @Path("/stop")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response stopOTA(@ContextUser User user, OtaDTO otaDTO) {
        if (otaDTO == null || otaDTO.isDevicesEmpty()) {
            log.error("No devices to stop OTA. {}.", otaDTO);
            return badRequest("No devices to stop OTA..");
        }

        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                otaDTO.orgId, otaDTO.productId, otaDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", otaDTO.productId);
            return badRequest("No devices for provided productId " + otaDTO.productId);
        }

        log.info("Stopping OTA for {}. {}", user.email, otaDTO);

        for (Device device : filteredDevices) {
            if (device.deviceOtaInfo != null && device.deviceOtaInfo.otaStatus != OTAStatus.SUCCESS
                    && device.deviceOtaInfo.otaStatus != OTAStatus.FAILURE) {
                device.setDeviceOtaInfo(null);
            }
        }

        Product product = organizationDao.getProductByIdOrThrow(otaDTO.productId);
        product.setOtaProgress(null);

        return ok();
    }

    @DELETE
    @Path("/deleteProgress/{productId}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response deleteProgress(@ContextUser User user, @PathParam("productId") int productId) {
        if (productId == -1) {
            log.error("No productId to delete OTA progress.");
            return badRequest("No productId to delete OTA progress");
        }

        log.info("Deleting OTA progress for {}.", user.email);

        Product product = organizationDao.getProductByIdOrThrow(productId);
        product.setOtaProgress(null);

        return ok();
    }
}
