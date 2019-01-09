package cc.blynk.server.core.processors.rules.actions;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.RuleDataStream;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import cc.blynk.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class SetReferenceDeviceDataStreamAction extends BaseAction {

    public final RuleDataStream targetDataStream;

    public final ValueBase pinValue;

    @JsonCreator
    public SetReferenceDeviceDataStreamAction(@JsonProperty("targetDataStream") RuleDataStream targetDataStream,
                                              @JsonProperty("pinValue") ValueBase pinValue) {
        this.targetDataStream = targetDataStream;
        this.pinValue = pinValue;
    }

    public SetReferenceDeviceDataStreamAction(int productId, short pin, ValueBase pinValue) {
        this(new RuleDataStream(productId, pin, PinType.VIRTUAL), pinValue);
    }

    @Override
    public void execute(Organization org, Device device, String triggerValue) {
        double resolvedValue = pinValue.resolve(org, device, triggerValue);
        if (resolvedValue != NumberUtil.NO_RESULT) {
            device.updateValue(targetDataStream, String.valueOf(resolvedValue));
        }
    }

    @Override
    public boolean isValid() {
        return targetDataStream != null && pinValue != null && pinValue.isValid();
    }
}
