package cc.blynk.server.internal.token;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public abstract class BaseToken implements Serializable {

    static final long DEFAULT_EXPIRE_TIME = TimeUnit.DAYS.toMillis(7);

    private final long expireAt;

    BaseToken(long tokenExpirationPeriodMillis) {
        this.expireAt = System.currentTimeMillis() + tokenExpirationPeriodMillis;
    }

    boolean isExpired(long now) {
        return expireAt < now;
    }
}
