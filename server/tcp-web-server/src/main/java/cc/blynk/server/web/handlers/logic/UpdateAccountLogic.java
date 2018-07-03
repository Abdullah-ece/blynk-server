package cc.blynk.server.web.handlers.logic;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_ACCOUNT;
import static cc.blynk.server.internal.CommonByteBufUtil.illegalCommandBody;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class UpdateAccountLogic {

    private static final Logger log = LogManager.getLogger(UpdateAccountLogic.class);

    private UpdateAccountLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        User updatedUser;
        try {
            updatedUser = JsonParser.parseUserFromString(message.body);
        } catch (IOException ioe) {
            log.debug("Error parsing account info. {}", ioe.getMessage());
            ctx.writeAndFlush(illegalCommandBody(message.id), ctx.voidPromise());
            return;
        }

        User user = state.user;
        user.setName(updatedUser.name);
        if (ctx.channel().isWritable()) {
            String userString = JsonParser.toJsonWeb(user);
            ctx.writeAndFlush(makeUTF8StringMessage(WEB_UPDATE_ACCOUNT, message.id, userString), ctx.voidPromise());
        }
    }

}
