package cc.blynk.server.core.model.exceptions;

import cc.blynk.server.core.protocol.exceptions.JsonException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.06.17.
 */
public class ProductNotFoundException extends JsonException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
