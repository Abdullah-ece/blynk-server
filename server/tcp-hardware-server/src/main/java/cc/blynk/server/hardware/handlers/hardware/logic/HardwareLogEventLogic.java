package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.NotificationsDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;

    private final SessionDao sessionDao;
    private final NotificationsDao notificationsDao;

    public HardwareLogEventLogic(Holder holder) {
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;

        this.sessionDao = holder.sessionDao;
        this.notificationsDao = holder.notificationsDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        var splitBody = split2(message.body);

        if (splitBody.length == 0) {
            log.error("Log event command body is empty.");
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        var device = state.device;
        var product = state.product;

        var eventCode = splitBody[0];
        Event event = product.findEventByCode(eventCode.hashCode());

        if (event == null) {
            log.error("Event with code {} not found in product {}.", eventCode, product.id);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        Session session = sessionDao.getOrgSession(state.orgId);
        String bodyForWeb = event.getType() + StringUtils.BODY_SEPARATOR_STRING + message.body;
        session.sendToSelectedDeviceOnWeb(HARDWARE_LOG_EVENT, message.id, bodyForWeb, device.id);

        String desc = splitBody.length > 1 ? splitBody[1].trim() : null;
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

        if (event.isNotificationsEnabled) {
            String finalDesc = event.getDescription(desc);
            notificationsDao.sendLogEventEmails(device, event, finalDesc);
            notificationsDao.sendLogEventPushNotifications(device, event, finalDesc);
        }
    }

}
