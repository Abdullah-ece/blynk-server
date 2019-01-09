package cc.blynk.server.core.processors.rules.conditions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public final class TriggerUpdatedCondition extends BaseCondition {

    @Override
    public boolean matches(double value) {
        return true;
    }

    @Override
    public boolean matches(String prevValue, String triggerValue) {
        return true;
    }
}
