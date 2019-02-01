package cc.blynk.server.hardware.internal;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.18.
 */
public final class ProvisionedDeviceAddedMessage {

    public final Organization org;

    public final User user;

    public final Device device;

    public final int msgId;

    public final Product product;

    public ProvisionedDeviceAddedMessage(Organization org, User user,
                                         Device device, int msgId,
                                         Product product) {
        this.org = org;
        this.user = user;
        this.device = device;
        this.msgId = msgId;
        this.product = product;
    }
}

