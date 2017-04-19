package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class TextMetaField extends MetaField {

    public String value;

    public TextMetaField() {
    }

    public TextMetaField(String name, Role role, String value) {
        super(name, role);
        this.value = value;
    }
}
