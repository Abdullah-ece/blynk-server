package cc.blynk.server.core.model.device.ota;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.08.17.
 */
public final class DeviceShipmentInfo {

    public final int shipmentId;

    public final ShipmentDeviceStatus status;

    public final int attempts;

    @JsonCreator
    public DeviceShipmentInfo(@JsonProperty("shipmentId") int shipmentId,
                              @JsonProperty("status") ShipmentDeviceStatus status,
                              @JsonProperty("attempts") int attempts) {
        this.shipmentId = shipmentId;
        this.status = status;
        this.attempts = attempts;
    }

    public DeviceShipmentInfo(DeviceShipmentInfo prev, ShipmentDeviceStatus status) {
        this(prev.shipmentId, status, prev.attempts);

    }

    public DeviceShipmentInfo(DeviceShipmentInfo prev, ShipmentDeviceStatus status, int attempts) {
        this(prev.shipmentId, status, attempts);

    }

    public boolean isLimitReached(int attemptsLimit) {
        return this.attempts >= attemptsLimit;
    }

}
