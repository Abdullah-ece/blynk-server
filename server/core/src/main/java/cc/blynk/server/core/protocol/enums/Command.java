package cc.blynk.server.core.protocol.enums;

import cc.blynk.utils.ReflectionUtil;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public final class Command {

    public static final short RESPONSE = 0;

    //app commands
    public static final short MOBILE_REGISTER = 1;
    public static final short LOGIN = 2;
    public static final short REDEEM = 3;
    public static final short DEVICE_CONNECTED = 4;

    public static final short DASH_SYNC = 5;
    public static final short PING = 6;
    public static final short MOBILE_ACTIVATE_DASHBOARD = 7;
    public static final short MOBILE_DEACTIVATE_DASHBOARD = 8;
    public static final short REFRESH_TOKEN = 9;
    public static final short MOBILE_EDIT_PROFILE_SETTINGS = 10;

    public static final short MOBILE_EDIT_DEVICE_METAFIELD = 11;

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
    public static final short MOBILE_CREATE_DASH = 21;
    public static final short MOBILE_EDIT_DASH = 22;
    public static final short MOBILE_DELETE_DASH = 23;
    public static final short MOBILE_LOAD_PROFILE_GZIPPED = 24;
    public static final short DEVICE_SYNC = 25;
    public static final short SHARING = 26;
    public static final short MOBILE_ADD_PUSH_TOKEN = 27;
    public static final short MOBILE_HARDWARE_GROUP = 28;

    public static final short HARDWARE_LOGIN = 29;
    //app sharing commands
    public static final short GET_SHARE_TOKEN = 30;
    public static final short REFRESH_SHARE_TOKEN = 31;
    public static final short SHARE_LOGIN = 32;
    //app commands
    public static final short MOBILE_CREATE_WIDGET = 33;
    public static final short MOBILE_EDIT_WIDGET = 34;
    public static final short MOBILE_DELETE_WIDGET = 35;

    //energy commands
    public static final short MOBILE_GET_ENERGY = 36;
    public static final short MOBILE_ADD_ENERGY = 37;

    public static final short MOBILE_EDIT_PROJECT_SETTINGS = 38;

    public static final short ASSIGN_TOKEN = 39;

    public static final short GET_SERVER = 40;
    public static final short CONNECT_REDIRECT = 41;

    public static final short MOBILE_CREATE_DEVICE = 42;
    public static final short MOBILE_EDIT_DEVICE = 43;
    public static final short MOBILE_DELETE_DEVICE = 44;
    public static final short MOBILE_GET_DEVICES = 45;

    public static final short MOBILE_GET_DEVICE = 50;

    public static final short MOBILE_EDIT_FACE = 51;

    //------------------------------------------

    //web sockets
    public static final short WEB_SOCKETS = 52;

    public static final short EVENTOR = 53;
    public static final short WEB_HOOKS = 54;

    public static final short MOBILE_CREATE_APP = 55;
    public static final short MOBILE_EDIT_APP = 56;
    public static final short MOBILE_DELETE_APP = 57;
    public static final short GET_PROJECT_BY_TOKEN = 58;
    public static final short EMAIL_QR = 59;
    public static final short GET_SUPERCHART_DATA = 60;
    public static final short MOBILE_DELETE_GRAPH_DATA = 61;

    public static final short GET_CLONE_CODE = 62;
    public static final short GET_PROJECT_BY_CLONE_CODE = 63;

    public static final short HARDWARE_LOG_EVENT = 64;
    public static final short HARDWARE_RESEND_FROM_BLUETOOTH = 65;
    public static final short LOGOUT = 66;

    public static final short MOBILE_CREATE_TILE_TEMPLATE = 67;
    public static final short MOBILE_EDIT_TILE_TEMPLATE = 68;
    public static final short MOBILE_DELETE_TILE_TEMPLATE = 69;
    public static final short MOBILE_GET_WIDGET = 70;
    public static final short DEVICE_DISCONNECTED = 71;
    public static final short OUTDATED_APP_NOTIFICATION = 72;
    public static final short WEB_TRACK_DEVICE = 73;
    public static final short MOBILE_GET_PROVISION_TOKEN = 74;
    public static final short WEB_RESOLVE_EVENT = 75;
    public static final short MOBILE_DELETE_DEVICE_DATA = 76;

    public static final short MOBILE_CREATE_REPORT = 77;
    public static final short MOBILE_EDIT_REPORT = 78;
    public static final short MOBILE_DELETE_REPORT = 79;
    public static final short MOBILE_EXPORT_REPORT = 80;

    public static final short RESET_PASSWORD = 81;

    //http codes. Used only for stats
    public static final short HTTP_IS_HARDWARE_CONNECTED = 82;
    public static final short HTTP_IS_APP_CONNECTED = 83;
    public static final short HTTP_GET_PIN_DATA = 84;
    public static final short HTTP_EDIT_PIN_DATA = 85;
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
    public static final short WEB_EDIT_ACCOUNT = 101;
    public static final short WEB_CREATE_DEVICE = 102;
    public static final short WEB_EDIT_DEVICE = 103;
    public static final short WEB_GET_DEVICES = 104;
    public static final short WEB_GET_DEVICE = 105;
    public static final short WEB_GET_ORG = 106;
    public static final short WEB_GET_ORGS = 107;
    public static final short WEB_GET_ORG_USERS = 108;
    public static final short WEB_GET_PRODUCT_LOCATIONS = 109;
    public static final short WEB_CAN_INVITE_USER = 110;
    public static final short WEB_EDIT_ORG = 111;
    public static final short WEB_CREATE_PRODUCT = 112;
    public static final short WEB_EDIT_PRODUCT = 113;
    public static final short WEB_DELETE_PRODUCT = 114;
    public static final short WEB_GET_PRODUCT = 115;
    public static final short WEB_GET_PRODUCTS = 116;
    public static final short WEB_EDIT_DEVICES_META_IN_PRODUCT = 117;
    public static final short WEB_EDIT_USER_INFO = 118;
    public static final short WEB_DELETE_USER = 119;
    public static final short WEB_CREATE_ORG = 120;
    public static final short WEB_DELETE_ORG = 122;
    public static final short WEB_CAN_DELETE_PRODUCT = 123;
    public static final short WEB_INVITE_USER = 124;
    public static final short WEB_LOGIN_VIA_INVITE = 125;
    public static final short WEB_EDIT_DEVICE_METAFIELD = 126;
    public static final short WEB_GET_DEVICE_TIMELINE = 127;
    public static final short WEB_DELETE_DEVICE = 128;
    public static final short WEB_GET_METAFIELD = 129;
    public static final short MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD = 130;
    public static final short WEB_GET_TEMP_SECURE_TOKEN = 131;
    public static final short WEB_GET_ORG_HIERARCHY = 132;
    public static final short WEB_CREATE_ROLE = 133;
    public static final short WEB_EDIT_ROLE = 134;
    public static final short WEB_GET_ROLE = 135;
    public static final short WEB_GET_ROLES = 136;
    public static final short WEB_DELETE_ROLE = 137;
    public static final short WEB_SET_AUTH_TOKEN = 138;
    public static final short WEB_EDIT_OWN_ORG = 139;
    public static final short WEB_GET_DEVICE_COUNT_FOR_ORG = 140;
    public static final short WEB_OTA_START = 141;
    public static final short WEB_OTA_STOP = 142;
    public static final short WEB_OTA_GET_FIRMWARE_INFO = 143;
    public static final short WEB_SHIPMENT_DELETE = 144;
    public static final short WEB_TRACK_ORG = 145;
    public static final short WEB_GET_USER_COUNTERS_BY_ROLE = 146;
    public static final short WEB_GET_DEVICES_BY_REFERENCE_METAFIELD = 147;
    public static final short MOBILE_GET_DEVICE_TIMELINE = 148;
    public static final short MOBILE_RESOLVE_DEVICE_TIMELINE = 149;
    public static final short WEB_GET_RULE_GROUP = 150;
    public static final short WEB_EDIT_RULE_GROUP = 151;
    public static final short WEB_GET_DEVICE_COUNT_FOR_PRODUCT_AND_SUBPRODUCTS = 152;
    public static final short WEB_GET_ORG_SHIPMENTS = 153;
    public static final short MOBILE_CREATE_GROUP_TEMPLATE = 154;
    public static final short MOBILE_EDIT_GROUP_TEMPLATE = 155;
    public static final short MOBILE_DELETE_GROUP_TEMPLATE = 156;
    public static final short MOBILE_CREATE_GROUP = 157;
    public static final short MOBILE_EDIT_GROUP = 158;
    public static final short MOBILE_DELETE_GROUP = 159;

    public static final int LAST_COMMAND_INDEX = 160;

    private Command() {
    }

    //all this code just to make logging more user-friendly
    public static final Map<Short, String> VALUES_NAME = ReflectionUtil.generateMapOfValueNameShort(Command.class);
    public static String getNameByValue(short val) {
        return VALUES_NAME.get(val);
    }
    //--------------------------------------------------------

}
