package cc.blynk.server.core.session.mobile;

import cc.blynk.server.core.model.auth.User;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class MobileStateHolder {

    public final int orgId;
    public final User user;
    public final Version version;

    public MobileStateHolder(int orgId, User user, Version version) {
        this.orgId = orgId;
        this.user = user;
        this.version = version;
    }

    public boolean isSameUser(String email) {
        return user.email.equals(email);
    }

    public boolean contains(String sharedToken) {
        return true;
    }

}
