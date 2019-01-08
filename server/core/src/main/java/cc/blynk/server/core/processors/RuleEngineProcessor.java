package cc.blynk.server.core.processors;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.Rule;
import cc.blynk.server.core.processors.rules.actions.BaseAction;
import cc.blynk.server.core.processors.rules.actions.SetNumberPinAction;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import cc.blynk.utils.NumberUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.12.18.
 */
public final class RuleEngineProcessor {

    private static final Logger log = LogManager.getLogger(RuleEngineProcessor.class);

    public RuleEngineProcessor() {
    }

    public void process(Organization org, Device device,
                        short pin, PinType pinType,
                        String prevValue, String triggerValue) {
        if (org.ruleGroup != null) {
            double triggerValueParsed = NumberUtil.parseDouble(triggerValue);
            for (Rule rule : org.ruleGroup.rules) {
                if (rule.isValid(device.productId, pin, pinType, prevValue, triggerValueParsed, triggerValue)) {
                    execute(org, rule, device, triggerValue);
                }
            }
        }
    }

    private void execute(Organization org, Rule rule, Device device,
                         String triggerValue) {
        BaseAction action = rule.action;
        if (action instanceof SetNumberPinAction) {
            SetNumberPinAction setNumberPinAction = (SetNumberPinAction) action;
            ValueBase valueBase = setNumberPinAction.pinValue;
            double resolvedValue = valueBase.resolve(org, device, triggerValue);
            if (resolvedValue != NumberUtil.NO_RESULT) {
                device.updateValue(setNumberPinAction.targetDataStream, String.valueOf(resolvedValue));
            }
        }
    }

}
