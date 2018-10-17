package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.BaseHttpHandler;
import cc.blynk.core.http.Response;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.ContextUser;
import cc.blynk.core.http.annotation.DELETE;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.PUT;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.core.http.utils.AdminHttpUtil;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.dashboard.dto.CommentDTO;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserKey;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.exceptions.WebException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.TokenGeneratorUtil;
import cc.blynk.utils.http.MediaType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.notFound;
import static cc.blynk.core.http.Response.ok;
import static cc.blynk.core.http.Response.serverError;
import static cc.blynk.server.core.protocol.enums.Command.RESOLVE_EVENT;

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
    private final ReportingDBManager reportingDBManager;

    public DevicesHandler(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
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

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Product product = org.getProduct(newDevice.productId);
        if (product == null) {
            log.error("Product with passed id {} not exists for org {}.", newDevice.productId, orgId);
            throw new ProductNotFoundException("Product with passed id " + newDevice.productId + " not found.");
        }

        newDevice.metaFields = product.copyMetaFields();
        newDevice.webDashboard = product.webDashboard.copy();

        deviceDao.create(orgId, newDevice);
        dash.devices = ArrayUtil.add(dash.devices, newDevice, Device.class);

        final String newToken = TokenGeneratorUtil.generateNewToken();
        tokenManager.assignToken(user, dash, newDevice, newToken);

        user.lastModifiedTs = System.currentTimeMillis();

        return ok(joinProductAndOrgInfo(newDevice));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{orgId}/{deviceId}/resolveEvent/{logEventId}")
    public Response resolveLogEvent(@Context ChannelHandlerContext ctx,
                                    @PathParam("orgId") int orgId,
                                    @PathParam("deviceId") int deviceId,
                                    @PathParam("logEventId") long logEventId,
                                    CommentDTO comment) {
        User user = getUser(ctx);
        Device device = deviceDao.getByIdOrThrow(deviceId);
        organizationDao.verifyUserAccessToDevice(user, device);

        blockingIOProcessor.executeEvent(() -> {
            Response response;
            try {
                String userComment = comment == null ? null : comment.comment;
                if (reportingDBManager.eventDBDao.resolveEvent(logEventId, user.name, userComment)) {
                    response = ok();
                    Session session = sessionDao.userSession.get(new UserKey(user));
                    String body = logEventId + StringUtils.BODY_SEPARATOR_STRING + user.email;
                    if (comment != null) {
                        body = body + StringUtils.BODY_SEPARATOR + userComment;
                    }
                    session.sendToSelectedDeviceOnWeb(ctx.channel(), RESOLVE_EVENT, 1111, body, deviceId);
                } else {
                    log.warn("Event with id {} for user {} not resolved.", logEventId, user.email);
                    response = notFound();
                }
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

        if (newDevice.productId < 1) {
            log.error("Device has no product assigned. {}", newDevice);
            return badRequest("Device has no product assigned.");
        }

        Organization org = organizationDao.getOrgByIdOrThrow(orgId);
        Product product = org.getProduct(newDevice.productId);
        if (product == null) {
            log.error("Product with passed id {} not exists for org {}.", newDevice.productId, orgId);
            throw new ProductNotFoundException("Product with passed id " + newDevice.productId + " not found.");
        }

        Device existingDevice = deviceDao.getByIdOrThrow(newDevice.id);
        organizationDao.verifyUserAccessToDevice(user, existingDevice);

        existingDevice.updateFromWeb(newDevice);

        return ok(joinProductAndOrgInfo(newDevice));
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{orgId}/{deviceId}/updateMetaField")
    public Response updateDeviceMetafield(@ContextUser User user,
                                          @PathParam("deviceId") int deviceId,
                                          MetaField updatedMetaField) {

        Device existingDevice = deviceDao.getByIdOrThrow(deviceId);
        organizationDao.verifyUserAccessToDevice(user, existingDevice);

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

        Collection<Device> devices = organizationDao.getAllDevicesByOrgId(orgId);

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                List<DeviceDTO> result = joinEventsCountSinceLastView(devices, user.email);
                response = ok(AdminHttpUtil.sort(result, orderFields, order));
            } catch (Exception e) {
                log.error("Error getting counters for devices.", e);
                response = serverError("Error getting counters for devices.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

        return null;
    }

    private DeviceDTO joinProductAndOrgInfo(Device device) {
        Product product = organizationDao.getProductById(device.productId);
        String orgName = organizationDao.getOrganizationNameByProductId(device.productId);
        return new DeviceDTO(device, product, orgName);
    }

    private List<DeviceDTO> joinEventsCountSinceLastView(Collection<Device> devices, String email) throws Exception {
        List<DeviceDTO> result = new ArrayList<>(devices.size());
        Map<LogEventCountKey, Integer> counters = reportingDBManager.eventDBDao.getEventsSinceLastView(email);
        for (Device device : devices) {
            Product product = organizationDao.getProductById(device.productId);
            result.add(new DeviceDTO(device, product, counters));
        }
        return result;
    }

    @GET
    @Path("/{orgId}/{deviceId}")
    public Response getDeviceById(@ContextUser User user,
                                  @PathParam("orgId") int orgId,
                                  @PathParam("deviceId") int deviceId) {
        Device device = deviceDao.getByIdOrThrow(deviceId);
        if (!organizationDao.hasAccess(user, orgId)) {
            throw new ForbiddenWebException("User has no rights for this device.");
        }

        return ok(joinProductAndOrgInfo(device));
    }

    @DELETE
    @Path("/{orgId}/{deviceId}")
    public Response delete(@ContextUser User user,
                           @PathParam("orgId") int userOrgId,
                           @PathParam("deviceId") int deviceId) {
        Device device = deviceDao.getByIdOrThrow(deviceId);
        organizationDao.verifyUserAccessToDevice(user, device);

        deviceDao.delete(deviceId);

        final int dashId = 0;
        DashBoard dash = user.profile.getDashById(dashId);

        if (dash == null) {
            log.error("Dash with orgId = {} not exists.", dashId);
            return badRequest();
        }

        try {
            int existingDeviceIndex = dash.getDeviceIndexById(deviceId);
            dash.devices = ArrayUtil.remove(dash.devices, existingDeviceIndex, Device.class);
        } catch (Exception e) {
          //no device in app dashboard. ignore.
          //todo provide method without exception thrown
        }

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

        Device device = deviceDao.getByIdOrThrow(deviceId);
        User user = getUser(ctx);
        organizationDao.verifyUserAccessToDevice(user, device);

        Product product = organizationDao.getProductById(device.productId);

        blockingIOProcessor.executeDB(() -> {
            Response response;
            try {
                List<LogEvent> eventList;
                //todo introduce some query builder? jOOQ?
                if (eventType == null && isResolved == null) {
                    eventList = reportingDBManager.eventDBDao.getEvents(deviceId, from, to, offset, limit);
                } else {
                    if (eventType == null) {
                        eventList = reportingDBManager.eventDBDao.getEvents(
                                deviceId, from, to, offset, limit, isResolved);
                    } else if (isResolved == null) {
                        eventList = reportingDBManager.eventDBDao.getEvents(
                                deviceId, eventType, from, to, offset, limit);
                    } else {
                        eventList = reportingDBManager.eventDBDao.getEvents(
                                deviceId, eventType, from, to, offset, limit,
                                isResolved);
                    }
                }

                reportingDBManager.eventDBDao.upsertLastSeen(deviceId, user.email);

                if (product != null) {
                    joinLogEventName(product, eventList);
                }

                Map<LogEventCountKey, Integer> totalCounters =
                        reportingDBManager.eventDBDao.getEventsTotalCounters(from, to, deviceId);

                response = ok(new HashMap<String, Object>() {
                    {
                        put("totalCritical", totalCounters.getOrDefault(
                                new LogEventCountKey(deviceId, EventType.CRITICAL, false), 0));
                        put("totalWarning", totalCounters.getOrDefault(
                                new LogEventCountKey(deviceId, EventType.WARNING, false), 0));
                        put("totalResolved", totalResolved(totalCounters));
                        put("logEvents", eventList);
                    }
                });
            } catch (Exception e) {
                log.error("Error retrieving timeline for deviceId {}, limit {}, offset {}.",
                        deviceId, limit, offset, e);
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
