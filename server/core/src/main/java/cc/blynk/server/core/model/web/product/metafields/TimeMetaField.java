package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class TimeMetaField extends MetaField {

    public final Date time;

    @JsonCreator
    public TimeMetaField(@JsonProperty("name") String name,
                         @JsonProperty("role") Role role,
                         @JsonProperty("time") Date time) {
        super(name, role);
        this.time = time;
    }

    @Override
    public MetaField copy() {
        return new TimeMetaField(name, role, (Date) time.clone());
    }
}
