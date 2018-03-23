package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.DBManager;
import cc.blynk.server.notifications.mail.MailWrapper;
import cc.blynk.server.notifications.push.GCMWrapper;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final OrganizationDao organizationDao;
    private final DeviceDao deviceDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;
    private final GCMWrapper gcmWrapper;
    private final MailWrapper mailWrapper;

    public HardwareLogEventLogic(Holder holder) {
        this.organizationDao = holder.organizationDao;
        this.deviceDao = holder.deviceDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
        this.gcmWrapper = holder.gcmWrapper;
        this.mailWrapper = holder.mailWrapper;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length == 0) {
            log.error("Log event command body is empty.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        Device device = state.device;

        Product product = organizationDao.getProductByIdOrNull(device.productId);
        if (product == null) {
            log.error("Product with id {} not exists.", device.productId);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String eventCode = split[0];
        Event event = product.findEventByCode(eventCode.hashCode());

        if (event == null) {
            log.error("Event with code {} not found in product {}.", eventCode, product.id);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        String description = split.length > 1 ? split[1] : null;

        blockingIOProcessor.executeDB(() -> {
            try {
                long now = System.currentTimeMillis();
                dbManager.insertEvent(device.id, event.getType(), now, eventCode.hashCode(), description);
                device.dataReceivedAt = now;
                ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
            } catch (Exception e) {
                log.error("Error inserting log event.", e);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            }
        });

        for (EventReceiver mailReceiver : event.emailNotifications) {
            MetaField metaField = device.findMetaFieldById(mailReceiver.metaFieldId);
            if (metaField != null) {
                String to = metaField.getNotificationEmail();
                if (to != null && !to.isEmpty()) {
                    mail(to, "You received event.", event.name);
                }
            }
        }

        for (EventReceiver pushReceiver : event.pushNotifications) {
            MetaField metaField = device.findMetaFieldById(pushReceiver.metaFieldId);
            if (metaField != null) {
                push(state, "You received new event : " + event.name);
            }
        }
    }

    private void push(HardwareStateHolder state, String message) {
        DashBoard dash = state.dash;
        Notification widget = dash.getWidgetByType(Notification.class);

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
