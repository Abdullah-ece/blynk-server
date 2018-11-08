package cc.blynk.server.core.session;

import cc.blynk.server.core.model.auth.User;

/**
 * Base class for user session state.
 * Every connection has it's own info like user, tokem .deviceId, etc.
 * All info that requires quick access without any lookups.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.01.16.
 */
public abstract class StateHolderBase {

    public final User user;

    public StateHolderBase(User user) {
        this.user = user;
    }

    public abstract boolean contains(String sharedToken);

    public abstract boolean isSameDevice(int deviceId);

}
