package cc.blynk.server.core.protocol.model.messages;

import cc.blynk.server.core.protocol.enums.Command;
import cc.blynk.server.core.protocol.exceptions.UnsupportedCommandException;
import cc.blynk.server.core.protocol.model.messages.appllication.GetServerMessage;
import cc.blynk.server.core.protocol.model.messages.appllication.LoginMessage;
import cc.blynk.server.core.protocol.model.messages.appllication.RegisterMessage;
import cc.blynk.server.core.protocol.model.messages.appllication.ResetPasswordMessage;
import cc.blynk.server.core.protocol.model.messages.appllication.sharing.ShareLoginMessage;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
import cc.blynk.server.core.protocol.model.messages.hardware.HardwareLogEventMessage;
import cc.blynk.server.core.protocol.model.messages.hardware.HardwareLoginMessage;
import cc.blynk.server.core.protocol.model.messages.web.WebLoginViaInviteMessage;

import static cc.blynk.server.core.protocol.enums.Command.GET_SERVER;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_REGISTER;
import static cc.blynk.server.core.protocol.enums.Command.RESET_PASSWORD;
import static cc.blynk.server.core.protocol.enums.Command.SHARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_JSON;
import static cc.blynk.server.core.protocol.enums.Command.WEB_LOGIN_VIA_INVITE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public final class MessageFactory {

    private MessageFactory() {
    }

    public static MessageBase produce(int messageId, short commandId, String body) {
        switch (commandId) {
            case MOBILE_REGISTER:
                return new RegisterMessage(messageId, body);
            case LOGIN :
                return new LoginMessage(messageId, body);
            case WEB_LOGIN_VIA_INVITE :
                return new WebLoginViaInviteMessage(messageId, body);
            case HARDWARE_LOGIN :
                return new HardwareLoginMessage(messageId, body);
            case SHARE_LOGIN :
                return new ShareLoginMessage(messageId, body);
            case HARDWARE :
                return new HardwareMessage(messageId, body);
            case GET_SERVER :
                return new GetServerMessage(messageId, body);
            case HARDWARE_LOG_EVENT :
                return new HardwareLogEventMessage(messageId, body);
            case RESET_PASSWORD :
                return new ResetPasswordMessage(messageId, body);
            case WEB_JSON :
                return new WebJsonMessage(messageId, body);
            default:
                if (commandId < Command.LAST_COMMAND_INDEX) {
                    return new StringMessage(messageId, commandId, body);
                }
                throw new UnsupportedCommandException("Command not supported. Code : " + commandId, messageId);
        }
    }

}
