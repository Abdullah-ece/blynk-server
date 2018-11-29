package cc.blynk.server.core.protocol.enums;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public final class Command {

    public static final short RESPONSE = 0;

    //app commands
    public static final short REGISTER = 1;
    public static final short LOGIN = 2;
    public static final short REDEEM = 3;
    public static final short DEVICE_CONNECTED = 4;

    public static final short DASH_SYNC = 5;
    public static final short PING = 6;
    public static final short ACTIVATE_DASHBOARD = 7;
    public static final short DEACTIVATE_DASHBOARD = 8;
    public static final short REFRESH_TOKEN = 9;

    public static final short MOBILE_UPDATE_DEVICE_METAFIELD = 11;

    //HARDWARE commands
    public static final short TWEET = 12;
    public static final short EMAIL = 13;
    public static final short PUSH_NOTIFICATION = 14;
    public static final short BRIDGE = 15;
    public static final short HARDWARE_SYNC = 16;
    public static final short BLYNK_INTERNAL = 17;
    public static final short SMS = 18;
    public static final short SET_WIDGET_PROPERTY = 19;
    public static final short HARDWARE = 20;
    //app commands
    public static final short CREATE_DASH = 21;
    public static final short UPDATE_DASH = 22;
    public static final short DELETE_DASH = 23;
    public static final short LOAD_PROFILE_GZIPPED = 24;
    public static final short DEVICE_SYNC = 25;
    public static final short SHARING = 26;
    public static final short ADD_PUSH_TOKEN = 27;

    public static final short HARDWARE_LOGIN = 29;
    //app sharing commands
    public static final short GET_SHARE_TOKEN = 30;
    public static final short REFRESH_SHARE_TOKEN = 31;
    public static final short SHARE_LOGIN = 32;
    //app commands
    public static final short CREATE_WIDGET = 33;
    public static final short UPDATE_WIDGET = 34;
    public static final short DELETE_WIDGET = 35;

    //energy commands
    public static final short GET_ENERGY = 36;
    public static final short ADD_ENERGY = 37;

    public static final short UPDATE_PROJECT_SETTINGS = 38;

    public static final short ASSIGN_TOKEN = 39;

    public static final short GET_SERVER = 40;
    public static final short CONNECT_REDIRECT = 41;

    public static final short CREATE_DEVICE = 42;
    public static final short UPDATE_DEVICE = 43;
    public static final short DELETE_DEVICE = 44;
    public static final short GET_DEVICES = 45;

    public static final short CREATE_TAG = 46;
    public static final short UPDATE_TAG = 47;
    public static final short DELETE_TAG = 48;
    public static final short GET_TAGS = 49;
    public static final short MOBILE_GET_DEVICE = 50;

    public static final short UPDATE_FACE = 51;

    //------------------------------------------

    //web sockets
    public static final short WEB_SOCKETS = 52;

    public static final short EVENTOR = 53;
    public static final short WEB_HOOKS = 54;

    public static final short CREATE_APP = 55;
    public static final short UPDATE_APP = 56;
    public static final short DELETE_APP = 57;
    public static final short GET_PROJECT_BY_TOKEN = 58;
    public static final short EMAIL_QR = 59;
    public static final short GET_ENHANCED_GRAPH_DATA = 60;
    public static final short DELETE_ENHANCED_GRAPH_DATA = 61;

    public static final short GET_CLONE_CODE = 62;
    public static final short GET_PROJECT_BY_CLONE_CODE = 63;

    public static final short HARDWARE_LOG_EVENT = 64;
    public static final short HARDWARE_RESEND_FROM_BLUETOOTH = 65;
    public static final short LOGOUT = 66;

    public static final short CREATE_TILE_TEMPLATE = 67;
    public static final short UPDATE_TILE_TEMPLATE = 68;
    public static final short DELETE_TILE_TEMPLATE = 69;
    public static final short GET_WIDGET = 70;
    public static final short DEVICE_DISCONNECTED = 71;
    public static final short OUTDATED_APP_NOTIFICATION = 72;
    public static final short TRACK_DEVICE = 73;
    public static final short GET_PROVISION_TOKEN = 74;
    public static final short RESOLVE_EVENT = 75;
    public static final short DELETE_DEVICE_DATA = 76;

    public static final short CREATE_REPORT = 77;
    public static final short UPDATE_REPORT = 78;
    public static final short DELETE_REPORT = 79;
    public static final short EXPORT_REPORT = 80;

    public static final short RESET_PASSWORD = 81;

    //http codes. Used only for stats
    public static final short HTTP_IS_HARDWARE_CONNECTED = 82;
    public static final short HTTP_IS_APP_CONNECTED = 83;
    public static final short HTTP_GET_PIN_DATA = 84;
    public static final short HTTP_UPDATE_PIN_DATA = 85;
    public static final short HTTP_NOTIFY = 86;
    public static final short HTTP_EMAIL = 87;
    public static final short HTTP_GET_PROJECT = 88;
    public static final short HTTP_QR = 89;
    public static final short HTTP_GET_HISTORY_DATA = 90;
    public static final short HTTP_START_OTA = 91;
    public static final short HTTP_STOP_OTA = 92;
    public static final short HTTP_CLONE = 93;
    public static final short HTTP_TOTAL = 94;
    public static final short HTTP_GET_DEVICE = 95;

    public static final short WEB_JSON = 99;
    public static final short WEB_GET_ACCOUNT = 100;
    public static final short WEB_UPDATE_ACCOUNT = 101;
    public static final short WEB_CREATE_DEVICE = 102;
    public static final short WEB_UPDATE_DEVICE = 103;
    public static final short WEB_GET_DEVICES = 104;
    public static final short WEB_GET_DEVICE = 105;
    public static final short WEB_GET_ORG = 106;
    public static final short WEB_GET_ORGS = 107;
    public static final short WEB_GET_ORG_USERS = 108;
    public static final short WEB_GET_PRODUCT_LOCATIONS = 109;
    public static final short WEB_CAN_INVITE_USER = 110;
    public static final short WEB_UPDATE_ORG = 111;
    public static final short WEB_CREATE_PRODUCT = 112;
    public static final short WEB_UPDATE_PRODUCT = 113;
    public static final short WEB_DELETE_PRODUCT = 114;
    public static final short WEB_GET_PRODUCT = 115;
    public static final short WEB_GET_PRODUCTS = 116;
    public static final short WEB_UPDATE_DEVICES_META_IN_PRODUCT = 117;
    public static final short WEB_UPDATE_USER_INFO = 118;
    public static final short WEB_DELETE_USER = 119;
    public static final short WEB_CREATE_ORG = 120;
    public static final short WEB_DELETE_ORG = 122;
    public static final short WEB_CAN_DELETE_PRODUCT = 123;
    public static final short WEB_INVITE_USER = 124;
    public static final short WEB_LOGIN_VIA_INVITE = 125;
    public static final short WEB_UPDATE_DEVICE_METAFIELD = 126;
    public static final short WEB_GET_DEVICE_TIMELINE = 127;
    public static final short WEB_DELETE_DEVICE = 128;
    public static final short WEB_GET_METAFIELD = 129;
    public static final short MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD = 130;
    public static final short WEB_GET_TEMP_SECURE_TOKEN = 131;
    public static final short WEB_GET_ORG_HIERARCHY = 132;
    public static final short WEB_CREATE_ROLE = 133;
    public static final short WEB_UPDATE_ROLE = 134;
    public static final short WEB_GET_ROLE = 135;
    public static final short WEB_GET_ROLES = 136;
    public static final short WEB_DELETE_ROLE = 137;
    public static final short WEB_SET_AUTH_TOKEN = 138;

    public static final int LAST_COMMAND_INDEX = 150;

    private Command() {
    }

    //all this code just to make logging more user-friendly
    public static final Map<Short, String> VALUES_NAME = Map.ofEntries(
            Map.entry(RESPONSE, "Response"),
            Map.entry(REDEEM, "Redeem"),
            Map.entry(DEVICE_CONNECTED, "HardwareConnected"),
            Map.entry(REGISTER, "Register"),
            Map.entry(DASH_SYNC, "DashSync"),
            Map.entry(LOGIN, "Login"),
            Map.entry(HARDWARE_LOGIN, "LoginHardware"),
            Map.entry(LOGOUT, "Logout"),
            Map.entry(LOAD_PROFILE_GZIPPED, "LoadProfile"),
            Map.entry(DEVICE_SYNC, "AppSync"),
            Map.entry(SHARING, "Sharing"),
            Map.entry(ASSIGN_TOKEN, "AssignToken"),
            Map.entry(PING, "Ping"), Map.entry(SMS, "Sms"),
            Map.entry(ACTIVATE_DASHBOARD, "Activate"),
            Map.entry(DEACTIVATE_DASHBOARD, "Deactivate"),
            Map.entry(REFRESH_TOKEN, "RefreshToken"),
            Map.entry(GET_ENHANCED_GRAPH_DATA, "GetEnhancedGraphDataRequest"),
            Map.entry(DELETE_ENHANCED_GRAPH_DATA, "DeleteEnhancedGraphDataRequest"),
            Map.entry(SET_WIDGET_PROPERTY, "setWidgetProperty"),
            Map.entry(BRIDGE, "Bridge"),
            Map.entry(HARDWARE, "Hardware"),
            Map.entry(GET_SHARE_TOKEN, "GetShareToken"),
            Map.entry(REFRESH_SHARE_TOKEN, "RefreshShareToken"),
            Map.entry(SHARE_LOGIN, "ShareLogin"),
            Map.entry(CREATE_DASH, "CreateProject"),
            Map.entry(UPDATE_DASH, "UpdateProject"),
            Map.entry(DELETE_DASH, "DeleteProject"),
            Map.entry(HARDWARE_SYNC, "HardwareSync"),
            Map.entry(BLYNK_INTERNAL, "Internal"),
            Map.entry(ADD_PUSH_TOKEN, "AddPushToken"),
            Map.entry(TWEET, "Tweet"), Map.entry(EMAIL, "Email"),
            Map.entry(PUSH_NOTIFICATION, "Push"),
            Map.entry(CREATE_WIDGET, "CreateWidget"),
            Map.entry(UPDATE_WIDGET, "UpdateWidget"),
            Map.entry(DELETE_WIDGET, "DeleteWidget"),
            Map.entry(GET_WIDGET, "GetWidget"),
            Map.entry(CREATE_TILE_TEMPLATE, "CreateTileTemplate"),
            Map.entry(UPDATE_TILE_TEMPLATE, "UpdateTileTemplate"),
            Map.entry(DELETE_TILE_TEMPLATE, "DeleteTileTemplate"),
            Map.entry(CREATE_DEVICE, "CreateDevice"),
            Map.entry(UPDATE_DEVICE, "UpdateDevice"),
            Map.entry(DELETE_DEVICE, "DeleteDevice"),
            Map.entry(MOBILE_GET_DEVICE, "GetDevice"),
            Map.entry(GET_DEVICES, "GetDevices"),
            Map.entry(ADD_ENERGY, "AddEnergy"),
            Map.entry(GET_ENERGY, "GetEnergy"),
            Map.entry(UPDATE_PROJECT_SETTINGS, "UpdateProjectSettings"),
            Map.entry(GET_SERVER, "GetServer"),
            Map.entry(CONNECT_REDIRECT, "ConnectRedirect"),
            Map.entry(CREATE_APP, "CreateApp"),
            Map.entry(UPDATE_APP, "UpdateApp"),
            Map.entry(DELETE_APP, "DeleteApp"),
            Map.entry(GET_PROJECT_BY_TOKEN, "GetProjectByToken"),
            Map.entry(EMAIL_QR, "MailQRs"),
            Map.entry(UPDATE_FACE, "UpdateFace"),
            Map.entry(GET_PROVISION_TOKEN, "getProvisionToken"),
            Map.entry(RESOLVE_EVENT, "resolveEvent"),
            Map.entry(DELETE_DEVICE_DATA, "deleteDeviceData"),
            Map.entry(HARDWARE_LOG_EVENT, "HardwareLogEvent"),
            Map.entry(HARDWARE_RESEND_FROM_BLUETOOTH, "HardwareResendFromBluetooth"),
            Map.entry(GET_CLONE_CODE, "GetCloneCode"),
            Map.entry(GET_PROJECT_BY_CLONE_CODE, "GetProjectByCloneCode"),
            Map.entry(DEVICE_DISCONNECTED, "deviceOffline"),
            Map.entry(OUTDATED_APP_NOTIFICATION, "outdatedAppNotification"),
            Map.entry(TRACK_DEVICE, "trackDevice"),
            Map.entry(CREATE_REPORT, "createReport"),
            Map.entry(UPDATE_REPORT, "updateReport"),
            Map.entry(DELETE_REPORT, "deleteReport"),
            Map.entry(EXPORT_REPORT, "exportReport"),
            Map.entry(RESET_PASSWORD, "resetPass"),
            Map.entry(MOBILE_UPDATE_DEVICE_METAFIELD, "UpdateDeviceMetafield"),
            Map.entry(HTTP_IS_HARDWARE_CONNECTED, "HttpIsHardwareConnected"),
            Map.entry(HTTP_IS_APP_CONNECTED, "HttpIsAppConnected"),
            Map.entry(HTTP_GET_PIN_DATA, "HttpGetPinData"),
            Map.entry(HTTP_UPDATE_PIN_DATA, "HttpUpdatePinData"),
            Map.entry(HTTP_NOTIFY, "HttpNotify"),
            Map.entry(HTTP_EMAIL, "HttpEmail"),
            Map.entry(HTTP_GET_PROJECT, "HttpGetProject"),
            Map.entry(HTTP_QR, "QR"),
            Map.entry(HTTP_CLONE, "Clone"),
            Map.entry(HTTP_GET_HISTORY_DATA, "HttpGetHistoryData"),
            Map.entry(HTTP_START_OTA, "HttpStartOTA"),
            Map.entry(HTTP_TOTAL, "HttpTotal"),
            Map.entry(HTTP_GET_DEVICE, "HttpGetDevice"),
            Map.entry(WEB_SOCKETS, "WebSockets"),
            Map.entry(EVENTOR, "Eventor"),
            Map.entry(WEB_HOOKS, "WebHooks"),
            Map.entry(WEB_GET_ACCOUNT, "WebGetAccount"),
            Map.entry(WEB_UPDATE_ACCOUNT, "WebUpdateAccount"),
            Map.entry(WEB_CREATE_DEVICE, "WebCreateDevice"),
            Map.entry(WEB_GET_DEVICES, "WebGetDevices"),
            Map.entry(WEB_GET_DEVICE, "WebGetDevice"),
            Map.entry(WEB_GET_ORG, "WebGetOrg"),
            Map.entry(WEB_GET_ORGS, "WebGetOrgs"),
            Map.entry(WEB_GET_ORG_USERS, "WebGetOrgUsers"),
            Map.entry(WEB_GET_PRODUCT_LOCATIONS, "WebGetProductLocations"),
            Map.entry(WEB_CAN_INVITE_USER, "WebCanInviteUser"),
            Map.entry(WEB_CREATE_PRODUCT, "WebCreateProduct"),
            Map.entry(WEB_UPDATE_PRODUCT, "WebUpdateProduct"),
            Map.entry(WEB_DELETE_PRODUCT, "WebDeleteProduct"),
            Map.entry(WEB_GET_PRODUCT, "WebGetProduct"),
            Map.entry(WEB_GET_PRODUCTS, "WebGetProducts"),
            Map.entry(WEB_UPDATE_DEVICES_META_IN_PRODUCT, "WebUpdateDevicesMeta"),
            Map.entry(WEB_UPDATE_DEVICE, "WebUpdateDevice"),
            Map.entry(WEB_UPDATE_USER_INFO, "WebUpdateUserInfo"),
            Map.entry(WEB_DELETE_USER, "WebDeleteUser"),
            Map.entry(WEB_CREATE_ORG, "WebCreateOrg"),
            Map.entry(WEB_UPDATE_ORG, "WebUpdateOrg"),
            Map.entry(WEB_DELETE_ORG, "WebDeleteOrg"),
            Map.entry(WEB_CAN_DELETE_PRODUCT, "WebCanDeleteProduct"),
            Map.entry(WEB_INVITE_USER, "WebInviteUser"),
            Map.entry(WEB_LOGIN_VIA_INVITE, "WebLoginViaInvite"),
            Map.entry(WEB_UPDATE_DEVICE_METAFIELD, "WebUpdateDeviceMetafield"),
            Map.entry(WEB_GET_DEVICE_TIMELINE, "WebGetDeviceTimeline"),
            Map.entry(WEB_DELETE_DEVICE, "WebDeleteDevice"),
            Map.entry(MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD, "GetDevicesByReferenceMetafield"),
            Map.entry(WEB_GET_TEMP_SECURE_TOKEN, "WebGetTempSecureToken"),
            Map.entry(WEB_GET_ORG_HIERARCHY, "WebGetOrgHierarchy"),
            Map.entry(WEB_CREATE_ROLE, "WebCreateRole"),
            Map.entry(WEB_UPDATE_ROLE, "WebUpdateRole"),
            Map.entry(WEB_GET_ROLES, "WebGetRoles"),
            Map.entry(WEB_GET_ROLE, "WebGetRole"),
            Map.entry(WEB_DELETE_ROLE, "WebDeleteRole"),
            Map.entry(WEB_SET_AUTH_TOKEN, "WebSetAuthToken")
    );

    public static String getNameByValue(short val) {
        return VALUES_NAME.get(val);
    }
    //--------------------------------------------------------

}
