package cc.blynk.server.core.dao;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.06.17.
 */
public class DeviceKey {

    public final int orgId;

    public final int deviceId;

    public DeviceKey(int orgId, int deviceId) {
        this.orgId = orgId;
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceKey)) return false;

        DeviceKey deviceKey = (DeviceKey) o;

        if (orgId != deviceKey.orgId) return false;
        return deviceId == deviceKey.deviceId;

    }

    @Override
    public int hashCode() {
        int result = orgId;
        result = 31 * result + deviceId;
        return result;
    }
}
