package cc.blynk.server.core.model.permissions;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_SWITCH;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;

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

    //org group
    public boolean canSwitchOrg() {
        return hasPermission1(ORG_SWITCH);
    }

    public boolean canViewOrg() {
        return hasPermission1(ORG_VIEW);
    }

    //org devices group
    public boolean canViewOrgDevices() {
        return hasPermission1(ORG_DEVICES_VIEW);
    }

    public boolean canEditOrgDevice() {
        return hasPermission1(ORG_DEVICES_EDIT);
    }

    public boolean canDeleteOrgDevice() {
        return hasPermission1(ORG_DEVICES_DELETE);
    }

    //own devices group
    public boolean canViewOwnDevices() {
        return hasPermission1(OWN_DEVICES_VIEW);
    }

    public boolean hasPermission1(int permission) {
        return hasPermission(permissionGroup1, permission);
    }

    public boolean hasPermission2(int permission) {
        return hasPermission(permissionGroup2, permission);
    }

    public Role copy(int id) {
        return new Role(id, name, permissionGroup1, permissionGroup2);
    }

    @Override
    public Role copy() {
        return new Role(id, name, permissionGroup1, permissionGroup2);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
