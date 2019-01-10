package cc.blynk.server.core.processors.rules;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class DeviceDataStream {

    public final short pin;

    public final PinType pinType;

    @JsonCreator
    public DeviceDataStream(@JsonProperty("pin") short pin,
                            @JsonProperty("pinType") PinType pinType) {
        this.pin = pin;
        this.pinType = pinType;
    }

    public boolean isSame(int productId, short pin, PinType pinType) {
        return this.pin == pin && this.pinType == pinType;
    }

    @Override
    public String toString() {
        return "DeviceDataStream{"
                + "pin=" + pin
                + ", pinType=" + pinType
                + '}';
    }
}
