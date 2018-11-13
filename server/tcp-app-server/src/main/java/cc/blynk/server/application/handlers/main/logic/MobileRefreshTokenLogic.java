package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileRefreshTokenLogic {

    private MobileRefreshTokenLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx,
                                       MobileStateHolder state, StringMessage message) {
        int deviceId = Integer.parseInt(message.body);
        User user = state.user;

        Device device = holder.deviceDao.getById(deviceId);

        if (device == null) {
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        String token = holder.tokenManager.refreshToken(user.orgId, user, device);

        Session session = holder.sessionDao.getOrgSession(state.orgId);
        session.closeHardwareChannelByDeviceId(deviceId);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(REFRESH_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
