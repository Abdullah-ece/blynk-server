package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;

public class AddressMetaField extends MetaField {

    public String streetAddress;

    public boolean isStreetAddressEnabled;

    public String city;

    public boolean isCityEnabled;

    public String state;

    public boolean isStateEnabled;

    public String zip;

    public boolean isZipEnabled;

    public String country;

    public boolean isCountryEnabled;

    public AddressMetaField() {
    }

    public AddressMetaField(String name, Role role, String streetAddress, String city, String state, String zip, String country) {
        super(name, role);
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }
}
