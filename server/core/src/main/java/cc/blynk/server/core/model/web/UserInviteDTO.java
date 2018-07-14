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

    public final Role role;

    @JsonCreator
    public UserInviteDTO(@JsonProperty("email") String email,
                         @JsonProperty("name") String name,
                         @JsonProperty("role") Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public boolean isNotValid() {
        return email == null || email.isEmpty() || role == null
                || role == Role.SUPER_ADMIN || BlynkEmailValidator.isNotValidEmail(email);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
