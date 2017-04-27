package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ShiftMetaField extends MetaField {

    public int from;

    public int to;

    public ShiftMetaField() {
        this.from = -1;
        this.to = -1;
    }

    public ShiftMetaField(String name, Role role, int from, int to) {
        super(name, role);
        this.from = from;
        this.to = to;
    }
}
