package cc.blynk.server.core.reporting.ota;

import cc.blynk.server.core.model.device.ota.ShipmentDeviceStatus;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 13.02.19.
 */
public final class DeviceShipmentEvent {

    public final int shipmentId;
    public final int deviceId;
    public final long ts;
    public final ShipmentDeviceStatus shipmentDeviceStatus;

    DeviceShipmentEvent(int shipmentId, int deviceId, long ts, ShipmentDeviceStatus shipmentDeviceStatus) {
        this.shipmentId = shipmentId;
        this.deviceId = deviceId;
        this.ts = ts;
        this.shipmentDeviceStatus = shipmentDeviceStatus;
    }
}
