package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * Request state sync info for widgets.
 * Supports sync for all widgets and sync for specific target
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class DashSyncLogic {

    private DashSyncLogic() {
    }

    public static void messageReceived(Holder holder,
                                       ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        int dashId = Integer.parseInt(message.body);

        ctx.write(ok(message.id), ctx.voidPromise());
        Channel appChannel = ctx.channel();

        List<Device> devices = holder.deviceDao.getDevicesOwnedByUser(state.user.email);
        for (Device device : devices) {
            device.sendPinStorageSyncs(appChannel);
        }

        ctx.flush();
    }

}
