package cc.blynk.server.core.model.web.response;

import cc.blynk.utils.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.04.17.
 */
public class ErrorMessage {

    public final Error error;

    public ErrorMessage(String message) {
        this.error = new Error(message);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    private static final class Error {

        public final String message;

        public Error(String message) {
            this.message = message;
        }

    }
}
