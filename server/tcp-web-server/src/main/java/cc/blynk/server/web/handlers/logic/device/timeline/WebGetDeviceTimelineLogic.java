package cc.blynk.server.web.handlers.logic.device.timeline;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebGetDeviceTimelineLogic {

    private static final Logger log = LogManager.getLogger(WebGetDeviceTimelineLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;

    public WebGetDeviceTimelineLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        TimelineDTO timelineDTO = JsonParser.readAny(message.body, TimelineDTO.class);

        User user = state.user;
        if (timelineDTO == null) {
            log.error("Error parsing timeline request {} for {}.", message.body, user.email);
            ctx.writeAndFlush(json(message.id, "Error parsing timeline request"), ctx.voidPromise());
            return;
        }

        DeviceValue deviceValue = deviceDao.getDeviceValueById(timelineDTO.deviceId);
        if (deviceValue == null) {
            log.error("Device {} not found for {}.", timelineDTO.deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Requested device not found."), ctx.voidPromise());
            return;
        }

        // organizationDao.verifyUserAccessToDevice(user, device);
        Device device = deviceValue.device;
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
                response = makeASCIIStringMessage(message.command, message.id, responseJson);
            } catch (Exception e) {
                log.error("Error retrieving timeline for deviceId {}, limit {}, offset {}.",
                        timelineDTO.deviceId, timelineDTO.limit, timelineDTO.offset, e);
                response = json(message.id, "Error retrieving timeline for device.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });
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

}
