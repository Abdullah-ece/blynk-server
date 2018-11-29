package cc.blynk.server.core.model.permissions;

import java.util.HashMap;
import java.util.Map;

public abstract class PermissionsTable {

    public static int ORG_CREATE =             0b1;
    public static int ORG_VIEW =               0b10;
    public static int ORG_EDIT =               0b100;
    public static int ORG_DELETE =             0b1000;

    public static int SUB_ORG_CREATE =         0b10000;
    public static int SUB_ORG_VIEW =           0b100000;
    public static int SUB_ORG_EDIT =           0b1000000;
    public static int SUB_ORG_DELETE =         0b10000000;

    public static int ORG_INVITE_USERS =       0b100000000;
    public static int ORG_VIEW_USERS =         0b1000000000;
    public static int ORG_EDIT_USERS =         0b10000000000;
    public static int ORG_DELETE_USERS =       0b100000000000;

    public static int PRODUCT_CREATE =         0b1000000000000;
    public static int PRODUCT_VIEW =           0b10000000000000;
    public static int PRODUCT_EDIT =           0b100000000000000;
    public static int PRODUCT_DELETE =         0b1000000000000000;

    public static int ROLE_CREATE =            0b10000000000000000;
    public static int ROLE_VIEW =              0b100000000000000000;
    public static int ROLE_EDIT =              0b1000000000000000000;
    public static int ROLE_DELETE =            0b10000000000000000000;

    public static int ORG_DEVICES_CREATE =     0b100000000000000000000;
    public static int ORG_DEVICES_VIEW =       0b1000000000000000000000;
    public static int ORG_DEVICES_EDIT =       0b10000000000000000000000;
    public static int ORG_DEVICES_DELETE =     0b100000000000000000000000;
    public static int ORG_DEVICES_SHARE =      0b1000000000000000000000000;

    public static int OWN_DEVICES_CREATE =     0b10000000000000000000000000;
    public static int OWN_DEVICES_VIEW =       0b100000000000000000000000000;
    public static int OWN_DEVICES_EDIT =       0b1000000000000000000000000000;
    public static int OWN_DEVICES_DELETE =     0b10000000000000000000000000000;
    public static int OWN_DEVICES_SHARE =      0b100000000000000000000000000000;

    public static int SET_AUTH_TOKEN =         0b1000000000000000000000000000000;

    public static Map<Integer, String> PERMISSION_NAMES = new HashMap<>() {
        {
            put(ORG_CREATE, "create organization");
            put(ORG_VIEW, "view organization");
            put(ORG_EDIT, "edit organization");
            put(ORG_DELETE, "delete organization");
            put(SUB_ORG_CREATE, "create sub organization");
            put(SUB_ORG_VIEW, "view sub organization");
            put(SUB_ORG_EDIT, "edit sub organization");
            put(SUB_ORG_DELETE, "delete sub organization");
            put(ORG_INVITE_USERS, "invite user");
            put(ORG_VIEW_USERS, "view user");
            put(ORG_EDIT_USERS, "edit user");
            put(ORG_DELETE_USERS, "delete user");
            put(PRODUCT_CREATE, "create product");
            put(PRODUCT_VIEW, "view product");
            put(PRODUCT_EDIT, "edit product");
            put(PRODUCT_DELETE, "delete product");
            put(ROLE_CREATE, "create role");
            put(ROLE_VIEW, "view role");
            put(ROLE_EDIT, "edit role");
            put(ROLE_DELETE, "delete role");
            put(ORG_DEVICES_CREATE, "create org devices");
            put(ORG_DEVICES_VIEW, "view org devices");
            put(ORG_DEVICES_EDIT, "edit org devices");
            put(ORG_DEVICES_DELETE, "delete org devices");
            put(ORG_DEVICES_SHARE, "share org devices");
            put(OWN_DEVICES_CREATE, "create own devices");
            put(OWN_DEVICES_VIEW, "view own devices");
            put(OWN_DEVICES_EDIT, "edit own devices");
            put(OWN_DEVICES_DELETE, "delete own devices");
            put(OWN_DEVICES_SHARE, "share own devices");
            put(SET_AUTH_TOKEN, "set auth token");
        }
    };

}
