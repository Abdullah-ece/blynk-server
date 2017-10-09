package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalTime;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class RangeTimeMetaField extends MetaField {

    @JsonSerialize(using = LocalTimeToIntSerializer.class)
    @JsonDeserialize(using = IntToLocalTimeSerializer.class)
    public final LocalTime from;

    @JsonSerialize(using = LocalTimeToIntSerializer.class)
    @JsonDeserialize(using = IntToLocalTimeSerializer.class)
    public final LocalTime to;

    @JsonCreator
    public RangeTimeMetaField(@JsonProperty("id") int id,
                              @JsonProperty("name") String name,
                              @JsonProperty("role") Role role,
                              @JsonProperty("isDefault") boolean isDefault,
                              @JsonProperty("from") LocalTime from,
                              @JsonProperty("to") LocalTime to) {
        super(id, name, role, isDefault);
        this.from = from;
        this.to = to;
    }

    @Override
    public MetaField copy() {
        return new RangeTimeMetaField(id, name, role, isDefault, from, to);
    }
}
