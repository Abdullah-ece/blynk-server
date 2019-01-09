package cc.blynk.server.core.processors.rules.actions;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SetDeviceDataStreamAction.class, name = "SET_DEVICE_PIN_ACTION"),
        @JsonSubTypes.Type(value = SetReferenceDeviceDataStreamAction.class, name = "SET_REFERENCE_DEVICE_PIN_ACTION")
})
public abstract class BaseAction {

    public abstract boolean isValid();

    public abstract void execute(Organization org, Device device, String triggerValue);

}
