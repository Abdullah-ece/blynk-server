package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class TemplateIdMetaField extends MetaField {

    public final String[] options;

    public final String selectedOption;

    @JsonCreator
    public TemplateIdMetaField(@JsonProperty("id") int id,
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

    public boolean containsTemplate(String templateId) {
        return ArrayUtil.contains(this.options, templateId);
    }

    @Override
    public MetaField copy() {
        return new TemplateIdMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, options, selectedOption);
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        TemplateIdMetaField listMetaField = (TemplateIdMetaField) metaField;
        return new TemplateIdMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon,
                listMetaField.options, selectedOption);
    }

    public MetaField copy(String selectedOption) {
        return new TemplateIdMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, options, selectedOption);
    }
}
