package cc.blynk.server.core.processors.rules.conditions;

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
        @JsonSubTypes.Type(value = TriggerUpdatedCondition.class, name = "UPDATED"),
        @JsonSubTypes.Type(value = TriggerChangedCondition.class, name = "CHANGED")
})
public abstract class BaseCondition {

    public abstract boolean matches(double triggerValue);

    public abstract boolean matches(String prevValue, String triggerValue);

}
