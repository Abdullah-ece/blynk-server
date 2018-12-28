package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ListMetaField extends MetaField {

    public final String[] options;

    public final String selectedOption;

    @JsonCreator
    public ListMetaField(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("roleIds") int[] roleIds,
                         @JsonProperty("includeInProvision") boolean includeInProvision,
                         @JsonProperty("isMandatory") boolean isMandatory,
                         @JsonProperty("isDefault") boolean isDefault,
                         @JsonProperty("icon") String icon,
                         @JsonProperty("options") String[] options,
                         @JsonProperty("selectedOption") String selectedOption) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.options = options;
        this.selectedOption = selectedOption;
    }

    @Override
    public void trim() {
        for (int i = 0; i < options.length; i++) {
            options[i] = options[i].trim();
        }
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        ListMetaField listMetaField = (ListMetaField) metaField;
        return new ListMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon,
                listMetaField.options, selectedOption);
    }

    @Override
    public MetaField copy() {
        return new ListMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, options, selectedOption);
    }

}
