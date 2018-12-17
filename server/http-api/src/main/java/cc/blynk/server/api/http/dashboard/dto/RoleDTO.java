package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class RoleDTO {

    public final int id;

    public final String name;

    public final int permissionGroup1;

    public final int permissionGroup2;

    @JsonCreator
    public RoleDTO(@JsonProperty("id") int id,
                   @JsonProperty("name") String name,
                   @JsonProperty("permissionGroup1") int permissionGroup1,
                   @JsonProperty("permissionGroup2") int permissionGroup2) {
        this.id = id;
        this.name = name;
        this.permissionGroup1 = permissionGroup1;
        this.permissionGroup2 = permissionGroup2;
    }

    public Role toRole() {
        return new Role(id, name, permissionGroup1, permissionGroup2);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
