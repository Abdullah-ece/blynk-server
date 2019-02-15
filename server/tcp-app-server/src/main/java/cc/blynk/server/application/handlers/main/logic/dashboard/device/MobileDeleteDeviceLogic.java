package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.db.ReportingDBManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.16.
 */
public final class MobileDeleteDeviceLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteDeviceLogic.class);

    private final DeviceDao deviceDao;
    private final SessionDao sessionDao;
    private final ReportingDBManager reportingDBManager;
    private final BlockingIOProcessor blockingIOProcessor;

    public MobileDeleteDeviceLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.sessionDao = holder.sessionDao;
        this.reportingDBManager = holder.reportingDBManager;
        this.blockingIOProcessor = holder.blockingIOProcessor;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                MobileStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);

        User user = state.user;

        Device device = deviceDao.getByIdOrThrow(deviceId);
        log.debug("Deleting device with id {}.", deviceId);

        deviceDao.delete(device.id);
        Session session = sessionDao.getOrgSession(state.user.orgId);
        session.closeHardwareChannelByDeviceId(deviceId);
        user.deleteDevice(deviceId);

        blockingIOProcessor.executeReporting(() -> {
            try {
                reportingDBManager.reportingDBDao.delete(deviceId);
            } catch (Exception e) {
                log.warn("Error removing device data. Reason : {}.", e.getMessage());
            }
        });

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
