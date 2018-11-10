package cc.blynk.server.hardware.internal;

import cc.blynk.server.core.dao.TokenValue;
import cc.blynk.server.core.protocol.model.messages.StringMessage;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.02.18.
 */
public class BridgeForwardMessage {

    public final int orgId;

    public final StringMessage message;

    public final TokenValue tokenValue;

    public final String email;

    public BridgeForwardMessage(int orgId, StringMessage bridgeMessage, TokenValue tokenValue, String email) {
        this.orgId = orgId;
        this.message = bridgeMessage;
        this.tokenValue = tokenValue;
        this.email = email;
    }
}

