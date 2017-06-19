package cc.blynk.server.core.model.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.06.17.
 */
public class NotAllowedWebException extends WebException {

    public NotAllowedWebException(String message) {
        super(message);
    }
}
