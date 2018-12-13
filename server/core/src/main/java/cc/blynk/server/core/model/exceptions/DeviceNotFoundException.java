package cc.blynk.server.core.model.exceptions;

import cc.blynk.server.core.protocol.exceptions.JsonException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.06.17.
 */
public class DeviceNotFoundException extends JsonException {

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
