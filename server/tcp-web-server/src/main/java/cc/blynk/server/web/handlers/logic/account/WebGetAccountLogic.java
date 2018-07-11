package cc.blynk.server.web.handlers.logic.account;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.web.session.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ACCOUNT;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class WebGetAccountLogic {

    private WebGetAccountLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        if (ctx.channel().isWritable()) {
            String userString = JsonParser.toJsonWeb(state.user);
            ctx.writeAndFlush(makeUTF8StringMessage(WEB_GET_ACCOUNT, message.id, userString), ctx.voidPromise());
        }
    }

}
