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
public class ContactMetaField extends MetaField {

    public final String contact;

    public final String firstName;

    public final boolean isFirstNameEnabled;

    public final String lastName;

    public final boolean isLastNameEnabled;

    public final String email;

    public final boolean isEmailEnabled;

    public final String phone;

    public final boolean isPhoneEnabled;

    public final String streetAddress;

    public final boolean isStreetAddressEnabled;

    public final String city;

    public final boolean isCityEnabled;

    public final String state;

    public final boolean isStateEnabled;

    public final String zip;

    public final boolean isZipEnabled;

    public final boolean isDefaultsEnabled;

    @JsonCreator
    public ContactMetaField(@JsonProperty("name") String name,
                            @JsonProperty("role") Role role,
                            @JsonProperty("contact") String contact,
                            @JsonProperty("firstName") String firstName,
                            @JsonProperty("isFirstNameEnabled") boolean isFirstNameEnabled,
                            @JsonProperty("lastName") String lastName,
                            @JsonProperty("isLastNameEnabled") boolean isLastNameEnabled,
                            @JsonProperty("email") String email,
                            @JsonProperty("isEmailEnabled") boolean isEmailEnabled,
                            @JsonProperty("phone") String phone,
                            @JsonProperty("isPhoneEnabled") boolean isPhoneEnabled,
                            @JsonProperty("streetAddress") String streetAddress,
                            @JsonProperty("isStreetAddressEnabled") boolean isStreetAddressEnabled,
                            @JsonProperty("city") String city,
                            @JsonProperty("isCityEnabled") boolean isCityEnabled,
                            @JsonProperty("state") String state,
                            @JsonProperty("isStateEnabled") boolean isStateEnabled,
                            @JsonProperty("zip") String zip,
                            @JsonProperty("isZipEnabled") boolean isZipEnabled,
                            @JsonProperty("isDefaultsEnabled") boolean isDefaultsEnabled) {
        super(name, role);
        this.contact = contact;
        this.firstName = firstName;
        this.isFirstNameEnabled = isFirstNameEnabled;
        this.lastName = lastName;
        this.isLastNameEnabled = isLastNameEnabled;
        this.email = email;
        this.isEmailEnabled = isEmailEnabled;
        this.phone = phone;
        this.isPhoneEnabled = isPhoneEnabled;
        this.streetAddress = streetAddress;
        this.isStreetAddressEnabled = isStreetAddressEnabled;
        this.city = city;
        this.isCityEnabled = isCityEnabled;
        this.state = state;
        this.isStateEnabled = isStateEnabled;
        this.zip = zip;
        this.isZipEnabled = isZipEnabled;
        this.isDefaultsEnabled = isDefaultsEnabled;
    }

    @Override
    public MetaField copy() {
        return new ContactMetaField(name, role, contact,
                firstName, isFirstNameEnabled,
                lastName, isLastNameEnabled,
                email, isEmailEnabled,
                phone, isPhoneEnabled,
                streetAddress, isStreetAddressEnabled,
                city, isCityEnabled,
                state, isStateEnabled,
                zip, isZipEnabled,
                isDefaultsEnabled);
    }
}
