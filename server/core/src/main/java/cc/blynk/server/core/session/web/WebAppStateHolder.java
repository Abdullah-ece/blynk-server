package cc.blynk.server.core.session.web;

import cc.blynk.server.core.model.auth.User;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class WebAppStateHolder {

    private static final int NO_DEVICE = -1;

    public final int orgId;
    public final User user;
    public int selectedDeviceId;

    public WebAppStateHolder(int orgId, User user) {
        this.orgId = orgId;
        this.user = user;
        this.selectedDeviceId = NO_DEVICE;
    }

    public boolean isSameDevice(int deviceId) {
        return selectedDeviceId == deviceId;
    }

}
