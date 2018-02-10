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
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.notifications.push.GCMWrapper;
import io.netty.channel.Channel;
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
    private final DBManager dbManager;
    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;

    public HardwareChannelStateHandler(Holder holder) {
        this.sessionDao = holder.sessionDao;
        this.gcmWrapper = holder.gcmWrapper;
        this.dbManager = holder.dbManager;
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel hardwareChannel = ctx.channel();
        HardwareStateHolder state = getHardState(hardwareChannel);
        if (state != null) {
            Session session = sessionDao.userSession.get(state.userKey);
            if (session != null) {
                session.removeHardChannel(hardwareChannel);
                Device device = state.device;
                log.trace("Hardware channel disconnect for {}, dashId {}, deviceId {}, token {}.",
                        state.userKey, state.dash.id, device.id, device.token);
                sentOfflineMessage(ctx, session, state.dash, device);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
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
        boolean isHardwareConnected = session.isHardwareConnected(dashBoard.id, device.id);
        if (!isHardwareConnected) {
            log.trace("Changing device status. DeviceId {}, dashId {}", device.id, dashBoard.id);
            disconnect(ctx, device);
        }

        if (!dashBoard.isActive || dashBoard.isNotificationsOff) {
            return;
        }

        Notification notification = dashBoard.getWidgetByType(Notification.class);

        if (notification != null && notification.notifyWhenOffline) {
            sendPushNotification(ctx, notification, dashBoard.id, device);
        } else {
            session.sendOfflineMessageToApps(dashBoard.id, device.id);
        }
    }

    private void disconnect(ChannelHandlerContext ctx, Device device) {
        log.trace("Disconnected device: {}", device);
        device.disconnected();

        Product product = organizationDao.getProductByIdOrNull(device.productId);
        if (product != null) {
            int ignorePeriod = product.getIgnorePeriod();
            //means no ignore period
            if (ignorePeriod == 0) {
                dbManager.insertSystemEvent(device.id, EventType.OFFLINE);
            } else {
                ctx.executor().schedule(new DelayedSystemEvent(device, ignorePeriod),
                        ignorePeriod, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void sendPushNotification(ChannelHandlerContext ctx,
                                      Notification notification, int dashId, Device device) {
        String deviceName = ((device == null || device.name == null) ? "device" : device.name);
        String message = "Your " + deviceName + " went offline.";
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

    private final class DelayedSystemEvent implements Runnable {

        private final Device device;
        private final int ignorePeriod;

        DelayedSystemEvent(Device device, int ignorePeriod) {
            this.device = device;
            this.ignorePeriod = ignorePeriod;
        }

        @Override
        public void run() {
            final long now = System.currentTimeMillis();
            if (device.status == Status.OFFLINE && now - device.disconnectTime > ignorePeriod) {
                dbManager.insertSystemEvent(device.id, EventType.OFFLINE);
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
