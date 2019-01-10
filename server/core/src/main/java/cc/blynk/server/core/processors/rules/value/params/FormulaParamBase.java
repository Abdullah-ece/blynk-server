package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TriggerDataStreamFormulaParam.class, name = "TRIGGER_STREAM_PARAM"),
        @JsonSubTypes.Type(value = TriggerDeviceDataStreamFormulaParam.class, name = "TRIGGER_DEVICE_STREAM_PARAM"),
        @JsonSubTypes.Type(value = DeviceReferenceFormulaParam.class, name = "DEVICE_REFERENCE_PARAM"),
        @JsonSubTypes.Type(value = BackDeviceReferenceFormulaParam.class, name = "BACK_DEVICE_REFERENCE_PARAM")
})
public abstract class FormulaParamBase {

    public abstract boolean isValid();

    public abstract Object resolve(Organization org, Device device, String triggerValue);

}
