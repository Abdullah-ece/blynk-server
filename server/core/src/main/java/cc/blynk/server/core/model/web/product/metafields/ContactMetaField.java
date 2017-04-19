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

    public String lastName;

    public String email;

    public String phone;

    public String streetAddress;

    public String city;

    public String state;

    public String zip;

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
