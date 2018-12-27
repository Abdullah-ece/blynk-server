package cc.blynk.server.core.processors.rules.conditions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class NumberUpdatedCondition extends BaseNumberCondition {

    @Override
    public boolean matches(double value) {
        return true;
    }

}
