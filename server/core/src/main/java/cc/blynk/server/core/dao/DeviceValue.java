package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public class DeviceValue {

    public final Organization org;

    public final Product product;

    public final Device device;

    public DeviceValue(Organization org, Product product, Device device) {
        this.org = org;
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
        return this.org.id == orgId;
    }

}
