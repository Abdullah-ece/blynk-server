package cc.blynk.server.hardware.handlers.hardware;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.notifications.push.GCMWrapper;
import cc.blynk.utils.properties.ServerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

import static cc.blynk.server.internal.StateHolderUtil.getHardState;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/20/2015.
 *
 * Removes channel from session in case it became inactive (closed from client side).
 */
@ChannelHandler.Sharable
public class HardwareChannelStateHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(HardwareChannelStateHandler.class);

    private final SessionDao sessionDao;
    private final GCMWrapper gcmWrapper;
    private final String pushNotificationBody;
    private final DBManager dbManager;
    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public HardwareChannelStateHandler(Holder holder) {
        this.sessionDao = holder.sessionDao;
        this.gcmWrapper = holder.gcmWrapper;
        this.pushNotificationBody = holder.textHolder.pushNotificationBody;
        this.dbManager = holder.dbManager;
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        var hardwareChannel = ctx.channel();
        var state = getHardState(hardwareChannel);
        if (state != null) {
            var session = sessionDao.userSession.get(state.userKey);
            if (session != null) {
                var device = state.device;
                log.trace("Hardware channel disconnect for {}, dashId {}, deviceId {}, token {}.",
                        state.userKey, state.dash.id, device.id, device.token);

                Product product = organizationDao.getProductByIdOrNull(device.productId);
                int ignorePeriod = product == null ? 0 : product.getIgnorePeriod();
                if (ignorePeriod > 0) {
                    ctx.executor().schedule(
                            new DelayedOfflineSystemEvent(device), ignorePeriod, TimeUnit.MILLISECONDS);
                } else {
                    sentOfflineMessage(ctx, session, state.dash, device);
                }
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            log.trace("State handler. Hardware timeout disconnect. Event : {}. Closing.",
                    ((IdleStateEvent) evt).state());
            ctx.close();
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    private void sentOfflineMessage(ChannelHandlerContext ctx, Session session, DashBoard dashBoard, Device device) {
        //this is special case.
        //in case hardware quickly reconnects we do not mark it as disconnected
        //as it is already online after quick disconnect.
        //https://github.com/blynkkk/blynk-server/issues/403
        var isHardwareConnected = session.isHardwareConnected(dashBoard.id, device.id);
        if (!isHardwareConnected) {
            log.trace("Changing device status. Device {}, dashId {}", device, dashBoard.id);
            device.disconnected();
        }

        //do insert anyway, as device was disconnected even it was relogged quickly
        dbManager.insertSystemEvent(device.id, EventType.OFFLINE);

        if (!dashBoard.isActive || dashBoard.isNotificationsOff) {
            return;
        }

        var notification = dashBoard.getNotificationWidget();

        if (notification != null && notification.notifyWhenOffline) {
            sendPushNotification(ctx, notification, dashBoard.id, device);
        } else {
            session.sendOfflineMessageToApps(dashBoard.id, device.id);
        }

        session.sendOfflineMessageToWeb(dashBoard.id, device.id);
    }

    private void sendPushNotification(ChannelHandlerContext ctx,
                                      Notification notification, int dashId, Device device) {
        var deviceName = ((device == null || device.name == null) ? "device" : device.name);
        var message = pushNotificationBody.replace(ServerProperties.DEVICE_NAME, deviceName);
        if (notification.notifyWhenOfflineIgnorePeriod == 0 || device == null) {
            notification.push(gcmWrapper,
                    message,
                    dashId
            );
        } else {
            //delayed notification
            //https://github.com/blynkkk/blynk-server/issues/493
            ctx.executor().schedule(new DelayedPush(device, notification, message, dashId),
                    notification.notifyWhenOfflineIgnorePeriod, TimeUnit.MILLISECONDS);
        }
    }

    private final class DelayedOfflineSystemEvent implements Runnable {

        private final Device device;
        private final long submittedTime;

        DelayedOfflineSystemEvent(Device device) {
            this.device = device;
            this.submittedTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            if (submittedTime > device.connectTime) {
                dbManager.insertSystemEvent(device.id, EventType.OFFLINE);
            } else {
                log.debug("Hardware was logged. Delayed task skipped. Device id {}, token {}.",
                        device.id, device.token);
            }
        }
    }

    private final class DelayedPush implements Runnable {

        private final Device device;
        private final Notification notification;
        private final String message;
        private final int dashId;

        DelayedPush(Device device, Notification notification, String message, int dashId) {
            this.device = device;
            this.notification = notification;
            this.message = message;
            this.dashId = dashId;
        }

        @Override
        public void run() {
            final long now = System.currentTimeMillis();
            if (device.status == Status.OFFLINE
                    && now - device.disconnectTime >= notification.notifyWhenOfflineIgnorePeriod) {
                notification.push(gcmWrapper,
                        message,
                        dashId
                );
            }
        }
    }

}
