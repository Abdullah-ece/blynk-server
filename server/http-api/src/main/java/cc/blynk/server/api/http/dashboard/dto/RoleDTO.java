package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleDTO {

    public final int id;

    public final String name;

    public final int permissions1;

    public final int permissions2;

    @JsonCreator
    public RoleDTO(@JsonProperty("id") int id,
                   @JsonProperty("name") String name,
                   @JsonProperty("permissions1") int permissions1,
                   @JsonProperty("permissions2") int permissions2) {
        this.id = id;
        this.name = name;
        this.permissions1 = permissions1;
        this.permissions2 = permissions2;
    }

    public Role toRole() {
        return new Role(id, name, permissions1, permissions2);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
