package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.MobileSyncWidget;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2Device;

/**
 * Request state sync info for widgets.
 * Supports sync for all widgets and sync for specific target
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileSyncLogic {

    private MobileSyncLogic() {
    }

    public static void messageReceived(Holder holder,
                                       ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        String[] dashIdAndTargetIdString = split2Device(message.body);
        int dashId = Integer.parseInt(dashIdAndTargetIdString[0]);
        int deviceId = MobileSyncWidget.ANY_TARGET;

        User user = state.user;

        if (dashIdAndTargetIdString.length == 2) {
            deviceId = Integer.parseInt(dashIdAndTargetIdString[1]);
        }

        ctx.write(ok(message.id), ctx.voidPromise());
        Channel appChannel = ctx.channel();

        if (deviceId == MobileSyncWidget.ANY_TARGET) {
            List<Device> devices = holder.deviceDao.getDevicesOwnedByUser(user.email);
            for (Device device : devices) {
                device.sendPinStorageSyncs(appChannel);
            }
        } else {
            Device device = holder.deviceDao.getById(deviceId);
            if (device != null) {
                device.sendPinStorageSyncs(appChannel);
            }
        }

        ctx.flush();
    }

}
