package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class CoordinatesMetaField extends MetaField {

    public final double lat;

    public final double lon;

    @JsonCreator
    public CoordinatesMetaField(@JsonProperty("id") int id,
                                @JsonProperty("name") String name,
                                @JsonProperty("roleIds") int[] roleIds,
                                @JsonProperty("includeInProvision") boolean includeInProvision,
                                @JsonProperty("isMandatory") boolean isMandatory,
                                @JsonProperty("isDefault") boolean isDefault,
                                @JsonProperty("icon") String icon,
                                @JsonProperty("lat") double lat,
                                @JsonProperty("lon") double lon) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new CoordinatesMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault, icon, lat, lon);
    }

    @Override
    public MetaField copy() {
        return new CoordinatesMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, lat, lon);
    }
}
