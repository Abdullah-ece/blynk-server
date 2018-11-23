package cc.blynk.server.internal;

import cc.blynk.server.core.protocol.model.messages.WebJsonMessage;

import static cc.blynk.server.core.protocol.enums.Response.DEVICE_NOT_IN_NETWORK;

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
        return json(msgId, "Request quota limit reached.");
    }

    public static WebJsonMessage deviceNotInNetwork(int msgId) {
        return  new WebJsonMessage(msgId, "Device not in the network.", DEVICE_NOT_IN_NETWORK);
    }

    public static WebJsonMessage userHasNoAccessToOrg(int msgId) {
        return json(msgId, "User has no access to this organization.");
    }

    public static WebJsonMessage productNotExists(int msgId) {
        return json(msgId, "Product with passed id doesn't exist.");
    }

}
