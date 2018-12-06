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
    public final Role role;
    public final Version version;

    BaseUserStateHolder(int orgId, User user, Role role, Version version) {
        this.orgId = orgId;
        this.user = user;
        this.role = role;
        this.version = version;
    }

    public boolean isSameUser(String email) {
        return user.email.equals(email);
    }

    public boolean contains(String sharedToken) {
        return true;
    }

}
