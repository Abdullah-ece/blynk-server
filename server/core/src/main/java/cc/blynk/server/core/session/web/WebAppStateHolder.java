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
    public int selectedOrgId;

    public WebAppStateHolder(User user, Role role, Version version) {
        super(user.orgId, user, version, role);
        this.selectedDeviceId = NO_DEVICE;
        this.selectedOrgId = orgId;
    }

    public boolean isSelected(int deviceId) {
        return selectedDeviceId == deviceId;
    }

    /**
     * If we get hardware control action like hardware/getTimeline/resolveEvent
     * we have to check that currently "selected device" is
     * matches to the deviceId in hardware command, so we can be sure
     * user doesn't try to access another device.
     *
     * This is required because we do security check only on trackDevice handler,
     * so in order to control device user should have it selected on the UI.
     */
    public void checkControlledDeviceIsSelected(int deviceId) {
        if (deviceId != selectedDeviceId) {
            throw new NoPermissionException("You can control only selected on UI device.");
        }
    }

}
