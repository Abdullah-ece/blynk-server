package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.core.dao.NotificationsDao;
import cc.blynk.server.core.model.profile.NotificationSettings;
import cc.blynk.server.core.processors.NotificationBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.properties.Placeholders;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.notificationInvalidBody;
import static cc.blynk.server.internal.CommonByteBufUtil.notificationNotAuthorized;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * Handler sends push notifications to Applications. Initiation is on hardware side.
 * Sends both to iOS and Android via Google Cloud Messaging service.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class PushLogic extends NotificationBase {

    private static final Logger log = LogManager.getLogger(PushLogic.class);

    private final NotificationsDao notificationsDao;

    public PushLogic(NotificationsDao notificationsDao, long notificationQuotaLimit) {
        super(notificationQuotaLimit);
        this.notificationsDao = notificationsDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        if (NotificationSettings.isWrongBody(message.body)) {
            log.debug("Notification message is empty or larger than limit.");
            ctx.writeAndFlush(notificationInvalidBody(message.id), ctx.voidPromise());
            return;
        }

        NotificationSettings notificationSettings = new NotificationSettings();

        if (notificationSettings.hasNoToken()) {
            log.debug("User has no access token provided for push widget.");
            ctx.writeAndFlush(notificationNotAuthorized(message.id), ctx.voidPromise());
            return;
        }

        long now = System.currentTimeMillis();
        checkIfNotificationQuotaLimitIsNotReached(now);

        String deviceName = state.device.name == null ? "" : state.device.name;
        String updatedBody = message.body.replace(Placeholders.DEVICE_NAME, deviceName);

        if (NotificationSettings.isWrongBody(updatedBody)) {
            log.debug("Notification message is larger than limit.");
            ctx.writeAndFlush(notificationInvalidBody(message.id), ctx.voidPromise());
            return;
        }

        int deviceId = state.device.id;
        log.trace("Sending push with message : '{}'.", message.body);
        notificationsDao.send(notificationSettings, updatedBody, deviceId);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
