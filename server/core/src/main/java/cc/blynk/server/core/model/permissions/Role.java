package cc.blynk.server.core.model.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.core.model.permissions.PermissionsTable.SUB_ORG_VIEW;

public class Role {

    //special ID for role that indicates it is super admin role
    public static final int SUPER_ADMIN_ROLE_ID = 0;

    public final int id;

    public final String name;

    public final long permissions;

    @JsonCreator
    public Role(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("permissions") long permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public boolean isSuperAdmin() {
        return id == SUPER_ADMIN_ROLE_ID;
    }

    public boolean hasSubOrgAccess() {
        return hasPermission(permissions, SUB_ORG_VIEW);
    }

    public static boolean hasPermission(long permissions, long mask) {
        return (permissions & mask) == mask;
    }

}
