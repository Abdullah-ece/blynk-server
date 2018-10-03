package cc.blynk.server.hardware.internal;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.18.
 */
public class CreateSessionForwardMessage {

    public final User user;

    public final DashBoard dash;

    public final Device device;

    public final int msgId;

    public CreateSessionForwardMessage(User user, DashBoard dash, Device device, int msgId) {
        this.user = user;
        this.dash = dash;
        this.device = device;
        this.msgId = msgId;
    }
}

