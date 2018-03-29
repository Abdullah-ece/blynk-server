package cc.blynk.server.core.session;

import cc.blynk.server.core.model.auth.User;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class WebAppStateHolder extends StateHolderBase {

    private static final int NO_DEVICE = -1;

    public int selectedDeviceId;

    public WebAppStateHolder(User user) {
        super(user);
        this.selectedDeviceId = NO_DEVICE;
    }

    @Override
    public boolean contains(String sharedToken) {
        return true;
    }

    @Override
    public boolean isSameDash(int inDashId) {
        return true;
    }

    @Override
    public boolean isSameDevice(int deviceId) {
        return selectedDeviceId == deviceId;
    }

    @Override
    public boolean isSameDashAndDeviceId(int inDashId, int deviceId) {
        return isSameDevice(deviceId);
    }
}
