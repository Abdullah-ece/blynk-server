package cc.blynk.server.core.processors.rules;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.rules.actions.BaseAction;
import cc.blynk.server.core.processors.rules.conditions.BaseCondition;
import cc.blynk.server.core.processors.rules.triggers.BaseTrigger;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class Rule {

    public final String name;

    public final BaseTrigger trigger;

    public final BaseCondition condition;

    public final BaseAction action;

    @JsonCreator
    public Rule(@JsonProperty("name") String name,
                @JsonProperty("trigger") BaseTrigger trigger,
                @JsonProperty("condition") BaseCondition condition,
                @JsonProperty("action") BaseAction action) {
        this.name = name;
        this.trigger = trigger;
        this.condition = condition;
        this.action = action;
    }

    public boolean isValid(int productId, short pin, PinType pinType, String triggerValue, double triggerValueParsed) {
        return isSame(productId, pin, pinType)
                && isConditionMatches(triggerValue, triggerValueParsed)
                && isValidAction();
    }

    private boolean isSame(int productId, short pin, PinType pinType) {
        return this.trigger != null && this.trigger.isSame(productId, pin, pinType);
    }

    private boolean isConditionMatches(String triggerValueString, double triggerValueDouble) {
        return this.condition != null && this.condition.matches(triggerValueDouble)
                && this.condition.matches(triggerValueString);
    }

    private boolean isValidAction() {
        return this.action != null && this.action.isValid();
    }

}
