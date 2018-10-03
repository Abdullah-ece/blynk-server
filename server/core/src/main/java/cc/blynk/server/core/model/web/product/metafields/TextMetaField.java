package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class TextMetaField extends MetaField {

    public static final String DEVICE_NAME = "Device Name";
    public static final String DEVICE_OWNER = "Device Owner";

    public final String value;

    @JsonCreator
    public TextMetaField(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("roleId") int roleId,
                         @JsonProperty("includeInProvision") boolean includeInProvision,
                         @JsonProperty("isMandatory") boolean isMandatory,
                         @JsonProperty("isDefault") boolean isDefault,
                         @JsonProperty("icon") String icon,
                         @JsonProperty("value") String value) {
        super(id, name, roleId, includeInProvision, isMandatory, isDefault, icon);
        this.value = value;
    }

    @Override
    public String getNotificationEmail() {
        return value;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new TextMetaField(id, metaField.name, metaField.roleId,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon, value);
    }

    @Override
    public MetaField copy() {
        return new TextMetaField(id, name, roleId,
                includeInProvision, isMandatory, isDefault,
                icon, value);
    }

}
