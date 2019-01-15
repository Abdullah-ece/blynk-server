package cc.blynk.server.core.session;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.ArrayUtil;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.09.15.
 */
public final class HardwareStateHolder  {

    public final int orgId;
    public final Product product;
    public final Device device;

    public HardwareStateHolder(int orgId, Product product, Device device) {
        this.orgId = orgId;
        this.product = product;
        this.device = device;
    }

    @Override
    public String toString() {
        return "HardwareStateHolder{"
                + "orgId=" + orgId
                + ", productId=" + product.id
                + ", device=" + device
                + '}';
    }

    public boolean isSameDevice(int deviceId) {
        return device.id == deviceId;
    }

    public boolean contains(int[] devicesIds) {
        return ArrayUtil.contains(devicesIds, device.id);
    }

}
