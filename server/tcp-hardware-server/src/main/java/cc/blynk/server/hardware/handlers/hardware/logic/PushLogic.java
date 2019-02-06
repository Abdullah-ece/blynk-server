package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.NotificationsDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.profile.NotificationSettings;
import cc.blynk.server.core.processors.NotificationBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.utils.properties.Placeholders;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.notificationInvalidBody;
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
public final class PushLogic extends NotificationBase {

    private static final Logger log = LogManager.getLogger(PushLogic.class);

    private final NotificationsDao notificationsDao;
    private final UserDao userDao;

    public PushLogic(Holder holder) {
        super(holder.limits.notificationPeriodLimitSec);
        this.notificationsDao = holder.notificationsDao;
        this.userDao = holder.userDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        if (NotificationSettings.isWrongBody(message.body)) {
            log.debug("Notification message is empty or larger than limit.");
            ctx.writeAndFlush(notificationInvalidBody(message.id), ctx.voidPromise());
            return;
        }

        long now = System.currentTimeMillis();
        checkIfNotificationQuotaLimitIsNotReached(now);

        Device device = state.device;
        String deviceName = device.name == null ? "" : device.name;
        String updatedBody = message.body.replace(Placeholders.DEVICE_NAME, deviceName);

        if (NotificationSettings.isWrongBody(updatedBody)) {
            log.debug("Notification message is larger than limit.");
            ctx.writeAndFlush(notificationInvalidBody(message.id), ctx.voidPromise());
            return;
        }

        List<User> usersByOrgId = userDao.getAllUsersByOrgId(state.org.id);

        for (User user : usersByOrgId) {
            //todo check permissions
            sendPush(user, device.id, updatedBody);
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

    private void sendPush(User user, int deviceId, String body) {
        NotificationSettings notificationSettings = user.profile.settings.notificationSettings;

        if (notificationSettings.hasNoToken()) {
            log.trace("User {} has no push token provided for notification.", user.email);
            return;
        }

        log.trace("Sending push to {} with message : '{}'.", user.email, body);
        if (notificationSettings.hasNoToken()) {
            log.trace("User {} has no access token provided for push notification.", user.email);
            return;
        }
        notificationsDao.send(notificationSettings, body, deviceId);
    }

}
