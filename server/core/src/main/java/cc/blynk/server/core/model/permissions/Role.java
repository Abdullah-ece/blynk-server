package cc.blynk.server.core.model.permissions;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DELETE_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_SHARE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_DEVICES_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_EDIT_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_INVITE_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ORG_VIEW_USERS;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_SHARE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.OWN_DEVICES_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.PRODUCT_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_EDIT;
import static cc.blynk.server.core.model.permissions.PermissionsTable.ROLE_VIEW;
import static cc.blynk.server.core.model.permissions.PermissionsTable.SUB_ORG_CREATE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.SUB_ORG_DELETE;
import static cc.blynk.server.core.model.permissions.PermissionsTable.SUB_ORG_EDIT;
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

    //org group
    public boolean canViewOrg() {
        return hasPermission1(ORG_VIEW);
    }

    public boolean canCreateOrg() {
        return hasPermission1(ORG_CREATE);
    }

    public boolean canEditOrg() {
        return hasPermission1(ORG_EDIT);
    }

    public boolean canDeleteOrg() {
        return hasPermission1(ORG_DELETE);
    }

    //suborg group
    public boolean canViewSubOrg() {
        return hasPermission1(SUB_ORG_VIEW);
    }

    public boolean canCreateSubOrg() {
        return hasPermission1(SUB_ORG_CREATE);
    }

    public boolean canEditSubOrg() {
        return hasPermission1(SUB_ORG_EDIT);
    }

    public boolean canDeleteSubOrg() {
        return hasPermission1(SUB_ORG_DELETE);
    }

    //user group
    public boolean canViewOrgUsers() {
        return hasPermission1(ORG_VIEW_USERS);
    }

    public boolean canInviteOrgUsers() {
        return hasPermission1(ORG_INVITE_USERS);
    }

    public boolean canEditOrgUsers() {
        return hasPermission1(ORG_EDIT_USERS);
    }

    public boolean canDeleteOrgUsers() {
        return hasPermission1(ORG_DELETE_USERS);
    }

    //product group
    public boolean canViewProduct() {
        return hasPermission1(PRODUCT_VIEW);
    }

    public boolean canCreateProduct() {
        return hasPermission1(PRODUCT_CREATE);
    }

    public boolean canEditProduct() {
        return hasPermission1(PRODUCT_EDIT);
    }

    public boolean canDeleteProduct() {
        return hasPermission1(PRODUCT_DELETE);
    }

    //role group
    public boolean canViewRole() {
        return hasPermission1(ROLE_VIEW);
    }

    public boolean canCreateRole() {
        return hasPermission1(ROLE_CREATE);
    }

    public boolean canEditRole() {
        return hasPermission1(ROLE_EDIT);
    }

    public boolean canDeleteRole() {
        return hasPermission1(ROLE_DELETE);
    }

    //org devices group
    public boolean canViewOrgDevices() {
        return hasPermission1(ORG_DEVICES_VIEW);
    }

    public boolean canCreateOrgDevice() {
        return hasPermission1(ORG_DEVICES_CREATE);
    }

    public boolean canEditOrgDevice() {
        return hasPermission1(ORG_DEVICES_EDIT);
    }

    public boolean canDeleteOrgDevice() {
        return hasPermission1(ORG_DEVICES_DELETE);
    }

    public boolean canShareOrgDevice() {
        return hasPermission1(ORG_DEVICES_SHARE);
    }

    //own devices group
    public boolean canViewOwnDevices() {
        return hasPermission1(OWN_DEVICES_VIEW);
    }

    public boolean canCreateOwnDevice() {
        return hasPermission1(OWN_DEVICES_CREATE);
    }

    public boolean canEditOwnDevice() {
        return hasPermission1(OWN_DEVICES_EDIT);
    }

    public boolean canDeleteOwnDevice() {
        return hasPermission1(OWN_DEVICES_DELETE);
    }

    public boolean canShareOwnDevice() {
        return hasPermission1(OWN_DEVICES_SHARE);
    }

    /**
     * Permission check for Group1
     */
    private boolean hasPermission1(int permission) {
        return hasPermission(permissionGroup1, permission);
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
