package cc.blynk.server.core.session.web;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.session.mobile.BaseUserStateHolder;
import cc.blynk.server.core.session.mobile.Version;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class WebAppStateHolder extends BaseUserStateHolder {

    private static final int NO_DEVICE = -1;

    public int selectedDeviceId;
    public int selectedOrgId;

    public WebAppStateHolder(User user, Role role, Version version) {
        super(user.orgId, user, role, version);
        this.selectedDeviceId = NO_DEVICE;
        this.selectedOrgId = orgId;
    }

    public boolean isSelected(int deviceId) {
        return selectedDeviceId == deviceId;
    }

}
