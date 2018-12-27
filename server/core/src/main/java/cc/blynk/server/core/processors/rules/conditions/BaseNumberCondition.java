package cc.blynk.server.core.processors.rules.conditions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public abstract class BaseNumberCondition extends BaseCondition {

    @Override
    public boolean matches(String triggerValue) {
        return true;
    }

}
