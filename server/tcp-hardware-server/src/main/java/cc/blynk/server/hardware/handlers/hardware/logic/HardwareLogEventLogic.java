package cc.blynk.server.hardware.handlers.hardware.logic;

import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.utils.BlynkByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareLogEventLogic {

    public static void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        }
    }

}
