package cc.blynk.server.core.model.permissions;

import java.util.Map;

import static java.util.Map.entry;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.04.18.
 */
public final class PermissionsTable {

    public static final int ORG_SWITCH =             0b1;
    public static final int OWN_ORG_EDIT =           0b10;
    public static final int OTA_VIEW =               0b100;
    public static final int OTA_START =              0b1000;
    public static final int OTA_STOP =               0b10000;
    public static final int ORG_CREATE =             0b100000;
    public static final int ORG_VIEW =               0b1000000;
    public static final int ORG_EDIT =               0b10000000;
    public static final int ORG_DELETE =             0b100000000;
    public static final int ORG_INVITE_USERS =       0b1000000000;
    public static final int ORG_VIEW_USERS =         0b10000000000;
    public static final int ORG_EDIT_USERS =         0b100000000000;
    public static final int ORG_DELETE_USERS =       0b1000000000000;
    public static final int PRODUCT_CREATE =         0b10000000000000;
    public static final int PRODUCT_VIEW =           0b100000000000000;
    public static final int PRODUCT_EDIT =           0b1000000000000000;
    public static final int PRODUCT_DELETE =         0b10000000000000000;
    public static final int ROLE_CREATE =            0b100000000000000000;
    public static final int ROLE_VIEW =              0b1000000000000000000;
    public static final int ROLE_EDIT =              0b10000000000000000000;
    public static final int ROLE_DELETE =            0b100000000000000000000;
    public static final int ORG_DEVICES_CREATE =     0b1000000000000000000000;
    public static final int ORG_DEVICES_VIEW =       0b10000000000000000000000;
    public static final int ORG_DEVICES_EDIT =       0b100000000000000000000000;
    public static final int ORG_DEVICES_DELETE =     0b1000000000000000000000000;
    public static final int ORG_DEVICES_SHARE =      0b10000000000000000000000000;
    public static final int OWN_DEVICES_CREATE =     0b100000000000000000000000000;
    public static final int OWN_DEVICES_VIEW =       0b1000000000000000000000000000;
    public static final int OWN_DEVICES_EDIT =       0b10000000000000000000000000000;
    public static final int OWN_DEVICES_DELETE =     0b100000000000000000000000000000;
    public static final int OWN_DEVICES_SHARE =      0b1000000000000000000000000000000;
    public static final int SET_AUTH_TOKEN =         0b10000000000000000000000000000000;

    //permission2 group
    public static final int RULE_GROUP_VIEW =        0b1;
    public static final int RULE_GROUP_EDIT =        0b10;

    public static final Map<Integer, String> PERMISSION1_NAMES = Map.ofEntries(
            entry(ORG_SWITCH, "switch organization"),
            entry(OWN_ORG_EDIT, "edit own organization"),
            entry(OTA_VIEW, "view ota"),
            entry(OTA_START, "start ota"),
            entry(OTA_STOP, "stop ota"),
            entry(ORG_CREATE, "create organization"),
            entry(ORG_VIEW, "view organization"),
            entry(ORG_EDIT, "edit organization"),
            entry(ORG_DELETE, "delete organization"),
            entry(ORG_INVITE_USERS, "invite user"),
            entry(ORG_VIEW_USERS, "view user"),
            entry(ORG_EDIT_USERS, "edit user"),
            entry(ORG_DELETE_USERS, "delete user"),
            entry(PRODUCT_CREATE, "create product"),
            entry(PRODUCT_VIEW, "view product"),
            entry(PRODUCT_EDIT, "edit product"),
            entry(PRODUCT_DELETE, "delete product"),
            entry(ROLE_CREATE, "create role"),
            entry(ROLE_VIEW, "view role"),
            entry(ROLE_EDIT, "edit role"),
            entry(ROLE_DELETE, "delete role"),
            entry(ORG_DEVICES_CREATE, "create org devices"),
            entry(ORG_DEVICES_VIEW, "view org devices"),
            entry(ORG_DEVICES_EDIT, "edit org devices"),
            entry(ORG_DEVICES_DELETE, "delete org devices"),
            entry(ORG_DEVICES_SHARE, "share org devices"),
            entry(OWN_DEVICES_CREATE, "create own devices"),
            entry(OWN_DEVICES_VIEW, "view own devices"),
            entry(OWN_DEVICES_EDIT, "edit own devices"),
            entry(OWN_DEVICES_DELETE, "delete own devices"),
            entry(OWN_DEVICES_SHARE, "share own devices"),
            entry(SET_AUTH_TOKEN, "set auth token")
    );

    public static final Map<Integer, String> PERMISSION2_NAMES = Map.ofEntries(
        entry(RULE_GROUP_VIEW, "view rule group"),
        entry(RULE_GROUP_EDIT, "edit rule group")
    );

    private PermissionsTable() {
    }

}
