package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.Response;
import cc.blynk.core.http.TokenBaseHttpHandler;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.Metric;
import cc.blynk.core.http.annotation.PUT;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceTokenValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.storage.value.SinglePinStorageValue;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandBodyException;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.db.dao.descriptor.TableDataMapper;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import cc.blynk.utils.http.MediaType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;

import static cc.blynk.core.http.Response.badRequest;
import static cc.blynk.core.http.Response.ok;
import static cc.blynk.core.http.Response.serverError;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_GET_PIN_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_IS_HARDWARE_CONNECTED;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_UPDATE_PIN_DATA;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.12.15.
 */
@Path("")
@ChannelHandler.Sharable
public class ExternalAPIHandler extends TokenBaseHttpHandler {

    private static final Logger log = LogManager.getLogger(ExternalAPIHandler.class);
    private final BlockingIOProcessor blockingIOProcessor;
    private final OrganizationDao organizationDao;
    private final ReportingDiskDao reportingDiskDao;
    private final ReportingDBManager reportingDBManager;

    public ExternalAPIHandler(Holder holder, String rootPath) {
        super(holder.deviceDao, holder.sessionDao, holder.stats, rootPath);
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.organizationDao = holder.organizationDao;
        this.reportingDiskDao = holder.reportingDiskDao;
        this.reportingDBManager = holder.reportingDBManager;
    }

    @GET
    @Path("/{token}/logEvent")
    public Response logEvent(@Context ChannelHandlerContext ctx,
                             @PathParam("token") String token,
                             @QueryParam("code") String eventCode,
                             @QueryParam("description") String description) {

        var tokenValue = deviceDao.getDeviceTokenValue(token);

        var device = tokenValue.device;

        if (eventCode == null) {
            log.error("Event code is not provided.");
            return (badRequest("Event code is not provided."));
        }

        var product = organizationDao.getProductById(device.productId);
        if (product == null) {
            log.error("Product with id {} not exists.", device.productId);
            return (badRequest("Product not exists for device."));
        }

        var event = product.findEventByCode(eventCode.hashCode());

        if (event == null) {
            log.error("Event with code {} not found in product {}.", eventCode, product.id);
            return badRequest("Event with code not found in product.");
        }

        var session = sessionDao.getOrgSession(tokenValue.orgId);
        var bodyForWeb = event.getType() + StringUtils.BODY_SEPARATOR_STRING + eventCode;
        session.sendToSelectedDeviceOnWeb(HARDWARE_LOG_EVENT, 111, bodyForWeb, device.id);

        blockingIOProcessor.executeDB(() -> {
            try {
                long now = System.currentTimeMillis();
                reportingDBManager.insertEvent(device.id, event.getType(), now, eventCode.hashCode(), description);
                device.setLastReportedAt(now);
                ctx.writeAndFlush(ok(), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error inserting log event.", e);
                ctx.writeAndFlush(serverError("Error inserting log event."), ctx.voidPromise());
            }
        });

        return null;
    }

    @GET
    @Path("/{token}/isHardwareConnected")
    @Metric(HTTP_IS_HARDWARE_CONNECTED)
    public Response isHardwareConnected(@PathParam("token") String token) {
        DeviceTokenValue tokenValue = deviceDao.getDeviceTokenValue(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        int deviceId = tokenValue.device.id;

        Session session = sessionDao.getOrgSession(tokenValue.orgId);

        return ok(session.isHardwareConnected(deviceId));
    }

    @GET
    @Path("/{token}/id")
    @Metric(HTTP_GET_DEVICE)
    public Response getDeviceJson(@PathParam("token") String token) {
        DeviceTokenValue tokenValue = deviceDao.getDeviceTokenValue(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        Device device = tokenValue.device;
        return ok(device, true);
    }

    @GET
    @Path("/{token}/get/{pin}")
    @Metric(HTTP_GET_PIN_DATA)
    public Response getWidgetPinDataNew(@PathParam("token") String token,
                                        @PathParam("pin") String pinString) {
        return getWidgetPinData(token, pinString);
    }

    private Response getWidgetPinData(@PathParam("token") String token,
                                      @PathParam("pin") String pinString) {

        DeviceTokenValue tokenValue = deviceDao.getDeviceTokenValue(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        int deviceId = tokenValue.device.id;

        PinType pinType;
        short pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = NumberUtil.parsePin(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        Device device = deviceDao.getById(deviceId);

        if (device != null) {
            PinStorageValue value = device.getValue(pin, pinType);
            if (value == null) {
                log.debug("Requested {} and pin {} not found.", token, pinString);
                return Response.badRequest("Requested pin doesn't exist in the app.");
            }
            if (value instanceof SinglePinStorageValue) {
                return ok(JsonParser.valueToJsonAsString((SinglePinStorageValue) value));
            } else {
                return ok(JsonParser.valueToJsonAsString(value.values()));
            }
        }

        log.debug("Requested {} and pin {} not found.", token, pinString);
        return Response.badRequest("Requested pin doesn't exist in the app.");
    }

    //todo it is a bit ugly right now. could be simplified by passing map of query params.
    @GET
    @Path("/{token}/update/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Metric(HTTP_UPDATE_PIN_DATA)
    public Response updateWidgetPinDataViaGet(@Context ChannelHandlerContext ctx,
                                              @PathParam("token") String token,
                                              @PathParam("pin") String pinString,
                                              @QueryParam("value") String[] pinValues,
                                              @QueryParam("label") String labelValue,
                                              @QueryParam("labels") String labelsValue,
                                              @QueryParam("color") String colorValue,
                                              @QueryParam("onLabel") String onLabelValue,
                                              @QueryParam("offLabel") String offLabelValue,
                                              @QueryParam("isOnPlay") String isOnPlay) {

        if (pinValues != null) {
            return updateWidgetPinData(ctx, token, pinString, pinValues);
        }
        if (labelValue != null) {
            return updateWidgetProperty(token, pinString, "label", labelValue);
        }
        if (labelsValue != null) {
            return updateWidgetProperty(token, pinString, "labels", labelsValue);
        }
        if (colorValue != null) {
            return updateWidgetProperty(token, pinString, "color", colorValue);
        }
        if (onLabelValue != null) {
            return updateWidgetProperty(token, pinString, "onLabel", onLabelValue);
        }
        if (offLabelValue != null) {
            return updateWidgetProperty(token, pinString, "offLabel", offLabelValue);
        }
        if (isOnPlay != null) {
            return updateWidgetProperty(token, pinString, "isOnPlay", isOnPlay);
        }

        return Response.badRequest("Wrong request format.");
    }

    @PUT
    @Path("/{token}/update/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Metric(HTTP_UPDATE_PIN_DATA)
    public Response updateWidgetPinDataNew(@Context ChannelHandlerContext ctx,
                                           @PathParam("token") String token,
                                           @PathParam("pin") String pinString,
                                           String[] pinValues) {
        return updateWidgetPinData(ctx, token, pinString, pinValues);
    }

    public Response updateWidgetProperty(String token,
                                         String pinString,
                                         String property,
                                         String... values) {
        if (values.length == 0) {
            log.debug("No properties for update provided.");
            return Response.badRequest("No properties for update provided.");
        }

        DeviceTokenValue tokenValue = deviceDao.getDeviceTokenValue(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        int deviceId = tokenValue.device.id;

        PinType pinType;
        short pin;
        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = NumberUtil.parsePin(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        if (pinType != PinType.VIRTUAL) {
            log.debug("Only virtual pins supported for SetWidgetProperty command.");
            return Response.badRequest("Only virtual pins supported for SetWidgetProperty command.");
        }

        WidgetProperty widgetProperty = WidgetProperty.getProperty(property);
        if (widgetProperty == null) {
            log.debug("Property not exists. Property : {}", property);
            return badRequest("Property not exists.");
        }

        //todo for now supporting only single property
        tokenValue.device.updateValue(pin, widgetProperty, values[0]);

        Session session = sessionDao.getOrgSession(tokenValue.orgId);
        session.sendToApps(SET_WIDGET_PROPERTY, 111,
                deviceId, "" + pin + BODY_SEPARATOR + property + BODY_SEPARATOR + values[0]);
        return ok();
    }

    @PUT
    @Path("/{token}/pin/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Metric(HTTP_UPDATE_PIN_DATA)
    public Response updateWidgetPinData(@Context ChannelHandlerContext ctx,
                                        @PathParam("token") String token,
                                        @PathParam("pin") String pinString,
                                        String[] pinValues) {

        if (pinValues.length == 0) {
            log.debug("No pin for update provided.");
            return Response.badRequest("No pin for update provided.");
        }

        DeviceTokenValue tokenValue = deviceDao.getDeviceTokenValue(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        int deviceId = tokenValue.device.id;

        PinType pinType;
        short pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = NumberUtil.parsePin(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        TableDescriptor tableDescriptor = TableDescriptor.BLYNK_DEFAULT_INSTANCE;
        if (TableDescriptor.BLYNK_DEFAULT_INSTANCE != tableDescriptor) {
            blockingIOProcessor.executeDB(() -> {
                try {
                    TableDataMapper tableDataMapper = new TableDataMapper(
                            tableDescriptor,
                            deviceId, pin, pinType, LocalDateTime.now(),
                            pinValues);
                    reportingDBManager.reportingDBDao.insertDataPoint(tableDataMapper);
                    ctx.writeAndFlush(ok());
                } catch (Exception e) {
                    log.error("Error insert record.", e);
                    ctx.writeAndFlush(serverError("Error insert record. " + e.getMessage()));
                }
            });
            return null;
        }

        final long now = System.currentTimeMillis();

        String pinValue = String.join(StringUtils.BODY_SEPARATOR_STRING,
                Arrays.copyOf(pinValues, pinValues.length, String[].class));

        Device device = deviceDao.getByIdOrThrow(deviceId);

        reportingDiskDao.process(device, pin, pinType, pinValue, now);
        device.updateValue(pin, pinType, pinValue, now);

        String body = DataStream.makeHardwareBody(pinType, pin, pinValue);

        Session session = sessionDao.getOrgSession(tokenValue.orgId);
        if (session == null) {
            log.debug("No session for hardware {}.", device.id);
            return Response.ok();
        }

        session.sendMessageToHardware(HARDWARE, 111, body, deviceId);
        session.sendToApps(HARDWARE, 111, deviceId, body);
        session.sendToSelectedDeviceOnWeb(HARDWARE, 111, body, deviceId);

        return ok();
    }
}
