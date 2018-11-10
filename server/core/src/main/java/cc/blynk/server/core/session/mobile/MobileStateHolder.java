package cc.blynk.server.core.session.mobile;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.session.StateHolderBase;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class MobileStateHolder implements StateHolderBase {

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

    @Override
    public boolean contains(String sharedToken) {
        return true;
    }

    @Override
    public boolean isSameDevice(int deviceId) {
        return true;
    }
}
