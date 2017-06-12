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
    public MeasurementUnitMetaField(@JsonProperty("name") String name,
                                    @JsonProperty("role") Role role,
                                    @JsonProperty("units") MeasurementUnit units,
                                    @JsonProperty("value") String value) {
        super(name, role);
        this.units = units;
        this.value = value;
    }

    @Override
    public MetaField copy() {
        return new MeasurementUnitMetaField(name, role, units, value);
    }
}
