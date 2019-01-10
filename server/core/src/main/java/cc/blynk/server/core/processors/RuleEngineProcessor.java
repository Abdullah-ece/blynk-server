package cc.blynk.server.core.processors;

import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.Rule;
import cc.blynk.server.core.processors.rules.actions.BaseAction;
import cc.blynk.utils.NumberUtil;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.12.18.
 */
public final class RuleEngineProcessor {

    private final SessionDao sessionDao;

    public RuleEngineProcessor(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void process(Organization org, Device device,
                        short pin, PinType pinType,
                        String prevValue, String triggerValue) {
        if (org.ruleGroup != null) {
            double triggerValueParsed = NumberUtil.parseDouble(triggerValue);
            for (Rule rule : org.ruleGroup.rules) {
                if (rule.isValid(device.productId, pin, pinType, prevValue, triggerValueParsed, triggerValue)) {
                    for (BaseAction action : rule.actions) {
                        if (action.isValid()) {
                            action.execute(sessionDao, org, device, triggerValue);
                        }
                    }
                }
            }
        }
    }
}
