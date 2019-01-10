package cc.blynk.server.core.processors.rules.actions;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.DeviceDataStream;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import cc.blynk.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class SetDeviceDataStreamAction extends BaseAction {

    public final DeviceDataStream targetDataStream;

    public final ValueBase pinValue;

    @JsonCreator
    public SetDeviceDataStreamAction(@JsonProperty("targetDataStream") DeviceDataStream targetDataStream,
                                     @JsonProperty("pinValue") ValueBase pinValue) {
        this.targetDataStream = targetDataStream;
        this.pinValue = pinValue;
    }

    public SetDeviceDataStreamAction(short pin, ValueBase pinValue) {
        this(new DeviceDataStream(pin, PinType.VIRTUAL), pinValue);
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
