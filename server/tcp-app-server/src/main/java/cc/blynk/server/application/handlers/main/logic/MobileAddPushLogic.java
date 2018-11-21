package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileAddPushLogic {

    private static final Logger log = LogManager.getLogger(MobileAddPushLogic.class);

    private MobileAddPushLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state, StringMessage message) {
        var splitBody = StringUtils.split3(message.body);

        var dashId = Integer.parseInt(splitBody[0]);
        var uid = splitBody[1];
        var token = splitBody[2];

        var dash = state.user.profile.getDashByIdOrThrow(dashId);

        var notification = dash.getNotificationWidget();

        if (notification == null) {
            log.error("No notification widget.");
            throw new JsonException("No notification widget.");
        }

        switch (state.version.osType) {
            case ANDROID :
                notification.androidTokens.put(uid, token);
                break;
            case IOS :
                notification.iOSTokens.put(uid, token);
                break;
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
