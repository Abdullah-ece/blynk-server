package cc.blynk.server.core.reporting.ota;

import cc.blynk.server.core.model.device.ota.ShipmentDeviceStatus;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simply stores every record in memory that should be stored in reporting DB lately.
 * Could cause OOM at high request rate.
 *
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 13.02.19.
 */
public final class ShipmentStatusProcessor {

    public final Queue<DeviceShipmentEvent> shipmentStatusStorage = new ConcurrentLinkedQueue<>();

    public void collect(int shipmentId, int deviceId, long ts, ShipmentDeviceStatus shipmentDeviceStatus) {
        if (shipmentDeviceStatus != null) {
            collect(new DeviceShipmentEvent(shipmentId, deviceId, ts, shipmentDeviceStatus));
        }
    }

    public void collect(DeviceShipmentEvent deviceShipmentEvent) {
        shipmentStatusStorage.add(deviceShipmentEvent);
    }
}
