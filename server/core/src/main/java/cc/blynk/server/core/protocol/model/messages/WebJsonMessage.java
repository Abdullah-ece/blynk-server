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

    public WebJsonMessage(int messageId, String message) {
        super(messageId, WEB_JSON);
        this.message = message;
    }

    @Override
    public byte[] getBytes() {
        //do not use json here as it is very simple structure
        return toJson(message).getBytes(UTF_8);
    }

    public static String toJson(String message) {
        return "{\"error\":{\"message\":\"" + message + "\"}}";
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
