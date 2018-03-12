package cc.blynk.server.application.handlers.main.auth;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.session.StateHolderBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public class WebAppStateHolder extends StateHolderBase {

    private static final Logger log = LogManager.getLogger(WebAppStateHolder.class);

    private static final int NO_DEVICE = -1;

    public final Version version;
    public int selectedDeviceId;

    public WebAppStateHolder(User user, Version version) {
        super(user);
        this.version = version;
        this.selectedDeviceId = NO_DEVICE;
    }

    @Override
    public boolean contains(String sharedToken) {
        return true;
    }

    @Override
    public boolean isSameDash(int inDashId) {
        return true;
    }

    @Override
    public boolean isSameDevice(int deviceId) {
        log.info("DDDDD selected {},  in {}", selectedDeviceId, deviceId);
        return selectedDeviceId == deviceId;
    }

    @Override
    public boolean isSameDashAndDeviceId(int inDashId, int deviceId) {
        return isSameDevice(deviceId);
    }
}
