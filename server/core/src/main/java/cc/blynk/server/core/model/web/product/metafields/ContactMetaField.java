package cc.blynk.server.core.model.web.product.metafields;

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

    public volatile boolean isFirstNameEnabled;

    public final String lastName;

    public volatile boolean isLastNameEnabled;

    public final String email;

    public volatile boolean isEmailEnabled;

    public final String phone;

    public volatile boolean isPhoneEnabled;

    public final String streetAddress;

    public volatile boolean isStreetAddressEnabled;

    public final String country;

    public volatile boolean isCountryEnabled;

    public final String city;

    public volatile boolean isCityEnabled;

    public final String state;

    public volatile boolean isStateEnabled;

    public final String zip;

    public volatile boolean isZipEnabled;

    public volatile boolean isDefaultsEnabled;

    @JsonCreator
    public ContactMetaField(@JsonProperty("id") int id,
                            @JsonProperty("name") String name,
                            @JsonProperty("roleId") int roleId,
                            @JsonProperty("isDefault") boolean isDefault,
                            @JsonProperty("icon") String icon,
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
                            @JsonProperty("country") String country,
                            @JsonProperty("isCountryEnabled") boolean isCountryEnabled,
                            @JsonProperty("city") String city,
                            @JsonProperty("isCityEnabled") boolean isCityEnabled,
                            @JsonProperty("state") String state,
                            @JsonProperty("isStateEnabled") boolean isStateEnabled,
                            @JsonProperty("zip") String zip,
                            @JsonProperty("isZipEnabled") boolean isZipEnabled,
                            @JsonProperty("isDefaultsEnabled") boolean isDefaultsEnabled) {
        super(id, name, roleId, isDefault, icon);
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
        this.country = country;
        this.isCountryEnabled = isCountryEnabled;
        this.city = city;
        this.isCityEnabled = isCityEnabled;
        this.state = state;
        this.isStateEnabled = isStateEnabled;
        this.zip = zip;
        this.isZipEnabled = isZipEnabled;
        this.isDefaultsEnabled = isDefaultsEnabled;
    }

    @Override
    public String getNotificationEmail() {
        return email;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        ContactMetaField contactMetaField = (ContactMetaField) metaField;
        return new ContactMetaField(id, metaField.name, metaField.roleId, metaField.isDefault, icon,
                contact,
                firstName, contactMetaField.isFirstNameEnabled,
                lastName, contactMetaField.isLastNameEnabled,
                email, contactMetaField.isEmailEnabled,
                phone, contactMetaField.isPhoneEnabled,
                streetAddress, contactMetaField.isStreetAddressEnabled,
                country, contactMetaField.isCountryEnabled,
                city, contactMetaField.isCityEnabled,
                state, contactMetaField.isStateEnabled,
                zip, contactMetaField.isZipEnabled,
                contactMetaField.isDefaultsEnabled);
    }

    @Override
    public MetaField copy() {
        return new ContactMetaField(id, name, roleId, isDefault, icon,
                contact,
                firstName, isFirstNameEnabled,
                lastName, isLastNameEnabled,
                email, isEmailEnabled,
                phone, isPhoneEnabled,
                streetAddress, isStreetAddressEnabled,
                country, isCountryEnabled,
                city, isCityEnabled,
                state, isStateEnabled,
                zip, isZipEnabled,
                isDefaultsEnabled);
    }
}
