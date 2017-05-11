package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;

import java.util.Date;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class TimeMetaField extends MetaField {

    public Date time;

    public TimeMetaField() {
    }

    public TimeMetaField(String name, Role role, Date time) {
        super(name, role);
        this.time = time;
    }
}
