package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Admin;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.ContextUser;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.Path;
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
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
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

        for (Device device : devices) {
            device.updateOTAInfo(user.email);
        }

        Session session = sessionDao.userSession.get(new UserKey(user));

        StringMessage msg = makeASCIIStringMessage(BLYNK_INTERNAL, 7777,
                OTAInfo.makeHardwareBody(serverHostUrl, serverHostUrl));

        for (Channel channel : session.hardwareChannels) {
            HardwareStateHolder hardwareState = getHardState(channel);
            if (hardwareState != null && channel.isWritable()) {
                channel.writeAndFlush(msg, channel.voidPromise());
                hardwareState.device.deviceOtaInfo.otaStatus = OTAStatus.REQUEST_SENT;
            }
        }

        return ok();
    }

}
