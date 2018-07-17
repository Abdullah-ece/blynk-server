package cc.blynk.server.web.handlers.logic.device.timeline;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.exceptions.NotAllowedException;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.RESOLVE_EVENT;
import static cc.blynk.server.internal.CommonByteBufUtil.json;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR_STRING;
import static cc.blynk.utils.StringUtils.split3;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class WebResolveLogEventLogic {

    private static final Logger log = LogManager.getLogger(WebResolveLogEventLogic.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;
    private final SessionDao sessionDao;

    public WebResolveLogEventLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
        this.sessionDao = holder.sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        //deviceId logEventId comment
        String[] messageParts = split3(message.body);

        int deviceId = Integer.parseInt(messageParts[0]);

        //logEventId comment
        long logEventId = Long.parseLong(messageParts[1]);
        String comment = messageParts.length == 3 ? messageParts[2] : null;

        User user = state.user;
        Device device = deviceDao.getById(deviceId);
        if (device == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            ctx.writeAndFlush(json(message.id, "Requested device not found."), ctx.voidPromise());
            return;
        }

        int orgId = organizationDao.getOrganizationIdByProductId(device.productId);
        if (!user.hasAccess(orgId)) {
            log.error("User {} tries to access device {} he has no access.", user.email, deviceId);
            throw new NotAllowedException("You have no access to this device.", message.id);
        }

        blockingIOProcessor.executeDB(() -> {
            ResponseMessage response;
            try {
                if (reportingDBManager.eventDBDao.resolveEvent(logEventId, user.name, comment)) {
                    response = ok(message.id);
                    Session session = sessionDao.userSession.get(state.userKey);
                    String body = messageParts[1] + BODY_SEPARATOR_STRING + user.email;
                    if (comment != null) {
                        body = body + BODY_SEPARATOR_STRING + comment;
                    }
                    session.sendToSelectedDeviceOnWeb(ctx.channel(), RESOLVE_EVENT, message.id, body, deviceId);
                } else {
                    log.warn("Event with id {} for user {} not resolved.", logEventId, user.email);
                    response = notAllowed(message.id);
                }
            } catch (Exception e) {
                log.error("Error marking event as resolved.", e);
                response = serverError(message.id);
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

    }
}
