package cc.blynk.server.core.model.web.response;

import cc.blynk.utils.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.04.17.
 */
public class OkMessage {

    public final Success success;

    public OkMessage(String message) {
        this.success = new Success(message);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    private static final class Success {

        public final String message;

        Success(String message) {
            this.message = message;
        }

    }
}
