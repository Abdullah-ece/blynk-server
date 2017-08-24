package cc.blynk.server.http.web;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.MediaType;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.*;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import cc.blynk.server.http.web.model.WebComment;
import cc.blynk.server.http.web.model.WebDevice;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.SortOrder;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

import static cc.blynk.core.http.Response.*;
import static cc.blynk.utils.AdminHttpUtil.sort;

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
    @Path("/{orgId}")
    public Response createDevice(@ContextUser User user, @PathParam("orgId") int orgId, Device newDevice) {
        if (newDevice == null || newDevice.productId < 1) {
            log.error("No data or product orgId is wrong. {}", newDevice);
            return badRequest();
        }

        organizationDao.hasAccess(user, orgId);

        //default dash for all devices...
        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with orgId = {} not exists.", dashId);
            return badRequest();
        }

        Organization org = organizationDao.getOrgById(orgId);
        Product product = org.getProduct(newDevice.productId);
        if (product == null) {
            log.error("Product with passed id {} not exists for org {}.", newDevice.productId, orgId);
            throw new ProductNotFoundException("Product with passed id " + newDevice.productId + " not found.");
        }

        newDevice.metaFields = product.copyNonDefaultMetaFields();
        newDevice.webDashboard = product.webDashboard.copy();

        deviceDao.create(orgId, newDevice);
        dash.devices = ArrayUtil.add(dash.devices, newDevice, Device.class);

        final String newToken = TokenGeneratorUtil.generateNewToken();
        tokenManager.assignToken(user, dashId, newDevice.id, newToken);

        user.lastModifiedTs = System.currentTimeMillis();

        return ok(joinProductAndOrgInfo(newDevice));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{orgId}/{deviceId}/resolveEvent/{logEventId}")
    public Response resolveLogEvent(@Context ChannelHandlerContext ctx,
                                    @PathParam("orgId") int orgId,
                                    @PathParam("deviceId") int deviceId,
                                    @PathParam("logEventId") int logEventId,
                                    WebComment comment) {
        User user = getUser(ctx);
        Device device = deviceDao.getById(deviceId);
        verifyUserAccessToDevice(user, device);

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                String userComment = comment == null ? "" : comment.comment;
                dbManager.eventDBDao.resolveEvent(logEventId, user.name, userComment);
                response = ok();
            } catch (Exception e) {
                log.error("Error marking event as resolved.", e);
                response = serverError("Error marking event as resolved.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

        return null;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{orgId}")
    public Response updateDevice(@ContextUser User user,
                                 @PathParam("orgId") int orgId,
                                 Device newDevice) {

        //default dash for all devices...
        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with orgId = {} not exists.", dashId);
            return badRequest();
        }

        if (newDevice.id == 0) {
            log.error("Cannot find device with orgId 0.");
            return badRequest();
        }

        Device existingDevice = deviceDao.getById(newDevice.id);
        verifyUserAccessToDevice(user, existingDevice);

        existingDevice.update(newDevice);

        user.lastModifiedTs = System.currentTimeMillis();

        return ok(joinProductAndOrgInfo(newDevice));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{orgId}/{deviceId}/updateMetaField")
    public Response updateDeviceMetafield(@ContextUser User user,
                                          @PathParam("deviceId") int deviceId,
                                          MetaField updatedMetaField) {

        Device existingDevice = deviceDao.getById(deviceId);
        verifyUserAccessToDevice(user, existingDevice);

        int fieldIndex = existingDevice.findMetaFieldIndex(updatedMetaField.id);
        if (fieldIndex == -1) {
            log.error("MetaField with id {} not found for device id {}.", updatedMetaField.id, deviceId);
            throw new WebException("MetaField with passed id not found.");
        }

        MetaField[] updatedMetaFields = Arrays.copyOf(existingDevice.metaFields, existingDevice.metaFields.length);
        updatedMetaFields[fieldIndex] = updatedMetaField;
        existingDevice.metaFields = updatedMetaFields;
        existingDevice.metadataUpdatedAt = System.currentTimeMillis();
        existingDevice.metadataUpdatedBy = user.email;

        return ok();
    }

    @GET
    @Path("/{orgId}")
    public Response getAll(@Context ChannelHandlerContext ctx,
                           @PathParam("orgId") int orgId,
                           @QueryParam("orderField") String[] orderFields,
                           @QueryParam("order") SortOrder order) {
        User user = getUser(ctx);

        organizationDao.hasAccess(user, orgId);

        Collection<Device> devices = deviceDao.getAllByUser(orgId);

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                List<WebDevice> result = joinEventsCountSinceLastView(devices, user.email);
                response = ok(sort(result, orderFields, order));
            } catch (Exception e){
                log.error("Error getting counters for devices.", e);
                response = serverError("Error getting counters for devices.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

        return null;
    }

    private WebDevice joinProductAndOrgInfo(Device device) {
        Product product = organizationDao.getProductByIdOrNull(device.productId);
        String orgName = organizationDao.getOrganizationNameByProductId(device.productId);
        return new WebDevice(device, product, orgName);
    }

    private List<WebDevice> joinEventsCountSinceLastView(Collection<Device> devices, String email) throws Exception {
        List<WebDevice> result = new ArrayList<>(devices.size());
        Map<LogEventCountKey, Integer> counters = dbManager.eventDBDao.getEventsSinceLastView(email);
        for (Device device : devices) {
            Product product = organizationDao.getProductByIdOrNull(device.productId);
            result.add(new WebDevice(device, product, counters));
        }
        return result;
    }

    @GET
    @Path("/{orgId}/{deviceId}")
    public Response getDeviceById(@ContextUser User user,
                                  @PathParam("orgId") int orgId,
                                  @PathParam("deviceId") int deviceId) {
        Device device = deviceDao.getById(deviceId);
        if (!organizationDao.hasAccess(user, orgId)) {
            throw new ForbiddenWebException("User has no rights for this device.");
        }

        return ok(joinProductAndOrgInfo(device));
    }

    private void verifyUserAccessToDevice(User user, Device device) {
        int orgId = organizationDao.getOrganizationIdByProductId(device.productId);

        if (!user.hasAccess(orgId)) {
            log.error("User {} tries to access device he has no access.", user.email);
            throw new ForbiddenWebException("You have no access to this device.");
        }
    }

    @DELETE
    @Path("/{orgId}/{deviceId}")
    @Admin
    public Response delete(@ContextUser User user,
                           @PathParam("orgId") int userOrgId,
                           @PathParam("deviceId") int deviceId) {
        Device device = deviceDao.getById(deviceId);
        verifyUserAccessToDevice(user, device);

        deviceDao.delete(user.orgId, deviceId);

        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with orgId = {} not exists.", dashId);
            return badRequest();
        }

        int existingDeviceIndex = dash.getDeviceIndexById(deviceId);
        dash.devices = ArrayUtil.remove(dash.devices, existingDeviceIndex, Device.class);

        tokenManager.deleteDevice(device);

        user.lastModifiedTs = System.currentTimeMillis();

        return ok();
    }

    @GET
    @Path("/{orgId}/{deviceId}/timeline")
    public Response getDeviceTimeline(@Context ChannelHandlerContext ctx,
                                      @PathParam("orgId") int orgId,
                                      @PathParam("deviceId") int deviceId,
                                      @QueryParam("eventType") EventType eventType,
                                      @QueryParam("isResolved") Boolean isResolved,
                                      @QueryParam("from") long from,
                                      @QueryParam("to") long to,
                                      @QueryParam("offset") int offset,
                                      @QueryParam("limit") int limit) {

        Device device = deviceDao.getById(deviceId);
        User user = getUser(ctx);
        verifyUserAccessToDevice(user, device);

        Product product = organizationDao.getProductByIdOrNull(device.productId);

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                List<LogEvent> eventList;
                //todo introduce some query builder? jOOQ?
                if (eventType == null && isResolved == null) {
                    eventList = dbManager.eventDBDao.getEvents(deviceId, from, to, offset, limit);
                } else {
                    if (eventType == null) {
                        eventList = dbManager.eventDBDao.getEvents(deviceId, from, to, offset, limit, isResolved);
                    } else if (isResolved == null) {
                        eventList = dbManager.eventDBDao.getEvents(deviceId, eventType, from, to, offset, limit);
                    } else {
                        eventList = dbManager.eventDBDao.getEvents(deviceId, eventType, from, to, offset, limit, isResolved);
                    }
                }

                dbManager.eventDBDao.upsertLastSeen(deviceId, user.email);

                if (product != null) {
                    joinLogEventName(product, eventList);
                }

                Map<LogEventCountKey, Integer> totalCounters = dbManager.eventDBDao.getEventsTotalCounters(from, to, deviceId);

                response = ok(new HashMap<String, Object>() {
                    {
                        put("totalCritical", totalCounters.getOrDefault(new LogEventCountKey(deviceId, EventType.CRITICAL, false), 0));
                        put("totalWarning", totalCounters.getOrDefault(new LogEventCountKey(deviceId, EventType.WARNING, false), 0));
                        put("totalResolved", totalResolved(totalCounters));
                        put("logEvents", eventList);
                    }
                });
            } catch (Exception e) {
                log.error("Error retrieving timeline for deviceId {}, limit {}, offset {}.", deviceId, limit, offset, e);
                response = serverError("Error retrieving timeline for device.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

        return null;
    }

    private int totalResolved(Map<LogEventCountKey, Integer> totalCounters) {
        int totalResolved = 0;
        for (Map.Entry<LogEventCountKey, Integer> entry : totalCounters.entrySet()) {
            if (entry.getKey().isResolved) {
                totalResolved += entry.getValue();
            }
        }
        return totalResolved;
    }

    private List<LogEvent> joinLogEventName(Product product, List<LogEvent> logEvents) {
        for (LogEvent logEvent : logEvents) {
            Event templateEvent = product.findEventByCode(logEvent.eventHashcode);
            if (templateEvent == null) {
                log.warn("Can't find template for event: {}", logEvent);
            } else {
                logEvent.update(templateEvent);
            }
        }
        return logEvents;
    }

    private static User getUser(ChannelHandlerContext ctx) {
        return ctx.channel().attr(SessionDao.userSessionAttributeKey).get().user;
    }
}
