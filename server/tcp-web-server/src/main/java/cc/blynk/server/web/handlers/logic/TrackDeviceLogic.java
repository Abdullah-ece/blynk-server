package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class TrackDeviceLogic {

    private static final Logger log = LogManager.getLogger(TrackDeviceLogic.class);

    private TrackDeviceLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);
        state.selectedDeviceId = deviceId;
        log.debug("Selecting webapp device {} for {}.", deviceId, state.user.email);
        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
