package cc.blynk.server.core.model.web;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.utils.validators.BlynkEmailValidator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.04.17.
 */
public class UserInviteDTO {

    public String email;

    public final String name;

    public final int roleId;

    @JsonCreator
    public UserInviteDTO(@JsonProperty("email") String email,
                         @JsonProperty("name") String name,
                         @JsonProperty("roleIds") int roleId) {
        this.email = (email == null ? null : email.trim().toLowerCase());
        this.name = name;
        this.roleId = roleId;
    }

    public boolean isNotValid() {
        return email == null || email.isEmpty() || email.trim().isEmpty()
                || roleId <= 0 || (name != null && name.length() > 255)
                || BlynkEmailValidator.isNotValidEmail(email);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
