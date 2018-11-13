package cc.blynk.server.application.handlers.main.logic.dashboard.device;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
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

    private MobileDeleteDeviceLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);

        User user = state.user;

        Device device = holder.deviceDao.getByIdOrThrow(deviceId);
        log.debug("Deleting device with id {}.", deviceId);

        for (DashBoard dash : user.profile.dashBoards) {
            dash.eraseWidgetValuesForDevice(deviceId);
        }
        user.profile.deleteDeviceFromTags(deviceId);
        holder.deviceDao.delete(device.id);
        Session session = holder.sessionDao.getOrgSession(state.orgId);
        session.closeHardwareChannelByDeviceId(deviceId);

        user.lastModifiedTs = System.currentTimeMillis();

        holder.blockingIOProcessor.executeHistory(() -> {
            try {
                holder.reportingDiskDao.delete(deviceId);
            } catch (Exception e) {
                log.warn("Error removing device data. Reason : {}.", e.getMessage());
            }
        });

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
