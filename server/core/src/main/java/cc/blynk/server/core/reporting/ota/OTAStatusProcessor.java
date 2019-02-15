package cc.blynk.server.core.reporting.ota;

import cc.blynk.server.core.model.device.ota.OTADeviceStatus;

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
public class OTAStatusProcessor {

    public final Queue<DeviceShipmentEvent> otaStatusesStorage = new ConcurrentLinkedQueue<>();

    public void collect(int shipmentId, int deviceId, long ts, OTADeviceStatus otaDeviceStatus) {
        if (otaDeviceStatus != null) {
            collect(new DeviceShipmentEvent(shipmentId, deviceId, ts, otaDeviceStatus));
        }
    }

    public void collect(DeviceShipmentEvent deviceShipmentEvent) {
        otaStatusesStorage.add(deviceShipmentEvent);
    }
}
