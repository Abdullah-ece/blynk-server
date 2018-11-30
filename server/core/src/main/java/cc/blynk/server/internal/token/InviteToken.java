package cc.blynk.server.internal.token;

import java.io.Serializable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class InviteToken extends BaseToken implements Serializable {

    public final int orgId;
    public final String appName;
    public final String email;

    public InviteToken(String email, int orgId, String appName) {
        super(DEFAULT_EXPIRE_TIME);
        this.email = email;
        this.orgId = orgId;
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "InviteToken{"
                + "orgId=" + orgId
                + ", appName='" + appName + '\''
                + '}';
    }
}
