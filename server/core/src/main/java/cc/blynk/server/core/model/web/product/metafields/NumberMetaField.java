package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class NumberMetaField extends MetaField {

    public final double value;

    public final float min;

    public final float max;

    public final float step;

    @JsonCreator
    public NumberMetaField(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("roleIds") int[] roleIds,
                           @JsonProperty("includeInProvision") boolean includeInProvision,
                           @JsonProperty("isMandatory") boolean isMandatory,
                           @JsonProperty("isDefault") boolean isDefault,
                           @JsonProperty("icon") String icon,
                           @JsonProperty("min") float min,
                           @JsonProperty("max") float max,
                           @JsonProperty("value") double value,
                           @JsonProperty("step") float step) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new NumberMetaField(id,
                metaField.name, metaField.roleIds, metaField.includeInProvision, metaField.isDefault,
                metaField.isMandatory, metaField.icon, min, max, value, step);
    }

    @Override
    public MetaField copy() {
        return new NumberMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, min, max, value, step);
    }
}
