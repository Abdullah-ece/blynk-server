package cc.blynk.server.core.model.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.06.17.
 */
public class ProductNotFoundException extends WebException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
