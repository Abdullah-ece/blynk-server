package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class NumberMetaField extends MetaField {

    public double value;

    public NumberMetaField() {
    }

    public NumberMetaField(String name, Role role, double value) {
        super(name, role);
        this.value = value;
    }
}
