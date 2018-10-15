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
        super(email, DEFAULT_EXPIRE_TIME);
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "InviteToken{"
                + "email='" + email + '\''
                + ", appName='" + appName + '\''
                + '}';
    }

}
