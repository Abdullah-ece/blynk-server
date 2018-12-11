package cc.blynk.server.application.handlers.main.logic;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
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
public final class MobileLogoutLogic {

    private static final Logger log = LogManager.getLogger(MobileLogoutLogic.class);

    private MobileLogoutLogic() {
    }

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage msg) {
        log.debug("User {}-{} did logout.", user.email, user.orgId);
        ctx.writeAndFlush(ok(msg.id), ctx.voidPromise());

        String uid = msg.body;
        user.profile.settings.notificationSettings.clear(uid);
        ctx.close();
    }

}
