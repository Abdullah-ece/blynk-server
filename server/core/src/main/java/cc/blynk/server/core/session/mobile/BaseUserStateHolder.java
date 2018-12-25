package cc.blynk.server.core.session.mobile;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.18.
 */
public abstract class BaseUserStateHolder {

    public final User user;
    public final Version version;
    public Role role;
    public int selectedOrgId;

    public BaseUserStateHolder(User user, Version version, Role role) {
        this.user = user;
        this.version = version;
        this.role = role;
        this.selectedOrgId = user.orgId; //for quick lookup
    }

    public boolean isSameUser(String email) {
        return user.email.equals(email);
    }

    public boolean contains(String sharedToken) {
        return true;
    }

    public void setRole(Role role) {
        this.role = role;
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
        //todo
        //all states should implement this in future
        //do nothing for now.
    }

}
