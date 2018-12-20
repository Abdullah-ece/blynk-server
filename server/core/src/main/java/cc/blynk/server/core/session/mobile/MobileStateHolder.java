package cc.blynk.server.core.session.mobile;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.permissions.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class MobileStateHolder extends BaseUserStateHolder {

    public MobileStateHolder(User user, Role role, Version version) {
        super(user.orgId, user, version, role);
    }

}
