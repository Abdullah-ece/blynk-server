package cc.blynk.server.web.handlers.logic.organization.dto;

import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationDTO {

    public final int id;

    public final String siteName;

    public final int deviceId;

    @JsonCreator
    public LocationDTO(@JsonProperty("id") int id,
                       @JsonProperty("siteName") String siteName,
                       @JsonProperty("deviceId") int deviceId) {
        this.id = id;
        this.siteName = siteName;
        this.deviceId = deviceId;
    }

    public LocationDTO(LocationMetaField locationMetaField, int deviceId) {
        this(locationMetaField.id, locationMetaField.siteName, deviceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocationDTO that = (LocationDTO) o;

        return siteName != null ? siteName.equals(that.siteName) : that.siteName == null;
    }

    @Override
    public int hashCode() {
        return siteName != null ? siteName.hashCode() : 0;
    }
}
