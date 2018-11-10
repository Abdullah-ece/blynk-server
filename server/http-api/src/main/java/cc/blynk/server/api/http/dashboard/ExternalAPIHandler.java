package cc.blynk.server.api.http.dashboard;

import cc.blynk.core.http.Response;
import cc.blynk.core.http.TokenBaseHttpHandler;
import cc.blynk.core.http.annotation.Consumes;
import cc.blynk.core.http.annotation.Context;
import cc.blynk.core.http.annotation.GET;
import cc.blynk.core.http.annotation.Metric;
import cc.blynk.core.http.annotation.POST;
import cc.blynk.core.http.annotation.PUT;
import cc.blynk.core.http.annotation.Path;
import cc.blynk.core.http.annotation.PathParam;
import cc.blynk.core.http.annotation.QueryParam;
import cc.blynk.server.Holder;
import cc.blynk.server.api.http.pojo.EmailPojo;
import cc.blynk.server.api.http.pojo.PinData;
import cc.blynk.server.api.http.pojo.PushMessagePojo;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.TokenValue;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.storage.value.SinglePinStorageValue;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.notifications.Mail;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.processors.EventorProcessor;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandBodyException;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.db.dao.descriptor.TableDataMapper;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.push.GCMWrapper;
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
import static cc.blynk.server.core.protocol.enums.Command.HTTP_EMAIL;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_GET_PIN_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_IS_HARDWARE_CONNECTED;
import static cc.blynk.server.core.protocol.enums.Command.HTTP_NOTIFY;
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
    private final MailWrapper mailWrapper;
    private final GCMWrapper gcmWrapper;
    private final ReportingDiskDao reportingDiskDao;
    private final ReportingDBManager reportingDBManager;
    private final EventorProcessor eventorProcessor;
    private final DeviceDao deviceDao;

    public ExternalAPIHandler(Holder holder, String rootPath) {
        super(holder.tokenManager, holder.sessionDao, holder.stats, rootPath);
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.organizationDao = holder.organizationDao;
        this.mailWrapper = holder.mailWrapper;
        this.gcmWrapper = holder.gcmWrapper;
        this.reportingDiskDao = holder.reportingDiskDao;
        this.reportingDBManager = holder.reportingDBManager;
        this.eventorProcessor = holder.eventorProcessor;
        this.deviceDao = holder.deviceDao;
    }

    @GET
    @Path("/{token}/logEvent")
    public Response logEvent(@Context ChannelHandlerContext ctx,
                             @PathParam("token") String token,
                             @QueryParam("code") String eventCode,
                             @QueryParam("description") String description) {

        var tokenValue = tokenManager.getTokenValueByToken(token);

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
                device.pinStorage.setDataReceivedAt(now);
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
        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        int deviceId = tokenValue.device.id;

        Session session = sessionDao.getOrgSession(tokenValue.orgId);

        return ok(session.isHardwareConnected(deviceId));
    }

    @GET
    @Path("/{token}/get/{pin}")
    @Metric(HTTP_GET_PIN_DATA)
    public Response getWidgetPinDataNew(@PathParam("token") String token,
                                        @PathParam("pin") String pinString) {
        return getWidgetPinData(token, pinString);
    }

    private static String makeBody(DashBoard dash, int deviceId, short pin, PinType pinType, String pinValue) {
        Widget widget = dash.findWidgetByPin(deviceId, pin, pinType);
        if (widget == null) {
            return DataStream.makeHardwareBody(pinType, pin, pinValue);
        } else {
            if (widget instanceof OnePinWidget) {
                return ((OnePinWidget) widget).makeHardwareBody();
            } else {
                return ((MultiPinWidget) widget).makeHardwareBody(pin, pinType);
            }
        }
    }

    private Response getWidgetPinData(@PathParam("token") String token,
                                      @PathParam("pin") String pinString) {

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

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
            PinStorageValue value = device.pinStorage.get(pin, pinType);
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

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        int deviceId = tokenValue.device.id;
        DashBoard dash = tokenValue.dash;

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
        tokenValue.device.updateValue(dash, pin, widgetProperty, values[0]);

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

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        User user = tokenValue.user;
        int deviceId = tokenValue.device.id;

        DashBoard dash = tokenValue.dash;

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
                    log.error("Error insert knight record.", e);
                    ctx.writeAndFlush(serverError("Error insert knight record. " + e.getMessage()));
                }
            });
            return null;
        }

        final long now = System.currentTimeMillis();

        String pinValue = String.join(StringUtils.BODY_SEPARATOR_STRING,
                Arrays.copyOf(pinValues, pinValues.length, String[].class));

        Device device = deviceDao.getByIdOrThrow(deviceId);

        reportingDiskDao.process(user, dash, device, pin, pinType, pinValue, now);
        device.webDashboard.update(deviceId, pin, pinType, pinValue);
        device.updateValue(dash, pin, pinType, pinValue, now);

        String body = makeBody(dash, deviceId, pin, pinType, pinValue);

        Session session = sessionDao.getOrgSession(tokenValue.orgId);
        if (session == null) {
            log.debug("No session for user {}.", user.email);
            return Response.ok();
        }

        eventorProcessor.process(user, session, dash, device, pin, pinType, pinValue);

        session.sendMessageToHardware(HARDWARE, 111, body, deviceId);
        session.sendToApps(HARDWARE, 111, deviceId, body);
        session.sendToSelectedDeviceOnWeb(HARDWARE, 111, body, deviceId);

        return ok();
    }

    @POST
    @Path("/{token}/notify")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Metric(HTTP_NOTIFY)
    public Response notify(@PathParam("token") String token,
                           PushMessagePojo message) {

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        User user = tokenValue.user;

        if (message == null || Notification.isWrongBody(message.body)) {
            log.debug("Notification body is wrong. '{}'", message == null ? "" : message.body);
            return Response.badRequest("Body is empty or larger than 255 chars.");
        }

        DashBoard dash = tokenValue.dash;

        if (!dash.isActive) {
            log.debug("Project is not active.");
            return Response.badRequest("Project is not active.");
        }

        Notification notification = dash.getWidgetByType(Notification.class);

        if (notification == null || notification.hasNoToken()) {
            log.debug("No notification tokens.");
            if (notification == null) {
                return Response.badRequest("No notification widget.");
            } else {
                return Response.badRequest("Notification widget not initialized.");
            }
        }

        log.trace("Sending push for user {}, with message : '{}'.", user.email, message.body);
        notification.push(gcmWrapper, message.body, dash.id);

        return Response.ok();
    }

    @POST
    @Path("/{token}/email")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Metric(HTTP_EMAIL)
    public Response email(@PathParam("token") String token,
                          EmailPojo message) {

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        DashBoard dash = tokenValue.dash;

        if (dash == null || !dash.isActive) {
            log.debug("Project is not active.");
            return Response.badRequest("Project is not active.");
        }

        Mail mail = dash.getWidgetByType(Mail.class);

        if (mail == null) {
            log.debug("No email widget.");
            return Response.badRequest("No email widget.");
        }

        if (message == null
                || message.subj == null || message.subj.isEmpty()
                || message.to == null || message.to.isEmpty()) {
            log.debug("Email body empty. '{}'", message);
            return Response.badRequest("Email body is wrong. Missing or empty fields 'to', 'subj'.");
        }

        log.trace("Sending Mail for user {}, with message : '{}'.", tokenValue.user.email, message.subj);
        mail(tokenValue.user.email, message.to, message.subj, message.title);

        return Response.ok();
    }

    private void mail(String email, String to, String subj, String body) {
        blockingIOProcessor.execute(() -> {
            try {
                mailWrapper.sendText(to, subj, body);
            } catch (Exception e) {
                log.error("Error sending email from HTTP. From : '{}', to : '{}'. Reason : {}",
                        email, to, e.getMessage());
            }
        });
    }

    @PUT
    @Path("/{token}/extra/pin/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Metric(HTTP_UPDATE_PIN_DATA)
    public Response updateWidgetPinData(@PathParam("token") String token,
                                        @PathParam("pin") String pinString,
                                        PinData[] pinsData) {

        if (pinsData.length == 0) {
            log.debug("No pin for update provided.");
            return Response.badRequest("No pin for update provided.");
        }

        TokenValue tokenValue = tokenManager.getTokenValueByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        User user = tokenValue.user;
        int deviceId = tokenValue.device.id;

        DashBoard dash = tokenValue.dash;

        PinType pinType;
        short pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = NumberUtil.parsePin(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        Device device = deviceDao.getByIdOrThrow(deviceId);
        for (PinData pinData : pinsData) {
            reportingDiskDao.process(user, dash, device, pin, pinType, pinData.value, pinData.timestamp);
        }

        long now = System.currentTimeMillis();
        device.updateValue(dash, pin, pinType, pinsData[0].value, now);

        String body = makeBody(dash, deviceId, pin, pinType, pinsData[0].value);

        if (body != null) {
            Session session = sessionDao.getOrgSession(tokenValue.orgId);
            if (session == null) {
                log.error("No session for user {}.", user.email);
                return Response.ok();
            }
            session.sendMessageToHardware(HARDWARE, 111, body, deviceId);

            if (dash.isActive) {
                session.sendToApps(HARDWARE, 111, deviceId, body);
            }
        }

        return Response.ok();
    }
}
