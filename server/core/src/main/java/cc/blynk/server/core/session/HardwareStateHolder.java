package cc.blynk.server.core.session;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.utils.ArrayUtil;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public final class HardwareStateHolder  {

    public final int orgId;
    public final Device device;

    public HardwareStateHolder(int orgId, Device device) {
        this.orgId = orgId;
        this.device = device;
    }

    @Override
    public String toString() {
        return "HardwareStateHolder{"
                + ", deviceId=" + device.id
                + ", token=" + device.token
                + '}';
    }

    public boolean isSameDevice(int deviceId) {
        return device.id == deviceId;
    }

    public boolean contains(int[] devicesIds) {
        return ArrayUtil.contains(devicesIds, device.id);
    }

}
