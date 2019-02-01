package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class MultiTextMetaField extends MetaField {

    public final String[] values;

    @JsonCreator
    public MultiTextMetaField(@JsonProperty("id") int id,
                              @JsonProperty("name") String name,
                              @JsonProperty("roleIds") int[] roleIds,
                              @JsonProperty("includeInProvision") boolean includeInProvision,
                              @JsonProperty("isMandatory") boolean isMandatory,
                              @JsonProperty("isDefault") boolean isDefault,
                              @JsonProperty("icon") String icon,
                              @JsonProperty("values") String[] values) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.values = values;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new MultiTextMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon, values);
    }

    @Override
    public MetaField copy() {
        return new MultiTextMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, values);
    }

}
