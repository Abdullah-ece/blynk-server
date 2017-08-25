package cc.blynk.server.core.session;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public final class HardwareStateHolder extends StateHolderBase {

    public final DashBoard dash;
    public final Device device;

    public HardwareStateHolder(User user, DashBoard dash, Device device) {
        super(user);
        this.dash = dash;
        this.device = device;
    }

    @Override
    public boolean contains(String sharedToken) {
        return false;
    }

    @Override
    public String toString() {
        return "HardwareStateHolder{" +
                "dashId=" + dash.id +
                ", deviceId=" + device.id +
                ", token=" + device.token +
                '}';
    }
}
