package cc.blynk.server.web.handlers;

import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.ChannelHandlerContext;

public interface PermissionBasedLogic {

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
