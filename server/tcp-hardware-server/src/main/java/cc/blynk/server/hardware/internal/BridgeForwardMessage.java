package cc.blynk.server.hardware.internal;

import cc.blynk.server.core.dao.DeviceValue;
import cc.blynk.server.core.protocol.model.messages.StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.18.
 */
public class BridgeForwardMessage {

    public final int orgId;

    public final StringMessage message;

    public final DeviceValue tokenValue;

    public BridgeForwardMessage(int orgId, StringMessage bridgeMessage, DeviceValue tokenValue) {
        this.orgId = orgId;
        this.message = bridgeMessage;
        this.tokenValue = tokenValue;
    }
}

