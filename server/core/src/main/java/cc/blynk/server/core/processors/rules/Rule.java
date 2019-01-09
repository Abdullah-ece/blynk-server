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
public final class Rule {

    private static final BaseTrigger[] EMPTY_TRIGGERS = {};
    private static final BaseAction[] EMPTY_ACTIONS = {};

    public final String name;

    public final BaseTrigger[] triggers;

    public final BaseCondition condition;

    public final BaseAction[] actions;

    @JsonCreator
    public Rule(@JsonProperty("name") String name,
                @JsonProperty("triggers") BaseTrigger[] triggers,
                @JsonProperty("condition") BaseCondition condition,
                @JsonProperty("actions") BaseAction[] actions) {
        this.name = name;
        this.triggers = triggers == null ? EMPTY_TRIGGERS : triggers;
        this.condition = condition;
        this.actions = actions == null ? EMPTY_ACTIONS : actions;
    }

    public boolean isValid(int productId, short pin, PinType pinType,
                           String prevValue, double triggerValueParsed, String triggerValue) {
        return isSame(productId, pin, pinType)
                && isConditionMatches(prevValue, triggerValueParsed, triggerValue)
                && isValidActions();
    }

    private boolean isSame(int productId, short pin, PinType pinType) {
        for (BaseTrigger trigger : triggers) {
            if (trigger.isSame(productId, pin, pinType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isConditionMatches(String prevValue, double triggerValueDouble, String triggerValue) {
        return this.condition != null && this.condition.matches(triggerValueDouble)
                && this.condition.matches(prevValue, triggerValue);
    }

    private boolean isValidActions() {
        return this.actions.length > 0;
    }

}
