package cc.blynk.server.core.dao;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.06.17.
 */
public class DeviceKey {

    public final int orgId;

    public final int productId;

    public final int deviceId;

    DeviceKey(int orgId, int productId, int deviceId) {
        this.orgId = orgId;
        this.productId = productId;
        this.deviceId = deviceId;
    }

    //orgId and productId is not used by purpose!!!
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceKey)) {
            return false;
        }

        DeviceKey deviceKey = (DeviceKey) o;

        return deviceId == deviceKey.deviceId;

    }

    @Override
    public int hashCode() {
        return deviceId;
    }
}
