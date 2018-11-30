package cc.blynk.server.internal.token;

import java.io.Serializable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class ResetPassToken extends BaseToken implements Serializable {

    public final String appName;
    public final String email;

    public ResetPassToken(String email, String appName) {
        super(DEFAULT_EXPIRE_TIME);
        this.email = email;
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "ResetPassToken{"
                + "email='" + email + '\''
                + ", appName='" + appName + '\''
                + '}';
    }

    public boolean isSame(String email, String appName) {
        return this.email.equals(email) && this.appName.equals(appName);
    }
}
