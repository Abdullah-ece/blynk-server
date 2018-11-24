package cc.blynk.server.core.model.storage.value;

import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.structure.BaseLimitedQueue;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Iterator;

import static cc.blynk.server.core.model.widgets.MobileSyncWidget.SYNC_DEFAULT_MESSAGE_ID;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static cc.blynk.utils.StringUtils.prependDeviceId;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27/04/2018.
 *
 */
public class MultiPinStorageValue extends PinStorageValue {

    public final MultiPinStorageValueType type;

    public final BaseLimitedQueue<String> values;

    public MultiPinStorageValue(MultiPinStorageValueType multiPinStorageValueType) {
        this.type = multiPinStorageValueType;
        this.values = multiPinStorageValueType.getQueue();
    }

    //todo quick temp solution
    private static String getLastElement(Collection<String> values) {
        Iterator<String> itr = values.iterator();
        String lastElement = itr.next();
        while (itr.hasNext()) {
            lastElement = itr.next();
        }
        return lastElement;
    }

    @Override
    public Collection<String> values() {
        return values;
    }

    @Override
    public void sendAppSync(Channel appChannel, int deviceId, DeviceStorageKey key) {
        if (values.size() > 0) {
            Iterator<String> valIterator = values.iterator();
            if (valIterator.hasNext()) {
                String last = null;
                StringBuilder sb = new StringBuilder();
                sb.append(deviceId).append(BODY_SEPARATOR)
                        .append(key.pinType.pintTypeChar).append('m').append(BODY_SEPARATOR).append(key.pin);
                while (valIterator.hasNext()) {
                    last = valIterator.next();
                    sb.append(BODY_SEPARATOR).append(last);
                }

                appChannel.write(makeUTF8StringMessage(DEVICE_SYNC, SYNC_DEFAULT_MESSAGE_ID, sb.toString()));

                //special case, when few widgets are on the same pin
                String body = prependDeviceId(deviceId, key.makeHardwareBody(last));
                StringMessage message = makeUTF8StringMessage(DEVICE_SYNC, SYNC_DEFAULT_MESSAGE_ID, body);
                appChannel.write(message, appChannel.voidPromise());
            }
        }
    }

    @Override
    public String lastValue() {
        return getLastElement(values);
    }

    @Override
    public void update(String value) {
        values.add(value);
    }
}
