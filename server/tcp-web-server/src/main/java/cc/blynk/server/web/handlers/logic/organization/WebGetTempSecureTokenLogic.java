package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    }


}
