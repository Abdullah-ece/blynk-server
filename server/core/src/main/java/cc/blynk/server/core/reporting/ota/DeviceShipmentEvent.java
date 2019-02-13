package cc.blynk.server.core.reporting.ota;

import cc.blynk.server.core.model.device.ota.OTADeviceStatus;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 13.02.19.
 */
public class DeviceShipmentEvent {

    public final int shipmentId;
    public final int deviceId;
    public final long ts;
    public final OTADeviceStatus otaDeviceStatus;

    DeviceShipmentEvent(int shipmentId, int deviceId, long ts, OTADeviceStatus otaDeviceStatus) {
        this.shipmentId = shipmentId;
        this.deviceId = deviceId;
        this.ts = ts;
        this.otaDeviceStatus = otaDeviceStatus;
    }
}
