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
public class MeasurementUnitMetaField extends MetaField {

    public final MeasurementUnit units;

    public final String value;

    @JsonCreator
    public MeasurementUnitMetaField(@JsonProperty("id") int id,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("role") Role role,
                                    @JsonProperty("isDefault") boolean isDefault,
                                    @JsonProperty("icon") String icon,
                                    @JsonProperty("units") MeasurementUnit units,
                                    @JsonProperty("value") String value) {
        super(id, name, role, isDefault, icon);
        this.units = units;
        this.value = value;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new MeasurementUnitMetaField(id, metaField.name, metaField.role, metaField.isDefault, metaField.icon,
                units, value);
    }

    @Override
    public MetaField copy() {
        return new MeasurementUnitMetaField(id, name, role, isDefault, icon, units, value);
    }
}
