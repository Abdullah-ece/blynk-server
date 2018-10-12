package cc.blynk.server.internal.token;

import java.io.Serializable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class InviteToken extends BaseToken implements Serializable {

    //todo remove?
    public final String appName;

    public InviteToken(String email, String appName) {
        super(email);
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "InviteToken{"
                + "email='" + email + '\''
                + ", appName='" + appName + '\''
                + '}';
    }

    public boolean isSame(String email, String appName) {
        return this.email.equals(email) && this.appName.equals(appName);
    }
}
