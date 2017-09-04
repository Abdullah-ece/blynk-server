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
public class RangeMetaField extends MetaField {

    public final int from;

    public final int to;

    @JsonCreator
    public RangeMetaField(@JsonProperty("id") int id,
                          @JsonProperty("name") String name,
                          @JsonProperty("role") Role role,
                          @JsonProperty("isDefault") boolean isDefault,
                          @JsonProperty("from") int from,
                          @JsonProperty("to") int to) {
        super(id, name, role, isDefault);
        this.from = from;
        this.to = to;
    }

    @Override
    public MetaField copy() {
        return new RangeMetaField(id, name, role, isDefault, from, to);
    }
}
