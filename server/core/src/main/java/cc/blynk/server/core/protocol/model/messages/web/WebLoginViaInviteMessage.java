package cc.blynk.server.core.protocol.model.messages.web;

import cc.blynk.server.core.protocol.model.messages.StringMessage;

import static cc.blynk.server.core.protocol.enums.Command.WEB_LOGIN_VIA_INVITE;

public class WebLoginViaInviteMessage extends StringMessage {

    public WebLoginViaInviteMessage(int messageId, String body) {
        super(messageId, WEB_LOGIN_VIA_INVITE, body);
    }

    @Override
    public String toString() {
        return "WebLoginViaInviteMessage{" + super.toString() + "}";
    }
}
