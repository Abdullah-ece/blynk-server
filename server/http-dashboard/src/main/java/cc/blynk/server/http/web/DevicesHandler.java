package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.core.http.Response.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
@Path("/devices")
@ChannelHandler.Sharable
public class DevicesHandler extends BaseHttpHandler {

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;

    public DevicesHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
    }

    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response createDevice(@Context ChannelHandlerContext ctx, Device newDevice) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        //default dash for all devices...
        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with id = {} not exists.", dashId);
            return badRequest();
        }

        deviceDao.add(user.orgId, newDevice);
        dash.devices = ArrayUtil.add(dash.devices, newDevice, Device.class);

        final String newToken = TokenGeneratorUtil.generateNewToken();
        tokenManager.assignToken(user, dashId, newDevice.id, newToken);

        user.lastModifiedTs = System.currentTimeMillis();

        String orgName = organizationDao.getOrganizationNameByProductId(newDevice.productId);
        newDevice.setOrgName(orgName);

        return ok(newDevice);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("")
    public Response updateDevice(@Context ChannelHandlerContext ctx, Device newDevice) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        //default dash for all devices...
        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with id = {} not exists.", dashId);
            return badRequest();
        }

        if (newDevice.id == 0) {
            log.error("Cannot find device with id 0.");
            return badRequest();
        }

        Device existingDevice = deviceDao.getById(newDevice.id);
        existingDevice.update(newDevice);

        user.lastModifiedTs = System.currentTimeMillis();

        String orgName = organizationDao.getOrganizationNameByProductId(newDevice.productId);
        newDevice.setOrgName(orgName);

        return ok(newDevice);
    }

    @GET
    @Path("")
    public Response getAll(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        if (user.isAdmin()) {
            return ok(deviceDao.getAll());
        }

        return ok(deviceDao.getAllByUser(user));
    }

    @GET
    @Path("/{id}")
    public Response getDeviceById(@Context ChannelHandlerContext ctx, @PathParam("id") int id) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        //todo security checks
        Device device = deviceDao.getById(id);

        if (device == null) {
            log.error("Device with id = {} not exists.", id);
            return notFound();
        }

        String orgName = organizationDao.getOrganizationNameByProductId(device.productId);
        device.setOrgName(orgName);

        return ok(device);
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@Context ChannelHandlerContext ctx, @PathParam("id") int id) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        Device device = deviceDao.delete(user.orgId, id);
        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with id = {} not exists.", dashId);
            return badRequest();
        }

        int existingDeviceIndex = dash.getDeviceIndexById(id);
        dash.devices = ArrayUtil.remove(dash.devices, existingDeviceIndex, Device.class);

        tokenManager.deleteDevice(device);

        user.lastModifiedTs = System.currentTimeMillis();

        return ok();
    }

}
