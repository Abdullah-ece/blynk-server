package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.push.GCMWrapper;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareLogEventLogic {

    private static final Logger log = LogManager.getLogger(HardwareLogEventLogic.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma, MMM d, yyyy");

    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;
    private final GCMWrapper gcmWrapper;
    private final MailWrapper mailWrapper;
    private final String deviceUrl;
    private final String eventLogEmailBody;
    private final SessionDao sessionDao;

    public HardwareLogEventLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
        this.gcmWrapper = holder.gcmWrapper;
        this.mailWrapper = holder.mailWrapper;
        this.deviceUrl = holder.props.getDeviceUrl();
        this.eventLogEmailBody = holder.textHolder.logEventMailBody;
        this.sessionDao = holder.sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        var splitBody = split2(message.body);

        if (splitBody.length == 0) {
            log.error("Log event command body is empty.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        var device = state.device;

        var product = organizationDao.getProductById(device.productId);
        if (product == null) {
            log.error("Product with id {} not exists.", device.productId);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        var eventCode = splitBody[0];
        var event = product.findEventByCode(eventCode.hashCode());

        if (event == null) {
            log.error("Event with code {} not found in product {}.", eventCode, product.id);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        var session = sessionDao.userSession.get(state.user.email);
        var bodyForWeb = event.getType() + StringUtils.BODY_SEPARATOR_STRING + message.body;
        session.sendToSelectedDeviceOnWeb(HARDWARE_LOG_EVENT, message.id, bodyForWeb, device.id);

        var desc = splitBody.length > 1 ? splitBody[1].trim() : null;
        blockingIOProcessor.executeEvent(() -> {
            try {
                long now = System.currentTimeMillis();
                reportingDBManager.insertEvent(device.id, event.getType(), now, eventCode.hashCode(), desc);
                device.dataReceivedAt = now;
                ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error inserting log event.", e);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            }
        });

        for (EventReceiver mailReceiver : event.emailNotifications) {
            var metaField = device.findMetaFieldById(mailReceiver.metaFieldId);
            if (metaField != null) {
                String to = metaField.getNotificationEmail();
                if (to != null && !to.isEmpty()) {
                    String eventDescription;
                    if (desc == null || desc.isEmpty()) {
                        eventDescription = event.description == null ? "" : event.description;
                    } else {
                        eventDescription = desc;
                    }
                    mail(to, device.name + ": " + event.name,
                            eventLogEmailBody
                                    .replace("{DEVICE_URL}", deviceUrl + device.id)
                                    .replace("{DEVICE_NAME}", device.name)
                                    .replace("{DATE_TIME}", formatter.format(LocalDateTime.now()))
                                    .replace("{EVENT_NAME}", event.name)
                                    .replace("{EVENT_DESCRIPTION}", eventDescription)
                    );
                }
            }
        }

        for (EventReceiver pushReceiver : event.pushNotifications) {
            var metaField = device.findMetaFieldById(pushReceiver.metaFieldId);
            if (metaField != null) {
                push(state, "You received new event : " + event.name);
            }
        }
    }

    private void push(HardwareStateHolder state, String message) {
        var dash = state.dash;
        var widget = dash.getWidgetByType(Notification.class);

        if (widget == null || widget.hasNoToken()) {
            log.debug("User has no access token provided for push widget for event log.");
            return;
        }
        widget.push(gcmWrapper, message, state.dash.id);
    }

    private void mail(String to, String subj, String body) {
        blockingIOProcessor.execute(() -> {
            try {
                mailWrapper.sendHtml(to, subj, body);
            } catch (Exception e) {
                log.error("Error sending email from hardware. From user {}, to : {}. Reason : {}", to, e.getMessage());
            }
        });
    }

}
