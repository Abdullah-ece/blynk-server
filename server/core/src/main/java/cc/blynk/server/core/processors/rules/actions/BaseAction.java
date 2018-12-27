package cc.blynk.server.core.processors.rules.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SetNumberPinAction.class, name = "SET_NUMBER_PIN_ACTION")
})
public abstract class BaseAction {

    public abstract boolean isValid();

}
