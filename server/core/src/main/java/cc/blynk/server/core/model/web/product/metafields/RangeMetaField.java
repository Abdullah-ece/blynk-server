package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class RangeMetaField extends MetaField {

    public int from;

    public int to;

    public RangeMetaField() {
    }

    public RangeMetaField(String name, Role role, int from, int to) {
        super(name, role);
        this.from = from;
        this.to = to;
    }
}
