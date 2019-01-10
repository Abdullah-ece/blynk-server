package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class TriggerDataStreamFormulaParam extends FormulaParamBase {

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Object resolve(Organization org, Device device, String triggerValue) {
        return triggerValue;
    }
}
