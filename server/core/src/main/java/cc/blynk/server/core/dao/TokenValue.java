package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.device.Device;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public class TokenValue {

    public final int orgId;

    public final Device device;

    public TokenValue(int orgId, Device device) {
        this.orgId = orgId;
        this.device = device;
    }

    public boolean isTemporary() {
        return false;
    }

    public boolean isExpired(long now) {
        return false;
    }

    public boolean belongsToOrg(int orgId) {
        return this.orgId == orgId;
    }

}
