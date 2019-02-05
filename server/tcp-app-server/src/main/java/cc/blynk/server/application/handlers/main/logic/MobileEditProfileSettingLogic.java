package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.profile.ProfileSettings;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11/12/2018.
 *
 */
public final class MobileEditProfileSettingLogic {

    private static final Logger log = LogManager.getLogger(MobileEditProfileSettingLogic.class);

    private MobileEditProfileSettingLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, MobileStateHolder state,
                                       StringMessage message) {

        String body = message.body;
        if (body.isEmpty()) {
            throw new JsonException("Income edit profile settings message is empty.");
        }

        log.debug("Trying to parse project settings : {}", body);
        ProfileSettings updatedSettings = JsonParser.parseProfileSettings(body, message.id);

        User user = state.user;

        user.profile.updateSettings(updatedSettings);
        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
