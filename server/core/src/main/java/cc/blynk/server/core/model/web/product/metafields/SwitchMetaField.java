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
public class SwitchMetaField extends RangeMetaField {

    public final String value;

    @JsonCreator
    public SwitchMetaField(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("role") Role role,
                           @JsonProperty("from") int from,
                           @JsonProperty("to") int to,
                           @JsonProperty("value") String value) {
        super(id, name, role, from, to);
        this.value = value;
    }

    @Override
    public MetaField copy() {
        return new SwitchMetaField(id, name ,role, from, to, value);
    }

}
