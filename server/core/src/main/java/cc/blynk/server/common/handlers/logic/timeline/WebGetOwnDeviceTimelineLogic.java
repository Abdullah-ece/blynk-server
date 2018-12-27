package cc.blynk.server.common.handlers.logic.timeline;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebGetOwnDeviceTimelineLogic implements PermissionBasedLogic<BaseUserStateHolder> {

    private final DeviceDao deviceDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;

    WebGetOwnDeviceTimelineLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
    }

    private static void joinLogEventName(Product product, List<LogEvent> logEvents) {
        for (LogEvent logEvent : logEvents) {
            Event templateEvent = product.findEventByCode(logEvent.eventHashcode);
            if (templateEvent == null) {
                log.warn("Can't find template for event: {}", logEvent);
            } else {
                logEvent.update(templateEvent);
            }
        }
    }

    @Override
    public int getPermission() {
        return OWN_DEVICES_VIEW;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        TimelineDTO timelineDTO = JsonParser.readAny(message.body, TimelineDTO.class);

        User user = state.user;
        if (timelineDTO == null) {
            log.error("Error parsing timeline request {} for {}.", message.body, user.email);
            ctx.writeAndFlush(json(message.id, "Error parsing timeline request"), ctx.voidPromise());
            return;
        }

        state.checkControlledDeviceIsSelected(timelineDTO.deviceId);

        DeviceValue deviceValue = deviceDao.getDeviceValueById(timelineDTO.deviceId);
        if (deviceValue == null) {
            log.error("Device {} not found for {}.", timelineDTO.deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Requested device not found."), ctx.voidPromise());
            return;
        }

        Product product = deviceValue.product;

        blockingIOProcessor.executeDB(() -> {
            MessageBase response;
            try {
                List<LogEvent> eventList;
                //todo introduce some query builder? jOOQ?
                if (timelineDTO.eventType == null && timelineDTO.isResolved == null) {
                    eventList = reportingDBManager.eventDBDao.getEvents(timelineDTO.deviceId,
                            timelineDTO.from, timelineDTO.to, timelineDTO.offset, timelineDTO.limit);
                } else {
                    if (timelineDTO.eventType == null) {
                        eventList = reportingDBManager.eventDBDao.getEvents(
                                timelineDTO.deviceId, timelineDTO.from, timelineDTO.to,
                                timelineDTO.offset, timelineDTO.limit, timelineDTO.isResolved);
                    } else if (timelineDTO.isResolved == null) {
                        eventList = reportingDBManager.eventDBDao.getEvents(
                                timelineDTO.deviceId, timelineDTO.eventType,
                                timelineDTO.from, timelineDTO.to, timelineDTO.offset, timelineDTO.limit);
                    } else {
                        eventList = reportingDBManager.eventDBDao.getEvents(
                                timelineDTO.deviceId, timelineDTO.eventType, timelineDTO.from,
                                timelineDTO.to, timelineDTO.offset, timelineDTO.limit,
                                timelineDTO.isResolved);
                    }
                }

                reportingDBManager.eventDBDao.upsertLastSeen(timelineDTO.deviceId, user.email);

                if (product != null) {
                    joinLogEventName(product, eventList);
                }

                Map<LogEventCountKey, Integer> totalCounters =
                        reportingDBManager.eventDBDao.getEventsTotalCounters(
                                timelineDTO.from, timelineDTO.to, timelineDTO.deviceId);

                String responseJson = new TimelineResponseDTO(timelineDTO.deviceId,
                        totalCounters, eventList).toString();
                response = makeUTF8StringMessage(message.command, message.id, responseJson);
            } catch (Exception e) {
                log.error("Error retrieving timeline for deviceId {}, limit {}, offset {}.",
                        timelineDTO.deviceId, timelineDTO.limit, timelineDTO.offset, e);
                response = json(message.id, "Error retrieving timeline for device.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });
    }

}
