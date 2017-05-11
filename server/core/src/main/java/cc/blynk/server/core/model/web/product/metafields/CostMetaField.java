package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;

import java.util.Currency;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class CostMetaField extends MetaField {

    public Currency currency;

    public double price;

    public double perValue;

    public MeasurementUnit units;

    public CostMetaField() {
    }

    public CostMetaField(String name, Role role, Currency currency, double price, double perValue, MeasurementUnit units) {
        super(name, role);
        this.currency = currency;
        this.price = price;
        this.perValue = perValue;
        this.units = units;
    }
}
