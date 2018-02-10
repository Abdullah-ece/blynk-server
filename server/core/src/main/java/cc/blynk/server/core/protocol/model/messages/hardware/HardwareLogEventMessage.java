package cc.blynk.server.core.protocol.model.messages.hardware;

import cc.blynk.server.core.protocol.model.messages.StringMessage;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class HardwareLogEventMessage extends StringMessage {

    public HardwareLogEventMessage(int messageId, String body) {
        super(messageId, HARDWARE_LOG_EVENT, body);
    }

    @Override
    public String toString() {
        return "HardwareLogEventMessage{" + super.toString() + "}";
    }
}
