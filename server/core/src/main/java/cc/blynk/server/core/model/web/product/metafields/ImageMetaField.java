package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ImageMetaField extends MetaField {

    public final String url;

    @JsonCreator
    public ImageMetaField(@JsonProperty("id") int id,
                          @JsonProperty("name") String name,
                          @JsonProperty("roleIds") int[] roleIds,
                          @JsonProperty("includeInProvision") boolean includeInProvision,
                          @JsonProperty("isMandatory") boolean isMandatory,
                          @JsonProperty("isDefault") boolean isDefault,
                          @JsonProperty("icon") String icon,
                          @JsonProperty("value") String url) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.url = url;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new ImageMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon, url);
    }

    @Override
    public MetaField copy() {
        return new ImageMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, url);
    }

}
