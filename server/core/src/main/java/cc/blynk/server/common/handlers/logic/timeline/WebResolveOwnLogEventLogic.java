package cc.blynk.server.common.handlers.logic.timeline;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.PermissionBasedLogic;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_EDIT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_RESOLVE_EVENT;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class WebResolveOwnLogEventLogic implements PermissionBasedLogic<BaseUserStateHolder> {

    private static final Logger log = LogManager.getLogger(WebResolveOwnLogEventLogic.class);

    private final DeviceDao deviceDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final ReportingDBManager reportingDBManager;
    private final SessionDao sessionDao;

    WebResolveOwnLogEventLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.reportingDBManager = holder.reportingDBManager;
        this.sessionDao = holder.sessionDao;
    }

    @Override
    public int getPermission() {
        return OWN_DEVICES_EDIT;
    }

    private static String buildEventBody(long logEventId, String resolverEmail, String resolveComment) {
        if (resolveComment == null) {
            return "" + logEventId + BODY_SEPARATOR + resolverEmail;
        }
        return "" + logEventId + BODY_SEPARATOR + resolverEmail + BODY_SEPARATOR + resolveComment;
    }

    @Override
    public void messageReceived0(ChannelHandlerContext ctx, BaseUserStateHolder state, StringMessage message) {
        ResolveEventDTO resolveEventDTO = JsonParser.readAny(message.body, ResolveEventDTO.class);

        if (resolveEventDTO == null) {
            log.error("Wrong income resolve event command for {}.", state.user.email);
            throw new JsonException("Wrong income resolve event command.");
        }

        int deviceId = resolveEventDTO.deviceId;
        state.checkControlledDeviceIsSelected(deviceId);

        User user = state.user;
        DeviceValue deviceValue = deviceDao.getDeviceValueById(deviceId);
        if (deviceValue == null) {
            log.error("Device {} not found for {}.", deviceId, user.email);
            throw new JsonException("Requested device not found.");
        }

        int orgId = deviceValue.orgId;
        if (!user.hasAccess(orgId)) {
            log.error("User {} tries to access device {} he has no access.", user.email, deviceId);
            throw new JsonException("User tries to access device he has no access.");
        }

        blockingIOProcessor.executeEvent(() -> {
            MessageBase response;
            try {
                if (reportingDBManager.eventDBDao.resolveEvent(
                        resolveEventDTO.logEventId, user.name, resolveEventDTO.resolveComment)) {
                    String body = buildEventBody(
                            resolveEventDTO.logEventId, user.email, resolveEventDTO.resolveComment);
                    Session session = sessionDao.getOrgSession(orgId);
                    session.sendToSelectedDeviceOnWeb(ctx.channel(), WEB_RESOLVE_EVENT, message.id, body, deviceId);
                    response = ok(message.id);
                } else {
                    log.warn("Event with id {} for user {} not resolved.", resolveEventDTO.logEventId, user.email);
                    response = json(message.id, "Event for user not resolved.");
                }
            } catch (Exception e) {
                log.error("Error marking event as resolved.", e);
                response = json(message.id, "Error marking event as resolved.");
            }
            ctx.writeAndFlush(response, ctx.voidPromise());
        });

    }
}
