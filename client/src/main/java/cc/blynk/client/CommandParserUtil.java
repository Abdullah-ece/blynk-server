package cc.blynk.client;

import static cc.blynk.server.core.protocol.enums.Command.ACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.ADD_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.ADD_PUSH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.ASSIGN_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.core.protocol.enums.Command.BRIDGE;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_APP;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_TAG;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.CREATE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.DEACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_APP;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_DEVICE_DATA;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_TAG;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.DELETE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL_QR;
import static cc.blynk.server.core.protocol.enums.Command.EXPORT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.GET_CLONE_CODE;
import static cc.blynk.server.core.protocol.enums.Command.GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.GET_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.GET_ENHANCED_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_CLONE_CODE;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROVISION_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_SERVER;
import static cc.blynk.server.core.protocol.enums.Command.GET_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_TAGS;
import static cc.blynk.server.core.protocol.enums.Command.GET_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_RESEND_FROM_BLUETOOTH;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.LOGOUT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_UPDATE_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.PUSH_NOTIFICATION;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.REGISTER;
import static cc.blynk.server.core.protocol.enums.Command.RESET_PASSWORD;
import static cc.blynk.server.core.protocol.enums.Command.RESOLVE_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.core.protocol.enums.Command.SHARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.SHARING;
import static cc.blynk.server.core.protocol.enums.Command.SMS;
import static cc.blynk.server.core.protocol.enums.Command.TRACK_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.TWEET;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_APP;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_FACE;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_PROFILE_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_PROJECT_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_TAG;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.UPDATE_WIDGET;
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
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_OWN_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES;
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
import static cc.blynk.server.core.protocol.enums.Command.WEB_INVITE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_LOGIN_VIA_INVITE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SET_AUTH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_DEVICES_META_IN_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_UPDATE_USER_INFO;

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
                return LOAD_PROFILE_GZIPPED;
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
                return GET_ENHANCED_GRAPH_DATA;
            case "deleteenhanceddata" :
                return DELETE_ENHANCED_GRAPH_DATA;
            case "activate" :
                return ACTIVATE_DASHBOARD;
            case "deactivate" :
                return DEACTIVATE_DASHBOARD;
            case "register" :
                return REGISTER;
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
                return ADD_PUSH_TOKEN;

            case "bridge" :
                return BRIDGE;

            case "createdash" :
                return CREATE_DASH;
            case "updatedash" :
                return UPDATE_DASH;
            case "deletedash" :
                return DELETE_DASH;
            case "updatesettings" :
                return UPDATE_PROJECT_SETTINGS;

            case "createwidget" :
                return CREATE_WIDGET;
            case "updatewidget" :
                return UPDATE_WIDGET;
            case "deletewidget" :
                return DELETE_WIDGET;
            case "getwidget" :
                return GET_WIDGET;

            case "hardsync" :
                return HARDWARE_SYNC;
            case "internal" :
                return BLYNK_INTERNAL;

            case "createtemplate" :
                return CREATE_TILE_TEMPLATE;
            case "updatetemplate" :
                return UPDATE_TILE_TEMPLATE;
            case "deletetemplate" :
                return DELETE_TILE_TEMPLATE;

            case "createdevice" :
                return CREATE_DEVICE;
            case "updatedevice" :
                return UPDATE_DEVICE;
            case "deletedevice" :
                return DELETE_DEVICE;
            case "getdevices" :
                return GET_DEVICES;
            case "getdevice" :
                return MOBILE_GET_DEVICE;

            case "createtag" :
                return CREATE_TAG;
            case "updatetag" :
                return UPDATE_TAG;
            case "deletetag" :
                return DELETE_TAG;
            case "gettags" :
                return GET_TAGS;

            case "addenergy" :
                return ADD_ENERGY;
            case "getenergy" :
                return GET_ENERGY;

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
                return CREATE_APP;
            case "updateapp" :
                return UPDATE_APP;
            case "deleteapp" :
                return DELETE_APP;
            case "getprojectbytoken" :
                return GET_PROJECT_BY_TOKEN;
            case "emailqr" :
                return EMAIL_QR;
            case "updateface" :
                return UPDATE_FACE;
            case "getclonecode" :
                return GET_CLONE_CODE;
            case "getprojectbyclonecode" :
                return GET_PROJECT_BY_CLONE_CODE;
            case "trackdevice" :
                return TRACK_DEVICE;
            case "getprovisiontoken" :
                return GET_PROVISION_TOKEN;
            case "resolveevent" :
                return RESOLVE_EVENT;
            case "deletedevicedata" :
                return DELETE_DEVICE_DATA;

            case "createreport" :
                return CREATE_REPORT;
            case "deletereport" :
                return DELETE_REPORT;
            case "updatereport" :
                return UPDATE_REPORT;
            case "exportreport" :
                return EXPORT_REPORT;
            case "resetpass" :
                return RESET_PASSWORD;
            case "getaccount" :
                return WEB_GET_ACCOUNT;
            case "updateaccount" :
                return WEB_UPDATE_ACCOUNT;
            case "webcreatedevice" :
                return WEB_CREATE_DEVICE;
            case "webupdatedevice" :
                return WEB_UPDATE_DEVICE;
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
                return WEB_UPDATE_PRODUCT;
            case "webdeleteproduct" :
                return WEB_DELETE_PRODUCT;
            case "webupdatedevicesmeta" :
                return WEB_UPDATE_DEVICES_META_IN_PRODUCT;
            case "webupdateuserinfo" :
                return WEB_UPDATE_USER_INFO;
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
                return WEB_UPDATE_DEVICE_METAFIELD;
            case "webgetdevicetimeline" :
                return WEB_GET_DEVICE_TIMELINE;
            case "webdeletedevice" :
                return WEB_DELETE_DEVICE;
            case "updatedevicemetafield" :
                return MOBILE_UPDATE_DEVICE_METAFIELD;
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
                return WEB_UPDATE_ROLE;
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
                return UPDATE_PROFILE_SETTINGS;
            case "webtrackorg" :
                return WEB_TRACK_ORG;

            default:
                throw new IllegalArgumentException("Unsupported command");
        }
    }

}
