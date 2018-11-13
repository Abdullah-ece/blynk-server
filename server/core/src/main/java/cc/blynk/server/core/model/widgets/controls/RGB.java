package cc.blynk.server.core.model.widgets.controls;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import io.netty.channel.Channel;

import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.prependDeviceId;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 21.03.15.
 */
public class RGB extends MultiPinWidget {

    public boolean splitMode;

    public boolean sendOnReleaseOn;

    public int frequency;

    @Override
    public void sendAppSync(Channel appChannel, int targetId) {
        if (dataStreams == null) {
            return;
        }
        if (targetId == ANY_TARGET || this.deviceId == targetId) {
            if (isSplitMode()) {
                for (DataStream dataStream : dataStreams) {
                    if (dataStream.notEmptyAndIsValid()) {
                        String body = prependDeviceId(deviceId, dataStream.makeHardwareBody());
                        appChannel.write(makeUTF8StringMessage(DEVICE_SYNC, SYNC_DEFAULT_MESSAGE_ID, body),
                                appChannel.voidPromise());
                    }
                }
            } else {
                if (dataStreams[0].notEmptyAndIsValid()) {
                    String body = prependDeviceId(deviceId, dataStreams[0].makeHardwareBody());
                    appChannel.write(makeUTF8StringMessage(DEVICE_SYNC, SYNC_DEFAULT_MESSAGE_ID, body),
                            appChannel.voidPromise());
                }
            }
        }
    }

    public boolean isSplitMode() {
        return splitMode;
    }

    @Override
    public PinMode getModeType() {
        return PinMode.out;
    }

    @Override
    public int getPrice() {
        return 400;
    }

}
