package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

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
public final class DeviceSyncLogic {

    private final DeviceDao deviceDao;

    public DeviceSyncLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);

        ctx.write(ok(message.id), ctx.voidPromise());
        Channel appChannel = ctx.channel();

        //todo check access?
        Device device = deviceDao.getById(deviceId);
        if (device != null) {
            device.sendPinStorageSyncs(appChannel);
        }

        ctx.flush();
    }

}
