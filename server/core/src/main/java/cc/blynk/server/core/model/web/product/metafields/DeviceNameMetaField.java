package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.protocol.exceptions.IllegalCommandBodyException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class DeviceNameMetaField extends MetaField {

    public final String value;

    @JsonCreator
    public DeviceNameMetaField(@JsonProperty("id") int id,
                               @JsonProperty("name") String name,
                               @JsonProperty("roleIds") int[] roleIds,
                               @JsonProperty("includeInProvision") boolean includeInProvision,
                               @JsonProperty("isMandatory") boolean isMandatory,
                               @JsonProperty("isDefault") boolean isDefault,
                               @JsonProperty("icon") String icon,
                               @JsonProperty("value") String value) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.value = value;
    }

    @Override
    public void validateAll() {
        super.validateAll();
        if (isEmptyValue()) {
            throw new IllegalCommandBodyException("Device name metafield value is empty.");
        }
    }

    private boolean isEmptyValue() {
        return this.value == null || this.value.isEmpty();
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new DeviceNameMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon, value);
    }

    @Override
    public MetaField copy() {
        return new DeviceNameMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, value);
    }
}
