package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.HttpSession;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.UserEvent;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventsSinceLastView;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;

    public DevicesHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
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

        return ok(joinProductAndOrgInfo(newDevice));
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

        return ok(joinProductAndOrgInfo(newDevice));
    }

    @GET
    @Path("")
    public Response getAll(@Context ChannelHandlerContext ctx) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        Collection<Device> devices;
        if (user.isAdmin()) {
            devices = deviceDao.getAll();
        } else {
            devices = deviceDao.getAllByUser(user);
        }

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                joinEventsCountSinceLastView(devices, user.lastViewTs);
                response = ok(devices);
                user.lastViewTs = System.currentTimeMillis();
            } catch (Exception e){
                log.error("Error getting counters for devices.", e);
                response = serverError("Error getting counters for devices.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

        return null;
    }

    private Device joinProductAndOrgInfo(Device device) {
        Map<String, Object> props = new HashMap<>();
        Product product = organizationDao.getProductById(device.productId);

        if (product != null) {
            if (product.name != null) {
                props.put("productName", product.name);
            }
            if (product.logoUrl != null) {
                props.put("productLogoUrl", product.logoUrl);
            }
        }

        String orgName = organizationDao.getOrganizationNameByProductId(device.productId);
        if (orgName != null) {
            props.put("orgName", orgName);
        }

        device.dynamicFields = props;

        return device;
    }

    private void joinEventsCountSinceLastView(Collection<Device> devices, long lastViewTs) throws Exception {
        Map<LogEventsSinceLastView, Integer> counters = dbManager.eventDBDao.getEventsSinceLastLogin(lastViewTs);
        for (Device device : devices) {
            Product product = organizationDao.getProductById(device.productId);
            String productName = product == null ? null : product.name;
            device.setEventsCounterSinceLastView(
                    counters.get(new LogEventsSinceLastView(device.id, EventType.CRITICAL)),
                    counters.get(new LogEventsSinceLastView(device.id, EventType.WARNING)),
                    productName
            );
        }
    }

    @GET
    @Path("/{id}")
    public Response getDeviceById(@Context ChannelHandlerContext ctx, @PathParam("id") int deviceId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        //todo security checks
        Device device = deviceDao.getById(deviceId);

        if (device == null) {
            log.error("Device with id = {} not exists.", deviceId);
            return notFound();
        }

        return ok(joinProductAndOrgInfo(device));
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@Context ChannelHandlerContext ctx, @PathParam("id") int deviceId) {
        HttpSession httpSession = ctx.channel().attr(SessionDao.userSessionAttributeKey).get();
        User user = httpSession.user;

        Device device = deviceDao.delete(user.orgId, deviceId);
        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with id = {} not exists.", dashId);
            return badRequest();
        }

        int existingDeviceIndex = dash.getDeviceIndexById(deviceId);
        dash.devices = ArrayUtil.remove(dash.devices, existingDeviceIndex, Device.class);

        tokenManager.deleteDevice(device);

        user.lastModifiedTs = System.currentTimeMillis();

        return ok();
    }

    @GET
    @Path("/timeline/{id}")
    public Response getDeviceTimeline(@Context ChannelHandlerContext ctx,
                                      @PathParam("id") int deviceId,
                                      @QueryParam("eventType") EventType eventType,
                                      @QueryParam("isResolved") Boolean isResolved,
                                      @QueryParam("from") long from,
                                      @QueryParam("to") long to,
                                      @QueryParam("offset") int offset,
                                      @QueryParam("limit") int limit) {

        Device device = deviceDao.getById(deviceId);

        if (device == null) {
            log.error("Device with id = {} not exists.", deviceId);
            return notFound();
        }

        Product product = organizationDao.getProductById(device.productId);

        if (product == null) {
            log.error("Product with id {} not exists.", device.productId);
            return notFound();
        }

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                List<LogEvent> eventList;
                if (eventType == null) {
                    if (isResolved != null && isResolved) {
                        eventList = dbManager.eventDBDao.getEvents(deviceId, from, to, offset, limit, true);
                    } else {
                        eventList = dbManager.eventDBDao.getEvents(deviceId, from, to, offset, limit);
                    }
                } else {
                    eventList = dbManager.eventDBDao.getEvents(deviceId, eventType, from, to, offset, limit);
                }

                joinLogEventName(product, eventList);

                response = ok(eventList);
            } catch (Exception e) {
                log.error("Error retrieving timeline for deviceId {}, limit {}, offset {}.", deviceId, limit, offset, e);
                response = serverError("Error retrieving timeline for device.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

        return null;
    }

    private void joinLogEventName(Product product, List<LogEvent> logEvents) {
        for (LogEvent logEvent : logEvents) {
            if (logEvent.eventType.isUserEvent) {
                UserEvent event = (UserEvent) product.findEventByCode(logEvent.eventHashcode);
                logEvent.update(event);
            }
        }
    }

}
