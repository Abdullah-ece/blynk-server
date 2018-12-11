package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.profile.NotificationSettings;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.events.Event;
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

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommand;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.DateTimeUtils.LOG_EVENT_FORMATTER;
import static cc.blynk.utils.StringUtils.split2;
import static cc.blynk.utils.properties.Placeholders.DATA_TIME;
import static cc.blynk.utils.properties.Placeholders.DEVICE_NAME;
import static cc.blynk.utils.properties.Placeholders.DEVICE_URL;
import static cc.blynk.utils.properties.Placeholders.EVENT_DESCRIPTION;
import static cc.blynk.utils.properties.Placeholders.EVENT_NAME;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareLogEventLogic {

    private static final Logger log = LogManager.getLogger(HardwareLogEventLogic.class);

    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;
    private final GCMWrapper gcmWrapper;
    private final MailWrapper mailWrapper;
    private final String deviceUrl;
    private final String eventLogEmailBody;
    private final SessionDao sessionDao;
    private final UserDao userDao;

    public HardwareLogEventLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
        this.gcmWrapper = holder.gcmWrapper;
        this.mailWrapper = holder.mailWrapper;
        this.deviceUrl = holder.props.getDeviceUrl();
        this.eventLogEmailBody = holder.textHolder.logEventMailBody;
        this.sessionDao = holder.sessionDao;
        this.userDao = holder.userDao;
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

        var session = sessionDao.getOrgSession(state.orgId);
        var bodyForWeb = event.getType() + StringUtils.BODY_SEPARATOR_STRING + message.body;
        session.sendToSelectedDeviceOnWeb(HARDWARE_LOG_EVENT, message.id, bodyForWeb, device.id);

        var desc = splitBody.length > 1 ? splitBody[1].trim() : null;
        blockingIOProcessor.executeEvent(() -> {
            try {
                long now = System.currentTimeMillis();
                reportingDBManager.insertEvent(device.id, event.getType(), now, eventCode.hashCode(), desc);
                device.setLastReportedAt(now);
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
                    String eventDescription = event.getDescription(desc);
                    email(to, eventDescription, event, device);
                }
            }
        }

        for (EventReceiver pushReceiver : event.pushNotifications) {
            var metaField = device.findMetaFieldById(pushReceiver.metaFieldId);
            if (metaField != null) {
                String to = metaField.getNotificationEmail();
                if (to != null && !to.isEmpty()) {
                    push(to, "You received new event : " + event.name, device.id);
                }
            }
        }
    }

    private void email(String to, String eventDescription, Event event, Device device) {
        String subj = device.name + ": " + event.name;
        String body = eventLogEmailBody
                .replace(DEVICE_URL, deviceUrl + device.id)
                .replace(DEVICE_NAME, device.name)
                .replace(DATA_TIME, LOG_EVENT_FORMATTER.format(LocalDateTime.now()))
                .replace(EVENT_NAME, event.name)
                .replace(EVENT_DESCRIPTION, eventDescription);
        blockingIOProcessor.execute(() -> {
            try {
                mailWrapper.sendHtml(to, subj, body);
            } catch (Exception e) {
                log.error("Error sending email from hardware. From user {}, to : {}. Reason : {}",
                        to, e.getMessage());
            }
        });
    }

    private void push(String to, String body, int deviceId) {
        User user = userDao.getByName(to);
        if (user == null) {
            log.trace("Receiver {} for push notifications not exists.", to);
            return;
        }

        NotificationSettings notificationSettings = user.profile.settings.notificationSettings;

        gcmWrapper.sendAndroid(notificationSettings.androidTokens, notificationSettings.priority, body, deviceId);
        gcmWrapper.sendIOS(notificationSettings.iOSTokens, notificationSettings.priority, body, deviceId);
    }

}
