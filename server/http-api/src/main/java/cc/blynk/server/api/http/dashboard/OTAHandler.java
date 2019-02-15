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
import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.Shipment;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.ReportingDBManager;
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
    private final ReportingDBManager reportingDBManager;
    private final String staticFilesFolder;
    private final TokensPool tokensPool;
    private final ServerProperties props;

    public OTAHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.reportingDBManager = holder.reportingDBManager;
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
    public Response startOTA(@ContextUser User user, ShipmentDTO shipmentDTO) {
        if (shipmentDTO == null || shipmentDTO.isNotValid()) {
            log.error("Wrong data for OTA start {}.", shipmentDTO);
            return badRequest("Wrong data for OTA start.");
        }

        //todo add tes for filter
        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                shipmentDTO.orgId, shipmentDTO.productId, shipmentDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", shipmentDTO.productId);
            return badRequest("No devices for provided productId " + shipmentDTO.productId);
        }

        log.info("Initiating OTA for {}. {}", user.email, shipmentDTO);

        long now = System.currentTimeMillis();
        Organization org = organizationDao.getOrgByIdOrThrow(shipmentDTO.orgId);
        Shipment shipment = new Shipment(shipmentDTO, user.email, now);
        org.addShipment(shipment);

        for (Device device : filteredDevices) {
            device.startedOTA(shipment, 0);
            reportingDBManager.collectEvent(shipment, device);
        }

        Session session = sessionDao.getOrgSession(shipmentDTO.orgId);
        String serverUrl = props.getServerUrl(shipment.isSecure);
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
                    reportingDBManager.collectEvent(shipment, hardwareState.device);
                }
            }
        }

        return ok(shipment);
    }

    @POST
    @Path("/stop")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response stopOTA(@ContextUser User user, ShipmentDTO shipmentDTO) {
        if (shipmentDTO == null || shipmentDTO.isDevicesEmpty()) {
            log.error("No devices to stop OTA. {}.", shipmentDTO);
            return badRequest("No devices to stop OTA..");
        }

        List<Device> filteredDevices = deviceDao.getByProductIdAndFilter(
                shipmentDTO.orgId, shipmentDTO.productId, shipmentDTO.deviceIds);
        if (filteredDevices.size() == 0) {
            log.error("No devices for provided productId {}", shipmentDTO.productId);
            return badRequest("No devices for provided productId " + shipmentDTO.productId);
        }

        log.info("Stopping OTA for {}. {}", user.email, shipmentDTO);

        for (Device device : filteredDevices) {
            if (device.deviceOtaInfo != null && device.deviceOtaInfo.status != OTADeviceStatus.SUCCESS
                    && device.deviceOtaInfo.status != OTADeviceStatus.FAILURE) {
                device.setDeviceOtaInfo(null);
            }
        }

        Organization org = organizationDao.getOrgByIdOrThrow(shipmentDTO.orgId);
        org.stopShipment(shipmentDTO.id);

        return ok();
    }

    @DELETE
    @Path("/deleteProgress/{shipmentId}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response deleteProgress(@ContextUser User user, @PathParam("shipmentId") int shipmentId) {
        if (shipmentId == -1) {
            log.error("No productId to delete OTA progress.");
            return badRequest("No productId to delete OTA progress");
        }

        log.info("Deleting OTA progress for {}.", user.email);

        //todo user.orgId is wrong, but leave for now, this api should be removed
        Organization org = organizationDao.getOrgByIdOrThrow(user.orgId);
        org.deleteShipment(shipmentId);

        return ok();
    }
}
