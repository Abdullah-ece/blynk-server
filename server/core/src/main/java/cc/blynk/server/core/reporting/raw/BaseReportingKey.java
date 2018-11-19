package cc.blynk.server.core.reporting.raw;

import cc.blynk.server.core.model.enums.PinType;

import java.io.Serializable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.07.17.
 */
public final class BaseReportingKey implements Serializable {

    public final int deviceId;
    public final PinType pinType;
    public final short pin;

    public BaseReportingKey(int deviceId, PinType pinType, short pin) {
        this.deviceId = deviceId;
        this.pinType = pinType;
        this.pin = pin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseReportingKey)) {
            return false;
        }

        BaseReportingKey that = (BaseReportingKey) o;

        if (deviceId != that.deviceId) {
            return false;
        }
        if (pin != that.pin) {
            return false;
        }
        return pinType == that.pinType;
    }

    @Override
    public int hashCode() {
        int result = deviceId;
        result = 31 * result + (pinType != null ? pinType.hashCode() : 0);
        result = 31 * result + (int) pin;
        return result;
    }
}
