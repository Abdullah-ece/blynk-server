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

    public final long selectedDeviceId;

    @JsonCreator
    public DeviceReferenceMetaField(@JsonProperty("id") int id,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("role") Role role,
                                    @JsonProperty("isDefault") boolean isDefault,
                                    @JsonProperty("icon") String icon,
                                    @JsonProperty("selectedProductIds") int[] selectedProductIds,
                                    @JsonProperty("selectedDeviceId") long selectedDeviceId) {
        super(id, name, role, isDefault, icon);
        this.selectedProductIds = selectedProductIds;
        this.selectedDeviceId = selectedDeviceId;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        DeviceReferenceMetaField deviceReferenceMetaField = (DeviceReferenceMetaField) metaField;
        return new DeviceReferenceMetaField(id, metaField.name, metaField.role, metaField.isDefault, icon,
                deviceReferenceMetaField.selectedProductIds, selectedDeviceId);
    }

    @Override
    public MetaField copy() {
        return new DeviceReferenceMetaField(id, name, role, isDefault, icon, selectedProductIds, selectedDeviceId);
    }
}
