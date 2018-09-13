package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Currency;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class CostMetaField extends MetaField {

    public final Currency currency;

    public final double price;

    public final double perValue;

    public final MeasurementUnit units;

    public final float min;

    public final float max;

    @JsonCreator
    public CostMetaField(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("roleId") int roleId,
                         @JsonProperty("isDefault") boolean isDefault,
                         @JsonProperty("icon") String icon,
                         @JsonProperty("currency") Currency currency,
                         @JsonProperty("price") double price,
                         @JsonProperty("perValue") double perValue,
                         @JsonProperty("units") MeasurementUnit units,
                         @JsonProperty("min") float min,
                         @JsonProperty("max") float max) {
        super(id, name, roleId, isDefault, icon);
        this.currency = currency;
        this.price = price;
        this.perValue = perValue;
        this.units = units;
        this.min = min;
        this.max = max;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new CostMetaField(id, metaField.name, metaField.roleId, metaField.isDefault, icon,
                currency, price, perValue, units, min, max);
    }

    @Override
    public MetaField copy() {
        return new CostMetaField(id, name, roleId, isDefault, icon, currency, price, perValue, units, min, max);
    }
}
