package cc.blynk.server.core.session.web;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.exceptions.NoPermissionException;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import cc.blynk.server.core.session.mobile.Version;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class WebAppStateHolder extends BaseUserStateHolder {

    public static final int NO_DEVICE = -1;

    public int selectedDeviceId;

    public WebAppStateHolder(User user, Role role, Version version) {
        super(user, version, role);
        this.selectedDeviceId = NO_DEVICE;
    }

    public boolean isSelected(int deviceId) {
        return selectedDeviceId == deviceId;
    }

    @Override
    public void checkControlledDeviceIsSelected(int deviceId) {
        if (deviceId != selectedDeviceId) {
            throw new NoPermissionException("You can control only selected on UI device.");
        }
    }

}
