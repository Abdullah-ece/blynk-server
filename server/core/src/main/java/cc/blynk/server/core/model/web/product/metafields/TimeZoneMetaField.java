package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.09.18.
 */
public class TimeZoneMetaField extends MetaField {

    public final String value;

    @JsonCreator
    public TimeZoneMetaField(@JsonProperty("id") int id,
                             @JsonProperty("name") String name,
                             @JsonProperty("role") Role role,
                             @JsonProperty("includeInProvision") boolean includeInProvision,
                             @JsonProperty("isMandatory") boolean isMandatory,
                             @JsonProperty("isDefault") boolean isDefault,
                             @JsonProperty("icon") String icon,
                             @JsonProperty("value") String value) {
        super(id, name, role, includeInProvision, isMandatory, isDefault, icon);
        this.value = value;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new TimeZoneMetaField(id, metaField.name, metaField.role,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon, value);
    }

    @Override
    public MetaField copy() {
        return new TimeZoneMetaField(id, name, role,
                includeInProvision, isMandatory, isDefault,
                icon, value);
    }

}
