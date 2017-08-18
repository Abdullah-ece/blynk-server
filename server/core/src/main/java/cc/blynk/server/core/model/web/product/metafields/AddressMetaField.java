package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressMetaField extends MetaField {

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

    public final boolean isDefaultsEnabled;

    @JsonCreator
    public AddressMetaField(@JsonProperty("id") int id,
                            @JsonProperty("name") String name,
                            @JsonProperty("role") Role role,
                            @JsonProperty("isDefault") boolean isDefault,
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
                            @JsonProperty("isDefaultsEnabled") boolean isDefaultsEnabled) {
        super(id, name, role, isDefault);
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
        this.isDefaultsEnabled = isDefaultsEnabled;
    }

    @Override
    public MetaField copy() {
        return new AddressMetaField(
                id, name, role, isDefault,
                streetAddress, isStreetAddressEnabled,
                city, isCityEnabled,
                state, isStateEnabled,
                zip, isZipEnabled,
                country, isCountryEnabled,
                isDefaultsEnabled
        );
    }
}
