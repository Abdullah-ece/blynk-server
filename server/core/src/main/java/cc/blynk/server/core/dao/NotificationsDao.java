package cc.blynk.server.core.dao;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.profile.NotificationSettings;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.push.GCMWrapper;
import cc.blynk.server.notifications.sms.SMSWrapper;
import cc.blynk.server.notifications.twitter.TwitterWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

import static cc.blynk.utils.DateTimeUtils.LOG_EVENT_FORMATTER;
import static cc.blynk.utils.properties.Placeholders.DATA_TIME;
import static cc.blynk.utils.properties.Placeholders.DEVICE_NAME;
import static cc.blynk.utils.properties.Placeholders.DEVICE_URL;
import static cc.blynk.utils.properties.Placeholders.EVENT_DESCRIPTION;
import static cc.blynk.utils.properties.Placeholders.EVENT_NAME;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.12.18.
 */
public final class NotificationsDao {

    private static final Logger log = LogManager.getLogger(NotificationsDao.class);

    public final TwitterWrapper twitterWrapper;
    public final MailWrapper mailWrapper;
    public final BlockingIOProcessor blockingIOProcessor;
    private final GCMWrapper gcmWrapper;
    private final SMSWrapper smsWrapper;
    private final UserDao userDao;

    private final String deviceUrl;
    private final String eventLogEmailBody;

    public NotificationsDao(TwitterWrapper twitterWrapper,
                            MailWrapper mailWrapper,
                            GCMWrapper gcmWrapper,
                            SMSWrapper smsWrapper,
                            BlockingIOProcessor blockingIOProcessor,
                            UserDao userDao,
                            String deviceUrl,
                            String eventLogEmailBody) {
        this.twitterWrapper = twitterWrapper;
        this.mailWrapper = mailWrapper;
        this.gcmWrapper = gcmWrapper;
        this.smsWrapper = smsWrapper;
        this.blockingIOProcessor = blockingIOProcessor;
        this.userDao = userDao;
        this.deviceUrl = deviceUrl;
        this.eventLogEmailBody = eventLogEmailBody;
    }

    public void sendLogEventPushNotifications(Device device, Event event) {
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

    public void push(String to, String body, int deviceId) {
        User user = userDao.getByName(to);
        if (user == null) {
            log.trace("Receiver {} for push notifications not exists.", to);
            return;
        }
        push(user, body, deviceId);
    }

    public void push(User user, String body, int deviceId) {
        NotificationSettings notificationSettings = user.profile.settings.notificationSettings;
        if (notificationSettings.hasNoToken()) {
            log.trace("User {} has no access token provided for push notification.", user.email);
            return;
        }
        send(notificationSettings, body, deviceId);
    }

    public void send(NotificationSettings notificationSettings, String body, int deviceId) {
        if (NotificationSettings.isWrongBody(body)) {
            log.trace("Wrong push {} body.", body);
            return;
        }

        gcmWrapper.sendAndroid(notificationSettings.androidTokens, notificationSettings.priority, body, deviceId);
        gcmWrapper.sendIOS(notificationSettings.iOSTokens, notificationSettings.priority, body, deviceId);
    }

    public void sendLogEventEmails(Device device, Event event, String overrideDescription) {
        for (EventReceiver mailReceiver : event.emailNotifications) {
            MetaField metaField = device.findMetaFieldById(mailReceiver.metaFieldId);
            if (metaField != null) {
                String to = metaField.getNotificationEmail();
                if (to != null && !to.isEmpty()) {
                    String eventDescription = event.getDescription(overrideDescription);
                    email(to, eventDescription, event, device);
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

}
