package cc.blynk.server.core.model.device;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.storage.DeviceStorageKeyDeserializer;
import cc.blynk.server.core.model.storage.PinStorageValueDeserializer;
import cc.blynk.server.core.model.storage.key.DevicePropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.storage.value.SinglePinStorageValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public final class PinStorage {

    @JsonDeserialize(keyUsing = DeviceStorageKeyDeserializer.class,
            contentUsing = PinStorageValueDeserializer.class)
    public final Map<DeviceStorageKey, PinStorageValue> values = new HashMap<>();

    public volatile long lastReportedAt;

    void removePinValue(PinType pinType, short pin, boolean removeProperties) {
        this.values.remove(new DeviceStorageKey(pin, pinType));
        if (removeProperties) {
            for (WidgetProperty widgetProperty : WidgetProperty.values()) {
                values.remove(new DevicePropertyStorageKey(pinType, pin, widgetProperty));
            }
        }
    }

    //property users always virtual pins
    public void updateValue(short pin, WidgetProperty widgetProperty, String value) {
        updateValue(new DevicePropertyStorageKey(PinType.VIRTUAL, pin, widgetProperty), value);
    }

    public void updateValue(short pin, PinType pinType, String value) {
        updateValue(new DeviceStorageKey(pin, pinType), value);
    }

    public void updateValue(DeviceStorageKey key, String value) {
        updateValue(key, value, System.currentTimeMillis());
    }

    public void updateValue(DataStream dataStream, String value, long now) {
        updateValue(new DeviceStorageKey(dataStream.pin, dataStream.pinType), value, now);
    }

    public String updateValue(short pin, PinType pinType, String value, long now) {
        return updateValue(new DeviceStorageKey(pin, pinType), value, now);
    }

    public String updateValue(DeviceStorageKey key, String value, long now) {
        PinStorageValue pinStorageValue = values.get(key);
        if (pinStorageValue == null) {
            //todo for now supporting only single values
            pinStorageValue = new SinglePinStorageValue();
            values.put(key, pinStorageValue);
        }
        this.lastReportedAt = now;
        return pinStorageValue.update(value);
    }

    void sendPinStorageSyncs(Channel appChannel, int deviceId) {
        for (Map.Entry<DeviceStorageKey, PinStorageValue> entry : values.entrySet()) {
            DeviceStorageKey key = entry.getKey();
            if (appChannel.isWritable()) {
                PinStorageValue pinStorageValue = entry.getValue();
                pinStorageValue.sendAppSync(appChannel, deviceId, key);
            }
        }
    }

    public PinStorageValue get(DeviceStorageKey deviceStorageKey) {
        return values.get(deviceStorageKey);
    }

    public PinStorageValue get(short pin, PinType pinType) {
        return get(new DeviceStorageKey(pin, pinType));
    }

    public void setLastReportedAt(long now) {
        this.lastReportedAt = now;
    }

    public void erase() {
        this.values.clear();
        this.lastReportedAt = 0;
    }
}
