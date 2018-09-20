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
public class CoordinatesMetaField extends MetaField {

    public final double lat;

    public final double lon;

    @JsonCreator
    public CoordinatesMetaField(@JsonProperty("id") int id,
                                @JsonProperty("name") String name,
                                @JsonProperty("role") Role role,
                                @JsonProperty("includeInProvision") boolean includeInProvision,
                                @JsonProperty("isMandatory") boolean isMandatory,
                                @JsonProperty("isDefault") boolean isDefault,
                                @JsonProperty("icon") String icon,
                                @JsonProperty("lat") double lat,
                                @JsonProperty("lon") double lon) {
        super(id, name, role, includeInProvision, isMandatory, isDefault, icon);
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new CoordinatesMetaField(id, metaField.name, metaField.role,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault, icon, lat, lon);
    }

    @Override
    public MetaField copy() {
        return new CoordinatesMetaField(id, name, role,
                includeInProvision, isMandatory, isDefault,
                icon, lat, lon);
    }
}
