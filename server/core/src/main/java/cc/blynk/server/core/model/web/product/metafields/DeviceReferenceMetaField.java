package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.utils.IntArray;
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
                                    @JsonProperty("roleIds") int[] roleIds,
                                    @JsonProperty("includeInProvision") boolean includeInProvision,
                                    @JsonProperty("isMandatory") boolean isMandatory,
                                    @JsonProperty("isDefault") boolean isDefault,
                                    @JsonProperty("icon") String icon,
                                    @JsonProperty("selectedProductIds") int[] selectedProductIds,
                                    @JsonProperty("selectedDeviceId") long selectedDeviceId) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.selectedProductIds = selectedProductIds == null ? IntArray.EMPTY_INTS : selectedProductIds;
        this.selectedDeviceId = selectedDeviceId;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        DeviceReferenceMetaField deviceReferenceMetaField = (DeviceReferenceMetaField) metaField;
        return new DeviceReferenceMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon,
                deviceReferenceMetaField.selectedProductIds, selectedDeviceId);
    }

    @Override
    public MetaField copy() {
        return new DeviceReferenceMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, selectedProductIds, selectedDeviceId);
    }
}
