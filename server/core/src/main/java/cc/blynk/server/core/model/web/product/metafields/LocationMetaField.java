package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationMetaField extends MetaField {

    public final String siteName;

    public final boolean isLocationEnabled;

    public final String streetAddress;

    public final boolean isStreetAddressEnabled;

    public final String city;

    public final boolean isCityEnabled;

    public final String state;

    public final boolean isStateEnabled;

    public final String zip;

    public final boolean isZipEnabled;

    public final String country;

    public final boolean isCountryEnabled;

    public final boolean isCoordinatesEnabled;

    public final double lat;

    public final double lon;

    public final boolean isBuildingNameEnabled;

    public final String buildingName;

    public final boolean isFloorEnabled;

    public final int floor;

    public final boolean isUnitEnabled;

    public final String unit;

    public final boolean isRoomEnabled;

    public final String room;

    public final boolean isZoneEnabled;

    public final String zone;

    public final boolean useLocationDataFromDevice;

    public final boolean isDefaultsEnabled;

    public final String placeId;

    @JsonCreator
    public LocationMetaField(@JsonProperty("id") int id,
                             @JsonProperty("name") String name,
                             @JsonProperty("role") Role role,
                             @JsonProperty("isDefault") boolean isDefault,
                             @JsonProperty("icon") String icon,
                             @JsonProperty("siteName") String siteName,
                             @JsonProperty("isLocationEnabled") boolean isLocationEnabled,
                             @JsonProperty("streetAddress") String streetAddress,
                             @JsonProperty("isStreetAddressEnabled") boolean isStreetAddressEnabled,
                             @JsonProperty("city") String city,
                             @JsonProperty("isCityEnabled") boolean isCityEnabled,
                             @JsonProperty("state") String state,
                             @JsonProperty("isStateEnabled") boolean isStateEnabled,
                             @JsonProperty("zip") String zip,
                             @JsonProperty("isZipEnabled") boolean isZipEnabled,
                             @JsonProperty("country") String country,
                             @JsonProperty("isCountryEnabled") boolean isCountryEnabled,
                             @JsonProperty("isCoordinatesEnabled") boolean isCoordinatesEnabled,
                             @JsonProperty("lat") double lat,
                             @JsonProperty("lon") double lon,
                             @JsonProperty("isBuildingNameEnabled") boolean isBuildingNameEnabled,
                             @JsonProperty("buildingName") String buildingName,
                             @JsonProperty("isFloorEnabled") boolean isFloorEnabled,
                             @JsonProperty("floor") int floor,
                             @JsonProperty("isUnitEnabled") boolean isUnitEnabled,
                             @JsonProperty("unit") String unit,
                             @JsonProperty("isRoomEnabled") boolean isRoomEnabled,
                             @JsonProperty("room") String room,
                             @JsonProperty("isZoneEnabled") boolean isZoneEnabled,
                             @JsonProperty("zone") String zone,
                             @JsonProperty("useLocationDataFromDevice") boolean useLocationDataFromDevice,
                             @JsonProperty("isDefaultsEnabled") boolean isDefaultsEnabled,
                             @JsonProperty("placeId") String placeId) {
        super(id, name, role, isDefault, icon);
        this.siteName = siteName;
        this.isLocationEnabled = isLocationEnabled;
        this.streetAddress = streetAddress;
        this.isStreetAddressEnabled = isStreetAddressEnabled;
        this.city = city;
        this.isCityEnabled = isCityEnabled;
        this.state = state;
        this.isStateEnabled = isStateEnabled;
        this.zip = zip;
        this.isZipEnabled = isZipEnabled;
        this.country = country;
        this.isCountryEnabled = isCountryEnabled;
        this.isCoordinatesEnabled = isCoordinatesEnabled;
        this.lat = lat;
        this.lon = lon;
        this.isBuildingNameEnabled = isBuildingNameEnabled;
        this.buildingName = buildingName;
        this.isFloorEnabled = isFloorEnabled;
        this.floor = floor;
        this.isUnitEnabled = isUnitEnabled;
        this.unit = unit;
        this.isRoomEnabled = isRoomEnabled;
        this.room = room;
        this.isZoneEnabled = isZoneEnabled;
        this.zone = zone;
        this.useLocationDataFromDevice = useLocationDataFromDevice;
        this.isDefaultsEnabled = isDefaultsEnabled;
        this.placeId = placeId;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        LocationMetaField addressMetaField = (LocationMetaField) metaField;
        return new LocationMetaField(
                id, metaField.name, metaField.role, metaField.isDefault,
                metaField.icon, addressMetaField.siteName, addressMetaField.isLocationEnabled,
                streetAddress, addressMetaField.isStreetAddressEnabled,
                city, addressMetaField.isCityEnabled,
                state, addressMetaField.isStateEnabled,
                zip, addressMetaField.isZipEnabled,
                country, addressMetaField.isCountryEnabled,
                addressMetaField.isCoordinatesEnabled, lat, lon,
                addressMetaField.isBuildingNameEnabled, buildingName,
                addressMetaField.isFloorEnabled, floor,
                addressMetaField.isUnitEnabled, unit,
                addressMetaField.isRoomEnabled, room,
                addressMetaField.isZoneEnabled, zone,
                addressMetaField.useLocationDataFromDevice,
                addressMetaField.isDefaultsEnabled,
                placeId
        );
    }

    @Override
    public MetaField copy() {
        return new LocationMetaField(
                id, name, role, isDefault,
                icon, siteName, isLocationEnabled,
                streetAddress, isStreetAddressEnabled,
                city, isCityEnabled,
                state, isStateEnabled,
                zip, isZipEnabled,
                country, isCountryEnabled,
                isCoordinatesEnabled, lat, lon,
                isBuildingNameEnabled, buildingName,
                isFloorEnabled, floor,
                isUnitEnabled, unit,
                isRoomEnabled, room,
                isZoneEnabled, zone,
                useLocationDataFromDevice,
                isDefaultsEnabled,
                placeId
        );
    }
}
