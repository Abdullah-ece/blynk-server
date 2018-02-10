package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.ContextUser;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.DataQueryRequestGroupDTO;
import cc.blynk.server.api.http.dashboard.dto.DataResponseDTO;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import cc.blynk.server.db.dao.descriptor.TableDataMapper;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.time.LocalDateTime;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.ok;
import static cc.blynk.core.http.Response.serverError;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/data")
@ChannelHandler.Sharable
public class DataHandler extends BaseHttpHandler {

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;

    DataHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
    }

    @GET
    @Path("/{deviceId}/insert")
    public Response insertSinglePoint(@Context ChannelHandlerContext ctx,
                                      @ContextUser User user,
                                      @PathParam("deviceId") int deviceId,
                                      @QueryParam("dataStream") String dataStream,
                                      @QueryParam("value") String value,
                                      @QueryParam("ts") Long inTs) {

        Device device = deviceDao.getById(deviceId);
        organizationDao.verifyUserAccessToDevice(user, device);

        PinType pinType = PinType.getPinType(dataStream.charAt(0));
        byte pin = Byte.parseByte(dataStream.substring(1));

        long ts = (inTs == null ? System.currentTimeMillis() : inTs);

        TableDescriptor descriptor = TableDescriptor.getTableByPin(pin, pinType);

        blockingIOProcessor.executeDB(() -> {
            dbManager.reportingDBDao.insertDataPoint(new TableDataMapper(
                    descriptor, deviceId, pin, pinType, LocalDateTime.now(), value));
            ctx.writeAndFlush(ok(), ctx.voidPromise());
        });

        return null;
    }

    @POST
    @Path("/{deviceId}/history")
    @SuppressWarnings("unckecked")
    public Response getAll(@Context ChannelHandlerContext ctx,
                           @ContextUser User user,
                           @PathParam("deviceId") int deviceId,
                           DataQueryRequestGroupDTO dataQueryRequestGroup) {

        if (dataQueryRequestGroup == null || dataQueryRequestGroup.isNotValid()) {
            return badRequest("No data stream provided for request.");
        }

        Device device = deviceDao.getById(deviceId);
        organizationDao.verifyUserAccessToDevice(user, device);
        dataQueryRequestGroup.setDeviceId(deviceId);

        blockingIOProcessor.executeDB(() -> {
            try {
                DataResponseDTO response = new DataResponseDTO(dataQueryRequestGroup.dataQueryRequests.length);
                for (DataQueryRequestDTO dataQueryRequest : dataQueryRequestGroup.dataQueryRequests) {
                    Object data = dbManager.getRawData(dataQueryRequest);
                    response.add(data);
                }
                ctx.writeAndFlush(ok(response.data()), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error fetching history data.", e);
                ctx.writeAndFlush(serverError("Error fetching history data."), ctx.voidPromise());
            }
        });

        return null;
    }

}
