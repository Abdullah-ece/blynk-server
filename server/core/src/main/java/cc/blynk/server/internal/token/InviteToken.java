package cc.blynk.server.internal.token;

import java.io.Serializable;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class InviteToken extends BaseToken implements Serializable {

    public final int orgId;

    public InviteToken(String email, int orgId) {
        super(email, DEFAULT_EXPIRE_TIME);
        this.orgId = orgId;
    }

    @Override
    public String toString() {
        return "InviteToken{"
                + "email='" + email + '\''
                + ", orgId='" + orgId + '\''
                + '}';
    }

}
