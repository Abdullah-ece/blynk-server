package cc.blynk.server.application.handlers.main.logic.web;

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
import cc.blynk.server.core.session.WebAppStateHolder;
import cc.blynk.server.db.DBManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.core.protocol.enums.Command.RESOLVE_EVENT;
import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public class ResolveWebEventHandler {

    private static final Logger log = LogManager.getLogger(ResolveWebEventHandler.class);

    private final DeviceDao deviceDao;
    private final OrganizationDao organizationDao;
    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;
    private final SessionDao sessionDao;

    public ResolveWebEventHandler(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
        this.sessionDao = holder.sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        //deviceId logEventId comment
        String[] split = split2(message.body);

        int deviceId = Integer.parseInt(split[0]);

        //logEventId comment
        String[] split2 = split2(split[1]);
        long logEventId = Long.parseLong(split2[0]);
        String comment = split2.length == 2 ? split2[1] : "";

        Device device = deviceDao.getById(deviceId);

        int orgId = organizationDao.getOrganizationIdByProductId(device.productId);
        User user = state.user;
        if (!user.hasAccess(orgId)) {
            log.error("User {} tries to access device {} he has no access.", user.email, deviceId);
            throw new NotAllowedException("You have no access to this device.", message.id);
        }

        blockingIOProcessor.executeDB(() -> {
            ResponseMessage response;
            try {
                if (dbManager.eventDBDao.resolveEvent(logEventId, user.name, comment)) {
                    response = ok(message.id);
                    Session session = sessionDao.userSession.get(state.userKey);
                    session.sendToSelectedDeviceOnWeb(ctx.channel(), RESOLVE_EVENT, message.id, split[1], deviceId);
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
