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

    public final float min;

    public final float max;

    @JsonCreator
    public MeasurementUnitMetaField(@JsonProperty("id") int id,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("role") Role role,
                                    @JsonProperty("includeInProvision") boolean includeInProvision,
                                    @JsonProperty("isMandatory") boolean isMandatory,
                                    @JsonProperty("icon") String icon,
                                    @JsonProperty("units") MeasurementUnit units,
                                    @JsonProperty("value") String value,
                                    @JsonProperty("min") float min,
                                    @JsonProperty("max") float max) {
        super(id, name, role, includeInProvision, isMandatory, icon);
        this.units = units;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new MeasurementUnitMetaField(id, metaField.name, metaField.role,
                metaField.includeInProvision, metaField.isMandatory, metaField.icon,
                units, value, min, max);
    }

    @Override
    public MetaField copy() {
        return new MeasurementUnitMetaField(id, name, role,
                includeInProvision, isMandatory, icon, units, value, min, max);
    }
}
