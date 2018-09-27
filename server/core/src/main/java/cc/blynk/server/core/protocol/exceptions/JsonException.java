package cc.blynk.server.core.protocol.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class JsonException extends RuntimeException {

    public final int msgId;

    public JsonException(int msgId, String message) {
        super(message, null, true, false);
        this.msgId = msgId;
    }

}
