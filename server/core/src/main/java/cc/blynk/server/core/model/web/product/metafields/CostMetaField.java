package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;

import java.util.Currency;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class CostMetaField extends MetaField {

    public Currency currency;

    public double value;

    public CostMetaField() {
    }

    public CostMetaField(String name, Role role, Currency currency, double value) {
        super(name, role);
        this.currency = currency;
        this.value = value;
    }
}
