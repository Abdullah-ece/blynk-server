package cc.blynk.server.web.handlers.logic.account;

import cc.blynk.server.core.model.auth.AccountDTO;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ACCOUNT;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;

/**
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/7/2018.
 *
 */
public final class WebEditAccountLogic {

    private WebEditAccountLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        AccountDTO updatedAccount = JsonParser.readAny(message.body, AccountDTO.class);
        if (updatedAccount == null) {
            throw new JsonException("Error parsing account info.");
        }

        if (updatedAccount.isNotValid()) {
            throw new JsonException("Account info is not valid.");
        }

        User user = state.user;
        user.name = updatedAccount.name;
        user.lastModifiedTs = System.currentTimeMillis();
        if (ctx.channel().isWritable()) {
            String userString = JsonParser.toJsonWeb(user);
            ctx.writeAndFlush(makeUTF8StringMessage(WEB_EDIT_ACCOUNT, message.id, userString), ctx.voidPromise());
        }
    }

}
