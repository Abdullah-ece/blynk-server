package cc.blynk.server.core.model.permissions;

import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.SUB_ORG_VIEW;

public class Role implements CopyObject<Role> {

    //special ID for role that indicates it is super admin role
    public static final int SUPER_ADMIN_ROLE_ID = 0;

    public final int id;

    public final String name;

    public final int permissionGroup1;

    public final int permissionGroup2;

    @JsonCreator
    public Role(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("permissionGroup1") int permissionGroup1,
                @JsonProperty("permissionGroup2") int permissionGroup2) {
        this.id = id;
        this.name = name;
        this.permissionGroup1 = permissionGroup1;
        this.permissionGroup2 = permissionGroup2;
    }

    public boolean isSuperAdmin() {
        return id == SUPER_ADMIN_ROLE_ID;
    }

    private static boolean hasPermission(int permissionGroup, int permission) {
        return (permissionGroup & permission) == permission;
    }

    public boolean canCreateRole() {
        return hasPermission(permissionGroup1, ROLE_CREATE);
    }

    public boolean hasSubOrgAccess() {
        return hasPermission(permissionGroup1, SUB_ORG_VIEW);
    }

    @Override
    public Role copy() {
        return new Role(id, name, permissionGroup1, permissionGroup2);
    }
}
