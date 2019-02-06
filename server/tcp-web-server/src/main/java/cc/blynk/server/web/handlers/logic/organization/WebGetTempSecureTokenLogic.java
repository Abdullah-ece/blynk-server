package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.server.internal.token.UploadTempToken;
import cc.blynk.server.web.handlers.logic.organization.dto.TokenDTO;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.internal.CommonByteBufUtil.makeASCIIStringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebGetTempSecureTokenLogic {

    private final TokensPool tokensPool;

    public WebGetTempSecureTokenLogic(Holder holder) {
        this.tokensPool = holder.tokensPool;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage msg) {
        String tokenString = TokenGeneratorUtil.generateNewToken();
        tokensPool.addToken(tokenString, new UploadTempToken(user.email));

        if (ctx.channel().isWritable()) {
            TokenDTO token = new TokenDTO(tokenString);
            ctx.writeAndFlush(
                    makeASCIIStringMessage(msg.command, msg.id, token.toString()),
                    ctx.voidPromise());
        }
    }

}
