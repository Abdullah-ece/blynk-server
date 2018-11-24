package cc.blynk.server.core.model.auth;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.11.18.
 */
public class AccountDTO {

    public final String name;

    public final int roleId;

    @JsonCreator
    public AccountDTO(@JsonProperty("name") String name,
                      @JsonProperty("roleId") int roleId) {
        this.name = name;
        this.roleId = roleId;
    }

    public boolean isNotValid() {
        return name == null || name.isEmpty() || name.trim().isEmpty()
                || roleId < 0;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
