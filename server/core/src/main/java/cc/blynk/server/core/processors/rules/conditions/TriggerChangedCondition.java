package cc.blynk.server.core.processors.rules.conditions;

/**
 * Trigger condition which means value was changed.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public final class TriggerChangedCondition extends BaseCondition {

    @Override
    public boolean matches(double value) {
        return true;
    }

    @Override
    public boolean matches(String prevValue, String triggerValue) {
        return !triggerValue.equals(prevValue);
    }
}
