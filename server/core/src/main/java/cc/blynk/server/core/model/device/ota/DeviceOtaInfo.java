package cc.blynk.server.core.model.device.ota;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.08.17.
 */
public final class DeviceOtaInfo {

    public final int shipmentId;

    public final OTADeviceStatus status;

    public final int attempts;

    @JsonCreator
    public DeviceOtaInfo(@JsonProperty("shipmentId") int shipmentId,
                         @JsonProperty("status") OTADeviceStatus status,
                         @JsonProperty("attempts") int attempts) {
        this.shipmentId = shipmentId;
        this.status = status;
        this.attempts = attempts;
    }

    public DeviceOtaInfo(DeviceOtaInfo prev, OTADeviceStatus status) {
        this(prev.shipmentId, status, prev.attempts);

    }

    public DeviceOtaInfo(DeviceOtaInfo prev, OTADeviceStatus status, int attempts) {
        this(prev.shipmentId, status, attempts);

    }

    public boolean isLimitReached(int attemptsLimit) {
        return this.attempts >= attemptsLimit;
    }

}
