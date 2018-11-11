package cc.blynk.server.core.session;

/**
 * Base class for user session state.
 * Every connection has it's own info like user, tokem .deviceId, etc.
 * All info that requires quick access without any lookups.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.01.16.
 */
public interface StateHolderBase {

    boolean contains(String sharedToken);

    boolean isSameDevice(int deviceId);

}
