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
public class SwitchMetaField extends MetaField {

    public final String value;

    public final String from;

    public final String to;

    @JsonCreator
    public SwitchMetaField(@JsonProperty("id") int id,
                           @JsonProperty("name") String name,
                           @JsonProperty("role") Role role,
                           @JsonProperty("isDefault") boolean isDefault,
                           @JsonProperty("from") String from,
                           @JsonProperty("to") String to,
                           @JsonProperty("value") String value) {
        super(id, name, role, isDefault);
        this.value = value;
        this.to = to;
        this.from = from;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new SwitchMetaField(id, metaField.name, metaField.role, metaField.isDefault,
                from, to, value);
    }

    @Override
    public MetaField copy() {
        return new SwitchMetaField(id, name, role, isDefault, from, to, value);
    }

}
