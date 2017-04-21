package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class CoordinatesMetaField extends MetaField {

    public double lat;

    public double lon;

    public CoordinatesMetaField() {
    }

    public CoordinatesMetaField(String name, Role role, double lat, double lon) {
        super(name, role);
        this.lat = lat;
        this.lon = lon;
    }
}
