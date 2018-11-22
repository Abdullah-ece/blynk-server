package cc.blynk.server.web.handlers;

import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface PermissionBasedLogic {

    Logger log = LogManager.getLogger(PermissionBasedLogic.class);

    boolean hasPermission(Role role);

    int getPermission();

    default void messageReceived(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message) {
        if (!hasPermission(state.role)) {
            throw new NoPermissionException(state.user.email, getPermission());
        }
        messageReceived0(ctx, state, message);
    }

    void messageReceived0(ChannelHandlerContext ctx, WebAppStateHolder state, StringMessage message);
}
