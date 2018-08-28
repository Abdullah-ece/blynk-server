package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
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

    @JsonCreator
    public NumberMetaField(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("role") Role role,
                           @JsonProperty("isDefault") boolean isDefault,
                           @JsonProperty("icon") String icon,
                           @JsonProperty("min") float min,
                           @JsonProperty("max") float max,
                           @JsonProperty("value") double value) {
        super(id, name, role, isDefault, icon);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new NumberMetaField(id,
                metaField.name, metaField.role, metaField.isDefault, metaField.icon, min, max, value);
    }

    @Override
    public MetaField copy() {
        return new NumberMetaField(id, name, role, isDefault, icon, min, max, value);
    }
}
