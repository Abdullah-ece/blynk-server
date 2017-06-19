package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class NumberMetaField extends MetaField {

    public final double value;

    @JsonCreator
    public NumberMetaField(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("role") Role role,
                           @JsonProperty("value") double value) {
        super(id, name, role);
        this.value = value;
    }

    @Override
    public MetaField copy() {
        return new NumberMetaField(id, name, role, value);
    }
}
