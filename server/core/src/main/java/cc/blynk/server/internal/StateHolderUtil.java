package cc.blynk.server.internal;

import cc.blynk.server.common.BaseSimpleChannelInboundHandler;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import io.netty.channel.Channel;

/**
 * Used instead of Netty's DefaultAttributeMap as it faster and
 * doesn't involves any synchronization at all.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public final class StateHolderUtil {

    private StateHolderUtil() {
    }

    public static HardwareStateHolder getHardState(Channel channel) {
        BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        return handler == null ? null : (HardwareStateHolder) handler.getState();
    }

    public static WebAppStateHolder getWebState(Channel channel) {
        BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        return handler == null ? null : (WebAppStateHolder) handler.getState();
    }

    public static boolean isSameDeviceId(Channel channel, int deviceId) {
        BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        return handler != null && ((HardwareStateHolder) handler.getState()).isSameDevice(deviceId);
    }

    public static boolean isSameEmail(Channel channel, String email) {
        BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        if (handler != null) {
            MobileStateHolder mobileStateHolder = ((MobileStateHolder) handler.getState());
            return mobileStateHolder.isSameUser(email);
        }
        return false;
    }

}
