package cc.blynk.server.core.model.storage.key;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import com.fasterxml.jackson.annotation.JsonValue;

import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.11.18.
 */
public class DeviceStorageKey {

    public final short pin;

    public final PinType pinType;

    public DeviceStorageKey(short pin, PinType pintType) {
        this.pin = pin;
        this.pinType = pintType;
    }

    public boolean isSame(OnePinWidget onePinWidget) {
        return this.pin == onePinWidget.pin
                && this.pinType == onePinWidget.pinType;
    }

    public boolean isSame(MultiPinWidget multiPinWidget) {
        if (multiPinWidget.dataStreams == null) {
            return false;
        }
        for (DataStream dataStream : multiPinWidget.dataStreams) {
            if (dataStream.isSame(this.pin, pinType)) {
                return true;
            }
        }
        return false;
    }

    public String makeHardwareBody(String value) {
        return DataStream.makeHardwareBody(pinType, pin, value);
    }

    public short getCmdType() {
        return DEVICE_SYNC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeviceStorageKey that = (DeviceStorageKey) o;

        if (pin != that.pin) {
            return false;
        }
        return pinType == that.pinType;
    }

    @Override
    public int hashCode() {
        int result = (int) pin;
        result = 31 * result + (pinType != null ? pinType.hashCode() : 0);
        return result;
    }

    @Override
    @JsonValue
    public String toString() {
        return "" + pinType.pintTypeChar + pin;
    }
}
