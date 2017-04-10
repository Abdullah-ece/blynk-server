package cc.blynk.server.core.model.web;

import cc.blynk.utils.validators.BlynkEmailValidator;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.04.17.
 */
public class UserInvite {

    public String email;

    public String name;

    public Role role;

    public UserInvite() {
    }

    public UserInvite(String email, String name,  Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public boolean isNotValid() {
        return email == null || email.isEmpty() || role == null || role == Role.SUPER_ADMIN || BlynkEmailValidator.isNotValidEmail(email);
    }
}
