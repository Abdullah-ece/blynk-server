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
    public CoordinatesMetaField(@JsonProperty("name") String name,
                                @JsonProperty("role") Role role,
                                @JsonProperty("lat") double lat,
                                @JsonProperty("lon") double lon) {
        super(name, role);
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public MetaField copy() {
        return new CoordinatesMetaField(name, role, lat, lon);
    }
}
