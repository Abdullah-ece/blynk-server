package cc.blynk.server.core.protocol.model.messages;

import static cc.blynk.server.core.protocol.enums.Command.WEB_JSON;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Special kind of response message that always return string
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class WebJsonMessage extends MessageBase {

    public final String message;

    public final int code;

    public WebJsonMessage(int id, String message, int code) {
        super(id, WEB_JSON);
        this.message = message;
        this.code = code;
    }

    public WebJsonMessage(int messageId, String message) {
        super(messageId, WEB_JSON);
        this.message = message;
        this.code = 0;
    }

    public static String toJson(String message, int code) {
        if (code == 0) {
            return "{\"error\":{\"message\":\"" + message + "\"}}";
        }
        return "{\"error\":{\"message\":\"" + message + "\",\"code\":" + code + "}}";
    }

    @Override
    public byte[] getBytes() {
        //do not use json here as it is very simple structure
        return toJson(message, code).getBytes(UTF_8);
    }

    @Override
    public String toString() {
        return super.toString() + ", message='" + message + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        WebJsonMessage that = (WebJsonMessage) o;

        return !(message != null ? !message.equals(that.message) : that.message != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
