package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;

import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public final class ProvisionTokenValue extends DeviceValue {

    private static final long EXPIRATION_PERIOD = TimeUnit.DAYS.toMillis(7);
    private final long created;
    public final User user;

    public ProvisionTokenValue(int orgId, User user, Device device) {
        //for provisioned device product defined during first connect of the device
        super(orgId, null, device);
        this.user = user;
        this.created = System.currentTimeMillis();
    }

    @Override
    public boolean isExpired(long now) {
        return created + EXPIRATION_PERIOD < now;
    }

    @Override
    public boolean isTemporary() {
        return true;
    }
}
