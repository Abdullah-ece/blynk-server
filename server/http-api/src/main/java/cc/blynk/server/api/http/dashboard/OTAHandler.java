package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Admin;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.ContextUser;
import cc.blynk.core.http.annotation.DELETE;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.StartOtaDTO;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserKey;
import cc.blynk.server.core.dao.ota.OTAInfo;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.OtaProgress;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.FileUtils;
import cc.blynk.utils.http.MediaType;
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
    private final String serverHostUrl;

    public OTAHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.staticFilesFolder = holder.props.jarPath;
        String httpPort = holder.props.getHttpPortAsString();
        this.serverHostUrl = "http://" + holder.props.getServerHost() + (httpPort.equals("80") ? "" : (":" + httpPort));
    }

    @GET
    @Path("/firmwareInfo")
    public Response getFirmwareInfo(@ContextUser User user, @QueryParam("file") String pathToFirmware) {
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        if (pathToFirmware == null) {
            log.error("No path to firmware.");
            return badRequest("No path to firmware.");
        }

        java.nio.file.Path path = Paths.get(staticFilesFolder, pathToFirmware);
        Map<String, String> firmwareInfoDTO = FileUtils.getPatternFromString(path);
        return ok(firmwareInfoDTO);
    }

    @POST
    @Path("/start")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Admin
    public Response startOTA(@ContextUser User user, StartOtaDTO startOtaDTO) {
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        if (startOtaDTO == null || startOtaDTO.isNotValid()) {
            log.error("Wrong data for OTA start {}.", startOtaDTO);
            return badRequest("Wrong data for OTA start.");
        }

        List<Device> devices = deviceDao.getAllByProductId(startOtaDTO.productId);
        if (devices.size() == 0) {
            log.error("No devices for provided productId {}", startOtaDTO.productId);
            return badRequest("No devices for provided productId " + startOtaDTO.productId);
        }

        log.info("Initiating OTA for {}. {}", user.email, startOtaDTO);

        java.nio.file.Path path = Paths.get(staticFilesFolder, startOtaDTO.pathToFirmware);
        FirmwareInfo firmwareInfo = new FirmwareInfo(FileUtils.getPatternFromString(path));

        long now = System.currentTimeMillis();
        Product product = organizationDao.getProductById(startOtaDTO.productId);
        product.setOtaProgress(new OtaProgress(startOtaDTO.title,
                startOtaDTO.pathToFirmware, startOtaDTO.firmwareOriginalFileName,
                now, -1,
                startOtaDTO.deviceIds, firmwareInfo));

        for (Device device : devices) {
            if (device.boardType == null || !device.boardType.equals(firmwareInfo.boardType)) {
                log.error("Device {} ({}) with id {} does't correspond to firmware {}.",
                        device.name, device.boardType, device.id, firmwareInfo.boardType);
                return badRequest(device.name + " board type doesn't correspond to firmware board type.");
            }
        }

        for (Device device : devices) {
            DeviceOtaInfo deviceOtaInfo = new DeviceOtaInfo(user.email, now,
                    -1L, -1L,
                    startOtaDTO.pathToFirmware, firmwareInfo.buildDate,
                    OTAStatus.STARTED);
            device.setDeviceOtaInfo(deviceOtaInfo);
        }

        Session session = sessionDao.userSession.get(new UserKey(user));
        if (session != null) {
            StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777,
                    OTAInfo.makeHardwareBody(serverHostUrl, startOtaDTO.pathToFirmware));

            for (Channel channel : session.hardwareChannels) {
                HardwareStateHolder hardwareState = getHardState(channel);
                if (hardwareState != null
                        && ArrayUtil.contains(startOtaDTO.deviceIds, hardwareState.device.id)
                        && channel.isWritable()) {
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
    @Admin
    public Response stopOTA(@ContextUser User user, StartOtaDTO startOtaDTO) {
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        if (startOtaDTO == null || startOtaDTO.isDevicesEmpty()) {
            log.error("No devices to stop OTA. {}.", startOtaDTO);
            return badRequest("No devices to stop OTA..");
        }

        List<Device> devices = deviceDao.getAllByProductId(startOtaDTO.productId);
        if (devices.size() == 0) {
            log.error("No devices for provided productId {}", startOtaDTO.productId);
            return badRequest("No devices for provided productId " + startOtaDTO.productId);
        }

        log.info("Stopping OTA for {}. {}", user.email, startOtaDTO);

        for (Device device : devices) {
            if (device.deviceOtaInfo != null && device.deviceOtaInfo.otaStatus == OTAStatus.STARTED) {
                device.setDeviceOtaInfo(null);
            }
        }

        Product product = organizationDao.getProductById(startOtaDTO.productId);
        product.setOtaProgress(null);

        return ok();
    }

    @DELETE
    @Path("/deleteProgress/{productId}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Admin
    public Response deleteProgress(@ContextUser User user, @PathParam("productId") int productId) {
        Organization organization = organizationDao.getOrgByIdOrThrow(user.orgId);

        if (organization == null) {
            log.error("Cannot find org with id {} for user {}", user.orgId, user.email);
            return badRequest();
        }

        if (productId == -1) {
            log.error("No productId to delete OTA progress.");
            return badRequest("No productId to delete OTA progress");
        }

        log.info("Deleting OTA progress for {}.", user.email);

        Product product = organizationDao.getProductById(productId);
        product.setOtaProgress(null);

        return ok();
    }
}
