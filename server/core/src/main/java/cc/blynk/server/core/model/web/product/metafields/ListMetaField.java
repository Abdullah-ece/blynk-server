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
public class ListMetaField extends MetaField {

    public final String[] options;

    public final String selectedOption;

    @JsonCreator
    public ListMetaField(@JsonProperty("id") int id,
                         @JsonProperty("name") String name,
                         @JsonProperty("role") Role role,
                         @JsonProperty("isDefault") boolean isDefault,
                         @JsonProperty("options") String[] options,
                         @JsonProperty("selectedOption") String selectedOption) {
        super(id, name, role, isDefault);
        this.options = options;
        this.selectedOption = selectedOption;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        ListMetaField listMetaField = (ListMetaField) metaField;
        return new ListMetaField(id, metaField.name, metaField.role, metaField.isDefault,
                listMetaField.options, selectedOption);
    }

    @Override
    public MetaField copy() {
        return new ListMetaField(id, name, role, isDefault, options, selectedOption);
    }
}
