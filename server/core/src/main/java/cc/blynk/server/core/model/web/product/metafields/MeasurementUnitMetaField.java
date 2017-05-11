package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class MeasurementUnitMetaField extends MetaField {

    public MeasurementUnit units;

    public String value;

    public MeasurementUnitMetaField() {
    }

    public MeasurementUnitMetaField(String name, Role role, MeasurementUnit units, String value) {
        super(name, role);
        this.units = units;
        this.value = value;
    }
}
