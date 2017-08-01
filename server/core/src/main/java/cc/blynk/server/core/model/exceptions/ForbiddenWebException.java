package cc.blynk.server.core.model.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.06.17.
 */
public class ForbiddenWebException extends WebException {

    public ForbiddenWebException(String message) {
        super(message);
    }
}
