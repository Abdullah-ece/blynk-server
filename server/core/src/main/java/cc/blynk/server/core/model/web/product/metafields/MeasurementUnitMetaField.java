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
public class MeasurementUnitMetaField extends MetaField {

    public final MeasurementUnit units;

    public final double value;

    public final float min;

    public final float max;

    public final float step;

    @JsonCreator
    public MeasurementUnitMetaField(@JsonProperty("id") int id,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("roleId") int roleId,
                                    @JsonProperty("includeInProvision") boolean includeInProvision,
                                    @JsonProperty("isDefault") boolean isDefault,
                                    @JsonProperty("isMandatory") boolean isMandatory,
                                    @JsonProperty("icon") String icon,
                                    @JsonProperty("units") MeasurementUnit units,
                                    @JsonProperty("value") double value,
                                    @JsonProperty("min") float min,
                                    @JsonProperty("max") float max,
                                    @JsonProperty("step") float step) {
        super(id, name, roleId, includeInProvision, isMandatory, isDefault, icon);
        this.units = units;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void validate() {
        super.validate();
        if (units == null) {
            throw new IllegalCommandBodyException("Metafield is not valid. Units field is empty.");
        }
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new MeasurementUnitMetaField(id, metaField.name, metaField.roleId,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon,
                units, value, min, max, step);
    }

    @Override
    public MetaField copy() {
        return new MeasurementUnitMetaField(id, name, roleId,
                includeInProvision, isMandatory, isDefault,
                icon, units, value, min, max, step);
    }
}
