package cc.blynk.server.core.model.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.06.17.
 */
public abstract class WebException extends RuntimeException {

    public WebException(String message) {
        super(message, null, true, false);
    }

}
