package cc.blynk.server.core.processors.rules.value;

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
        @JsonSubTypes.Type(value = FormulaValue.class, name = "FORMULA_VALUE")
})
public abstract class ValueBase {

    public abstract boolean isValid();

    public abstract double resolve(Organization org, Device device, String triggerValue);

}
