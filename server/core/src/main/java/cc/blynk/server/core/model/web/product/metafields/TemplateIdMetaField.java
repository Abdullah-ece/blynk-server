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
public class TemplateIdMetaField extends ListMetaField {

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
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon, options, selectedOption);
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
}
