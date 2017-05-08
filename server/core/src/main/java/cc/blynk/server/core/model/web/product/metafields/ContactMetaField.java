package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ContactMetaField extends MetaField {

    public String contact;

    public String firstName;

    public boolean isFirstNameEnabled;

    public String lastName;

    public boolean isLastNameEnabled;

    public String email;

    public boolean isEmailEnabled;

    public String phone;

    public boolean isPhoneEnabled;

    public String streetAddress;

    public boolean isStreetAddressEnabled;

    public String city;

    public boolean isCityEnabled;

    public String state;

    public boolean isStateEnabled;

    public String zip;

    public boolean isZipEnabled;

    public boolean isDefaultsEnabled;

    public ContactMetaField() {
    }

    public ContactMetaField(String name, Role role, String contact, String firstName, String lastName, String email, String phone, String streetAddress, String city, String state, String zip) {
        super(name, role);
        this.contact = contact;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }
}
