package cc.blynk.server.application.handlers.main.logic.web;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.OrganizationDao;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.exceptions.NotAllowedException;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.DBManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.notAllowed;
import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.server.internal.CommonByteBufUtil.serverError;
import static cc.blynk.utils.StringUtils.split3;

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

    public ResolveWebEventHandler(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.organizationDao = holder.organizationDao;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split3(message.body);

        int deviceId = Integer.parseInt(split[0]);
        long logEventId = Long.parseLong(split[1]);
        String comment = split.length == 3 ? split[2] : "";

        Device device = deviceDao.getById(deviceId);

        int orgId = organizationDao.getOrganizationIdByProductId(device.productId);
        if (!user.hasAccess(orgId)) {
            log.error("User {} tries to access device {} he has no access.", user.email, deviceId);
            throw new NotAllowedException("You have no access to this device.", message.id);
        }

        blockingIOProcessor.executeDB(() -> {
            ResponseMessage response;
            try {
                if (dbManager.eventDBDao.resolveEvent(logEventId, user.name, comment)) {
                    response = ok(message.id);
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
