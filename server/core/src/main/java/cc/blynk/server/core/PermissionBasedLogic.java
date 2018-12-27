package cc.blynk.server.core;

import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface PermissionBasedLogic<T extends BaseUserStateHolder> {

    Logger log = LogManager.getLogger(PermissionBasedLogic.class);

    int getPermission();

    default void messageReceived(ChannelHandlerContext ctx, T state, StringMessage msg) {
        if (hasPermission(state.role)) {
            messageReceived0(ctx, state, msg);
        } else {
            noPermissionAction(ctx, state, msg);
        }
    }

    default boolean hasPermission(Role role) {
        return role.hasPermission1(getPermission());
    }

    /**
     * Used when API handler should behave differently when 2 permissions overlap.
     * For example, VIEW_ORG_DEVICES permission overlap VIEW_OWN_DEVICES,
     * so when getDevices command comes we have to check first for overlapping permission
     * and if user have overlapping permission VIEW_ORG_DEVICES - return all devices for this org
     * if user doesn't have VIEW_ORG_DEVICES - return devices based on VIEW_OWN_DEVICES
     */
    default void noPermissionAction(ChannelHandlerContext ctx, T state, StringMessage msg) {
        throw new NoPermissionException(state.user.email, getPermission());
    }

    void messageReceived0(ChannelHandlerContext ctx, T state, StringMessage message);
}
