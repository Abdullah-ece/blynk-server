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
public class TextMetaField extends MetaField {

    public final String value;

    @JsonCreator
    public TextMetaField(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("role") Role role,
                         @JsonProperty("isDefault") boolean isDefault,
                         @JsonProperty("value") String value) {
        super(id, name, role, isDefault);
        this.value = value;
    }

    @Override
    public String getNotificationEmail() {
        return value;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new TextMetaField(id, metaField.name, metaField.role, metaField.isDefault, value);
    }

    @Override
    public MetaField copy() {
        return new TextMetaField(id, name, role, isDefault, value);
    }

}
