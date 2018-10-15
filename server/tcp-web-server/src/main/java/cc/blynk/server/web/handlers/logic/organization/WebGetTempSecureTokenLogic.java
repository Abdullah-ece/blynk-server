package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.internal.token.UploadTempToken;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebGetTempSecureTokenLogic {

    private static final Logger log = LogManager.getLogger(WebGetTempSecureTokenLogic.class);

    private WebGetTempSecureTokenLogic() {
    }

    public static void messageReceived(Holder holder, ChannelHandlerContext ctx, User user, StringMessage msg) {
        String token = TokenGeneratorUtil.generateNewToken();
        holder.tokensPool.addToken(token, new UploadTempToken(user.email));

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(msg.command, msg.id, token), ctx.voidPromise());
        }
    }


}
