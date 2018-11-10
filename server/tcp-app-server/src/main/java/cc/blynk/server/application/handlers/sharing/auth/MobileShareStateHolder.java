package cc.blynk.server.application.handlers.sharing.auth;

import cc.blynk.server.core.dao.SharedTokenManager;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.core.session.mobile.Version;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public final class MobileShareStateHolder extends MobileStateHolder {

    public final String token;
    public final int dashId;

    MobileShareStateHolder(int orgId, User user, Version version, String token, int dashId) {
        super(orgId, user, version);
        this.token = token;
        this.dashId = dashId;
    }

    @Override
    public boolean contains(String sharedToken) {
        return token.equals(sharedToken) || SharedTokenManager.ALL.equals(sharedToken);
    }

}
