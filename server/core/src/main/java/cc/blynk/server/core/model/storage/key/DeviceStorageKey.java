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

    public final char pinTypeChar;

    public DeviceStorageKey(short pin, char pintTypeChar) {
        this.pin = pin;
        this.pinTypeChar = pintTypeChar;
    }

    public DeviceStorageKey(short pin, PinType pinType) {
        this(pin, pinType.pintTypeChar);
    }

    public boolean isSame(OnePinWidget onePinWidget) {
        return this.pin == onePinWidget.pin
                && this.pinTypeChar == onePinWidget.pinType.pintTypeChar;
    }

    public boolean isSame(MultiPinWidget multiPinWidget) {
        if (multiPinWidget.dataStreams == null) {
            return false;
        }
        for (DataStream dataStream : multiPinWidget.dataStreams) {
            if (dataStream.isSame(this.pin, PinType.getPinType(this.pinTypeChar))) {
                return true;
            }
        }
        return false;
    }

    public String makeHardwareBody(String value) {
        return DataStream.makeHardwareBody(pinTypeChar, pin, value);
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
        return pinTypeChar == that.pinTypeChar;
    }

    @Override
    public int hashCode() {
        int result = (int) pin;
        result = 31 * result + (int) pinTypeChar;
        return result;
    }

    @Override
    @JsonValue
    public String toString() {
        return "" + pinTypeChar + pin;
    }
}
