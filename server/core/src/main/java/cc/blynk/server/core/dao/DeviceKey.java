package cc.blynk.server.core.dao;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.06.17.
 */
public class DeviceKey {

    public final String email;

    public final int dashId;

    public final int deviceId;

    public DeviceKey(String email, int dashId, int deviceId) {
        this.email = email;
        this.dashId = dashId;
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceKey)) return false;

        DeviceKey deviceKey = (DeviceKey) o;

        if (dashId != deviceKey.dashId) return false;
        if (deviceId != deviceKey.deviceId) return false;
        return !(email != null ? !email.equals(deviceKey.email) : deviceKey.email != null);

    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + dashId;
        result = 31 * result + deviceId;
        return result;
    }
}
