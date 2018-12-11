package cc.blynk.server.application.handlers.main.logic;

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

        String uid;
        String token;

        //new format
        if (splitBody.length == 2) {
            uid = splitBody[0];
            token = splitBody[1];
        } else {
            //todo not needed anymore
            //var dashId = Integer.parseInt(splitBody[0]);
            uid = splitBody[1];
            token = splitBody[2];
        }

        var notificationSettings = state.user.profile.settings.notificationSettings;

        switch (state.version.osType) {
            case ANDROID :
                notificationSettings.androidTokens.put(uid, token);
                break;
            case IOS :
                notificationSettings.iOSTokens.put(uid, token);
                break;
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
