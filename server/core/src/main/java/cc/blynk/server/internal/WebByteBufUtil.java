package cc.blynk.server.internal;

import cc.blynk.server.core.protocol.model.messages.WebJsonMessage;

import static cc.blynk.server.core.protocol.enums.Response.DEVICE_NOT_IN_NETWORK;
import static cc.blynk.server.core.protocol.enums.Response.FACEBOOK_USER_LOGIN_WITH_PASS;
import static cc.blynk.server.core.protocol.enums.Response.ILLEGAL_COMMAND_BODY;
import static cc.blynk.server.core.protocol.enums.Response.NOT_ALLOWED;
import static cc.blynk.server.core.protocol.enums.Response.USER_NOT_AUTHENTICATED;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 08.08.18.
 */
public final class WebByteBufUtil {

    private WebByteBufUtil() {
    }

    public static WebJsonMessage json(int msgId, String message) {
        return new WebJsonMessage(msgId, message);
    }

    public static WebJsonMessage quotaLimit(int msgId) {
        return new WebJsonMessage(msgId, "Request quota limit reached.");
    }

    public static WebJsonMessage facebookUserLoginWithPass(int msgId, String msg) {
        return new WebJsonMessage(msgId, msg, FACEBOOK_USER_LOGIN_WITH_PASS);
    }

    public static WebJsonMessage notAuthenticated(int msgId, String msg) {
        return new WebJsonMessage(msgId, msg, USER_NOT_AUTHENTICATED);
    }

    public static WebJsonMessage illegalCommandBody(int msgId) {
        return new WebJsonMessage(msgId, "Wrong income message format.", ILLEGAL_COMMAND_BODY);
    }

    public static WebJsonMessage notAllowed(int msgId, String msg) {
        return new WebJsonMessage(msgId, msg, NOT_ALLOWED);
    }

    public static WebJsonMessage deviceNotInNetwork(int msgId) {
        return new WebJsonMessage(msgId, "Device not in the network.", DEVICE_NOT_IN_NETWORK);
    }

    public static WebJsonMessage userHasNoAccessToOrg(int msgId) {
        return new WebJsonMessage(msgId, "User has no access to this organization.");
    }

    public static WebJsonMessage productNotExists(int msgId) {
        return new WebJsonMessage(msgId, "Product with passed id doesn't exist.");
    }

}
