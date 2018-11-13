package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;

import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public final class TemporaryTokenValue extends TokenValue {

    private static final long EXPIRATION_PERIOD = TimeUnit.DAYS.toMillis(7);
    private final long created;

    public TemporaryTokenValue(int orgId, User user, Device device) {
        super(orgId, user, device);
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
