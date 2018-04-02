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
public class DeviceReferenceMetaField extends MetaField {

    public final int[] selectedProductIds;

    @JsonCreator
    public DeviceReferenceMetaField(@JsonProperty("id") int id,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("role") Role role,
                                    @JsonProperty("isDefault") boolean isDefault,
                                    @JsonProperty("selectedProductIds") int[] selectedProductIds) {
        super(id, name, role, isDefault);
        this.selectedProductIds = selectedProductIds;
    }

    @Override
    public MetaField copy() {
        return new DeviceReferenceMetaField(id, name, role, isDefault, selectedProductIds);
    }
}
