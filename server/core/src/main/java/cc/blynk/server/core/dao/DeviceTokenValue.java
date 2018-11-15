package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.Product;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public class DeviceTokenValue {

    public final int orgId;

    public final Product product;

    public final Device device;

    public DeviceTokenValue(int orgId, Product product, Device device) {
        this.orgId = orgId;
        this.product = product;
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
