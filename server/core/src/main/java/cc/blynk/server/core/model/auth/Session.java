package cc.blynk.server.core.model.auth;

import cc.blynk.server.common.BaseSimpleChannelInboundHandler;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.HardwareStateHolder;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cc.blynk.server.internal.CommonByteBufUtil.deviceOffline;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.StateHolderUtil.getHardState;
import static cc.blynk.server.internal.StateHolderUtil.getWebState;
import static cc.blynk.server.internal.StateHolderUtil.isSameDeviceId;
import static cc.blynk.utils.StringUtils.prependDeviceId;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 * <p>
 * DefaultChannelGroup.java too complicated. so doing in simple way for now.
 */
public class Session {

    private static final Logger log = LogManager.getLogger(Session.class);

    public final EventLoop initialEventLoop;
    public final Set<Channel> appChannels = ConcurrentHashMap.newKeySet();
    public final Set<Channel> hardwareChannels = ConcurrentHashMap.newKeySet();
    public final Set<Channel> webChannels = ConcurrentHashMap.newKeySet();

    private final ChannelFutureListener webRemover = future -> webChannels.remove(future.channel());
    private final ChannelFutureListener appRemover = future -> appChannels.remove(future.channel());
    private final ChannelFutureListener hardRemover = future -> hardwareChannels.remove(future.channel());

    public Session(EventLoop initialEventLoop) {
        this.initialEventLoop = initialEventLoop;
    }

    public boolean isSameEventLoop(ChannelHandlerContext ctx) {
        return isSameEventLoop(ctx.channel());
    }

    public boolean isSameEventLoop(Channel channel) {
        return initialEventLoop == channel.eventLoop();
    }

    public static boolean needSync(Channel channel, String sharedToken) {
        BaseSimpleChannelInboundHandler appHandler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        return appHandler != null && ((MobileStateHolder) appHandler.getState()).contains(sharedToken);
    }

    public void addAppChannel(Channel appChannel) {
        if (appChannels.add(appChannel)) {
            appChannel.closeFuture().addListener(appRemover);
        }
    }

    public void addHardChannel(Channel hardChannel) {
        if (hardwareChannels.add(hardChannel)) {
            hardChannel.closeFuture().addListener(hardRemover);
        }
    }

    public void addWebChannel(Channel webChannel) {
        if (webChannels.add(webChannel)) {
            webChannel.closeFuture().addListener(webRemover);
        }
    }

    private Set<Channel> filter(int bodySize, int[] deviceIds) {
        Set<Channel> targetChannels = new HashSet<>();
        for (Channel channel : hardwareChannels) {
            HardwareStateHolder hardwareState = getHardState(channel);
            if (hardwareState != null
                    && (deviceIds.length == 0 || ArrayUtil.contains(deviceIds, hardwareState.device.id))) {
                if (hardwareState.device.fitsBufferSize(bodySize)) {
                    targetChannels.add(channel);
                } else {
                    log.trace("Message is to large. Size {}.", bodySize);
                }
            }
        }
        return targetChannels;
    }

    private Set<Channel> filter(int bodySize, int deviceId) {
        Set<Channel> targetChannels = new HashSet<>();
        for (Channel channel : hardwareChannels) {
            HardwareStateHolder hardwareState = getHardState(channel);
            if (hardwareState != null && hardwareState.isSameDevice(deviceId)) {
                if (hardwareState.device.fitsBufferSize(bodySize)) {
                    targetChannels.add(channel);
                } else {
                    log.trace("Message is to large. Size {}.", bodySize);
                }
            }
        }
        return targetChannels;
    }

    public boolean sendMessageToHardware(short cmd, int msgId, String body, int deviceId) {
        return hardwareChannels.size() == 0
                || sendMessageToHardware(filter(body.length(), deviceId), cmd, msgId, body);
    }

    public boolean sendMessageToHardware(short cmd, int msgId, String body, int... deviceIds) {
        return hardwareChannels.size() == 0
                || sendMessageToHardware(filter(body.length(), deviceIds), cmd, msgId, body);
    }

    public boolean sendMessageToHardware(short cmd, int msgId, String body) {
        return sendMessageToHardware(hardwareChannels, cmd, msgId, body);
    }

    private boolean sendMessageToHardware(Set<Channel> targetChannels, short cmd, int msgId, String body) {
        int channelsNum = targetChannels.size();
        if (channelsNum == 0) {
            return true; // -> no active hardware
        }

        send(targetChannels, cmd, msgId, body);

        return false; // -> there is active hardware
    }

    public boolean isHardwareConnected() {
        return hardwareChannels.size() > 0;
    }

    public boolean isHardwareConnected(int devideId) {
        for (Channel channel : hardwareChannels) {
            if (isSameDeviceId(channel, devideId)) {
                return true;
            }
        }
        return false;
    }

    public void sendOfflineMessageToApps(int deviceId) {
        int targetsNum = appChannels.size();
        if (targetsNum > 0) {
            log.trace("Sending device offline message to app.");

            StringMessage deviceOfflineMessage = deviceOffline(deviceId);
            sendMessageToMultipleReceivers(appChannels, deviceOfflineMessage);
        }
    }

    public void sendOfflineMessageToWeb(int deviceId) {
        int targetsNum = webChannels.size();
        if (targetsNum > 0) {
            log.trace("Sending device offline message to webapp.");

            StringMessage deviceOfflineMessage = deviceOffline(deviceId);
            sendMessageToMultipleReceivers(webChannels, deviceOfflineMessage);
        }
    }

    public void sendToSelectedDeviceOnWeb(short cmd, int msgId, String body, int... deviceIds) {
        sendToSelectedDeviceOnWeb(null, cmd, msgId, body, deviceIds);
    }

    public void sendToSelectedDeviceOnWeb(Channel self, short cmd, int msgId, String body, int... deviceIds) {
        if (!isWebConnected()) {
            return;
        }
        for (Channel channel : webChannels) {
            if (channel != self) {
                WebAppStateHolder webAppStateHolder = getWebState(channel);
                if (webAppStateHolder != null) {
                    for (int deviceId : deviceIds) {
                        if (webAppStateHolder.isSelected(deviceId)) {
                            String finalBody = prependDeviceId(deviceId, body);
                            StringMessage msg = makeUTF8StringMessage(cmd, msgId, finalBody);
                            if (channel.isWritable()) {
                                channel.writeAndFlush(msg, channel.voidPromise());
                            }
                        }
                    }
                }
            }
        }
    }

    public void sendToApps(short cmd, int msgId, int deviceId, String body) {
        if (isAppConnected()) {
            String finalBody = prependDeviceId(deviceId, body);
            sendToApps(cmd, msgId, finalBody);
        }
    }

    public void sendToApps(short cmd, int msgId, String finalBody) {
        //todo permissions filter
        Set<Channel> targetChannels = appChannels;

        int targetsNum = targetChannels.size();
        if (targetsNum > 0) {
            send(targetChannels, cmd, msgId, finalBody);
        }
    }

    public void sendToWeb(short cmd, int msgId, String finalBody) {
        int targetsNum = webChannels.size();
        if (targetsNum > 0) {
            send(webChannels, cmd, msgId, finalBody);
        }
    }

    private static void sendMessageToMultipleReceivers(Set<Channel> targets, StringMessage msg) {
        for (Channel channel : targets) {
            if (channel.isWritable()) {
                channel.writeAndFlush(msg, channel.voidPromise());
            }
        }
    }

    private static void send(Set<Channel> targets, short cmd, int msgId, String body) {
        StringMessage msg = makeUTF8StringMessage(cmd, msgId, body);
        sendMessageToMultipleReceivers(targets, msg);
    }

    public void sendToSharedApps(Channel sendingChannel, String sharedToken, short cmd, int msgId, String body) {
        Set<Channel> targetChannels = new HashSet<>();
        for (Channel channel : appChannels) {
            if (channel != sendingChannel && needSync(channel, sharedToken)) {
                targetChannels.add(channel);
            }
        }

        int channelsNum = targetChannels.size();
        if (channelsNum > 0) {
            send(targetChannels, cmd, msgId, body);
        }
    }

    public boolean isAppConnected() {
        return appChannels.size() > 0;
    }

    public boolean isWebConnected() {
        return webChannels.size() > 0;
    }

    public void closeHardwareChannelByDeviceId(int deviceId) {
        for (Channel channel : hardwareChannels) {
            if (isSameDeviceId(channel, deviceId)) {
                channel.close();
            }
        }
    }

    public void closeAll() {
        hardwareChannels.forEach(io.netty.channel.Channel::close);
        appChannels.forEach(io.netty.channel.Channel::close);
        webChannels.forEach(io.netty.channel.Channel::close);
    }

}
