package cc.blynk.server.core.processors.rules;

import cc.blynk.server.core.protocol.exceptions.JsonException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class RuleEngineException extends JsonException {

    public RuleEngineException(String message) {
        super(message);
    }
}
