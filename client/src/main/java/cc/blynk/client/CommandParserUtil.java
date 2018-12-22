package cc.blynk.client;

import static cc.blynk.server.core.protocol.enums.Command.ASSIGN_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.core.protocol.enums.Command.BRIDGE;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL_QR;
import static cc.blynk.server.core.protocol.enums.Command.GET_CLONE_CODE;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_CLONE_CODE;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_SERVER;
import static cc.blynk.server.core.protocol.enums.Command.GET_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_RESEND_FROM_BLUETOOTH;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.LOGOUT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_PUSH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_TAG;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DEACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_TAG;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_FACE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_PROFILE_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_PROJECT_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_TAG;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EXPORT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_PROVISION_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_TAGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_REGISTER;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.PUSH_NOTIFICATION;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.RESET_PASSWORD;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.core.protocol.enums.Command.SHARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.SHARING;
import static cc.blynk.server.core.protocol.enums.Command.SMS;
import static cc.blynk.server.core.protocol.enums.Command.TWEET;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CAN_DELETE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CAN_INVITE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_CREATE_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_DELETE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_DEVICES_META_IN_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_OWN_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_USER_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_COUNT_FOR_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_TIMELINE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORGS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_HIERARCHY;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_USERS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCTS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCT_LOCATIONS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ROLES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_TEMP_SECURE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_USER_COUNTERS_BY_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_INVITE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_LOGIN_VIA_INVITE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_GET_FIRMWARE_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_START;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_STOP;
import static cc.blynk.server.core.protocol.enums.Command.WEB_RESOLVE_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SET_AUTH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_ORG;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 * Convertor between user-friendly command and protocol command code
 */
public final class CommandParserUtil {

    private CommandParserUtil() {
    }

    public static Short parseCommand(String stringCommand) {
        switch (stringCommand.toLowerCase()) {
            case "hardware" :
                return HARDWARE;
            case "logevent" :
                return HARDWARE_LOG_EVENT;
            case "hardwarebt" :
                return HARDWARE_RESEND_FROM_BLUETOOTH;
            case "ping" :
                return PING;
            case "loadprofilegzipped" :
                return MOBILE_LOAD_PROFILE_GZIPPED;
            case "appsync" :
                return DEVICE_SYNC;
            case "sharing" :
                return SHARING;
            case "assigntoken" :
                return ASSIGN_TOKEN;
            case "refreshtoken" :
                return REFRESH_TOKEN;
            case "login" :
                return LOGIN;
            case "hardwarelogin" :
                return HARDWARE_LOGIN;
            case "logout" :
                return LOGOUT;
            case "getenhanceddata" :
                return GET_SUPERCHART_DATA;
            case "deleteenhanceddata" :
                return MOBILE_DELETE_ENHANCED_GRAPH_DATA;
            case "activate" :
                return MOBILE_ACTIVATE_DASHBOARD;
            case "deactivate" :
                return MOBILE_DEACTIVATE_DASHBOARD;
            case "register" :
                return MOBILE_REGISTER;
            case "setproperty" :
                return SET_WIDGET_PROPERTY;

            case "tweet" :
                return TWEET;
            case "email" :
                return EMAIL;
            case "push" :
                return PUSH_NOTIFICATION;
            case "sms" :
                return SMS;
            case "addpushtoken" :
                return MOBILE_ADD_PUSH_TOKEN;

            case "bridge" :
                return BRIDGE;

            case "createdash" :
                return MOBILE_CREATE_DASH;
            case "updatedash" :
                return MOBILE_EDIT_DASH;
            case "deletedash" :
                return MOBILE_DELETE_DASH;
            case "updatesettings" :
                return MOBILE_EDIT_PROJECT_SETTINGS;

            case "createwidget" :
                return MOBILE_CREATE_WIDGET;
            case "updatewidget" :
                return MOBILE_EDIT_WIDGET;
            case "deletewidget" :
                return MOBILE_DELETE_WIDGET;
            case "getwidget" :
                return MOBILE_GET_WIDGET;

            case "hardsync" :
                return HARDWARE_SYNC;
            case "internal" :
                return BLYNK_INTERNAL;

            case "createtemplate" :
                return MOBILE_CREATE_TILE_TEMPLATE;
            case "updatetemplate" :
                return MOBILE_EDIT_TILE_TEMPLATE;
            case "deletetemplate" :
                return MOBILE_DELETE_TILE_TEMPLATE;

            case "createdevice" :
                return MOBILE_CREATE_DEVICE;
            case "updatedevice" :
                return MOBILE_EDIT_DEVICE;
            case "deletedevice" :
                return MOBILE_DELETE_DEVICE;
            case "getdevices" :
                return MOBILE_GET_DEVICES;
            case "getdevice" :
                return MOBILE_GET_DEVICE;

            case "createtag" :
                return MOBILE_CREATE_TAG;
            case "updatetag" :
                return MOBILE_EDIT_TAG;
            case "deletetag" :
                return MOBILE_DELETE_TAG;
            case "gettags" :
                return MOBILE_GET_TAGS;

            case "addenergy" :
                return MOBILE_ADD_ENERGY;
            case "getenergy" :
                return MOBILE_GET_ENERGY;

            case "getserver" :
                return GET_SERVER;

            //sharing section
            case "sharelogin" :
                return SHARE_LOGIN;
            case "getsharetoken" :
                return GET_SHARE_TOKEN;
            case "refreshsharetoken" :
                return REFRESH_SHARE_TOKEN;

            case "createapp" :
                return MOBILE_CREATE_APP;
            case "updateapp" :
                return MOBILE_EDIT_APP;
            case "deleteapp" :
                return MOBILE_DELETE_APP;
            case "getprojectbytoken" :
                return GET_PROJECT_BY_TOKEN;
            case "emailqr" :
                return EMAIL_QR;
            case "updateface" :
                return MOBILE_EDIT_FACE;
            case "getclonecode" :
                return GET_CLONE_CODE;
            case "getprojectbyclonecode" :
                return GET_PROJECT_BY_CLONE_CODE;
            case "trackdevice" :
                return WEB_TRACK_DEVICE;
            case "getprovisiontoken" :
                return MOBILE_GET_PROVISION_TOKEN;
            case "resolveevent" :
                return WEB_RESOLVE_EVENT;
            case "deletedevicedata" :
                return MOBILE_DELETE_DEVICE_DATA;

            case "createreport" :
                return MOBILE_CREATE_REPORT;
            case "deletereport" :
                return MOBILE_DELETE_REPORT;
            case "updatereport" :
                return MOBILE_EDIT_REPORT;
            case "exportreport" :
                return MOBILE_EXPORT_REPORT;
            case "resetpass" :
                return RESET_PASSWORD;
            case "getaccount" :
                return WEB_GET_ACCOUNT;
            case "updateaccount" :
                return WEB_EDIT_ACCOUNT;
            case "webcreatedevice" :
                return WEB_CREATE_DEVICE;
            case "webupdatedevice" :
                return WEB_EDIT_DEVICE;
            case "webgetdevices" :
                return WEB_GET_DEVICES;
            case "webgetdevice" :
                return WEB_GET_DEVICE;
            case "webgetorg" :
                return WEB_GET_ORG;
            case "webgetorgs" :
                return WEB_GET_ORGS;
            case "webgetorgusers" :
                return WEB_GET_ORG_USERS;
            case "webgetproductlocations" :
                return WEB_GET_PRODUCT_LOCATIONS;
            case "webcaninviteuser" :
                return WEB_CAN_INVITE_USER;
            case "webupdateorg" :
                return WEB_EDIT_ORG;
            case "webcreateproduct" :
                return WEB_CREATE_PRODUCT;
            case "webgetproduct" :
                return WEB_GET_PRODUCT;
            case "webgetproducts" :
                return WEB_GET_PRODUCTS;
            case "webupdateproduct" :
                return WEB_EDIT_PRODUCT;
            case "webdeleteproduct" :
                return WEB_DELETE_PRODUCT;
            case "webupdatedevicesmeta" :
                return WEB_EDIT_DEVICES_META_IN_PRODUCT;
            case "webupdateuserinfo" :
                return WEB_EDIT_USER_INFO;
            case "webdeleteuser" :
                return WEB_DELETE_USER;
            case "webcreateorg" :
                return WEB_CREATE_ORG;
            case "webdeleteorg" :
                return WEB_DELETE_ORG;
            case "webcandeleteproduct" :
                return WEB_CAN_DELETE_PRODUCT;
            case "webinviteuser" :
                return WEB_INVITE_USER;
            case "webloginviainvite" :
                return WEB_LOGIN_VIA_INVITE;
            case "webupdatedevicemetafield" :
                return WEB_EDIT_DEVICE_METAFIELD;
            case "webgetdevicetimeline" :
                return WEB_GET_DEVICE_TIMELINE;
            case "webdeletedevice" :
                return WEB_DELETE_DEVICE;
            case "updatedevicemetafield" :
                return MOBILE_EDIT_DEVICE_METAFIELD;
            case "webgetmetafield" :
                return WEB_GET_METAFIELD;
            case "getdevicesbyreferencemetafield" :
                return MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;
            case "webgettempsecuretoken" :
                return WEB_GET_TEMP_SECURE_TOKEN;
            case "caninviteuser" :
                return WEB_CAN_INVITE_USER;
            case "getorganizationhierarchy" :
                return WEB_GET_ORG_HIERARCHY;
            case "webcreaterole" :
                return WEB_CREATE_ROLE;
            case "webdeleterole" :
                return WEB_DELETE_ROLE;
            case "webupdaterole" :
                return WEB_EDIT_ROLE;
            case "webgetroles" :
                return WEB_GET_ROLES;
            case "webgetrole" :
                return WEB_GET_ROLE;
            case "websetauthtoken" :
                return WEB_SET_AUTH_TOKEN;
            case "webeditownorg" :
                return WEB_EDIT_OWN_ORG;
            case "webgetdevicecountfororg" :
                return WEB_GET_DEVICE_COUNT_FOR_ORG;
            case "mobileupdateprofilesettings" :
                return MOBILE_EDIT_PROFILE_SETTINGS;
            case "webtrackorg" :
                return WEB_TRACK_ORG;
            case "webotagetfirmwareinfo" :
                return WEB_OTA_GET_FIRMWARE_INFO;
            case "webstartota" :
                return WEB_OTA_START;
            case "webstopota" :
                return WEB_OTA_STOP;
            case "getusercountersbyrole" :
                return WEB_GET_USER_COUNTERS_BY_ROLE;
            case "webgetdevicesbyreferencemetafield" :
                return WEB_GET_DEVICES_BY_REFERENCE_METAFIELD;

            default:
                throw new IllegalArgumentException("Unsupported command");
        }
    }

}
