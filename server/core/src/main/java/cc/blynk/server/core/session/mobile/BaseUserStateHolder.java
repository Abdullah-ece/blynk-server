package cc.blynk.server.core.session.mobile;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.18.
 */
public abstract class BaseUserStateHolder {

    public final int orgId;
    public final User user;
    public final Version version;
    public Role role;

    public BaseUserStateHolder(int orgId, User user, Version version, Role role) {
        this.orgId = orgId;
        this.user = user;
        this.version = version;
        this.role = role;
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

}
