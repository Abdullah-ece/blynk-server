package cc.blynk.client;

import static cc.blynk.server.core.protocol.enums.Command.*;

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
                return APP_SYNC;
            case "sharing" :
                return SHARING;
            case "gettoken" :
                return GET_TOKEN;
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
            case "export" :
                return EXPORT_GRAPH_DATA;
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
            case "track" :
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
                return WEB_UPDATE_ORG;
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
            case "getdevicemetafields" :
                return GET_DEVICE_METAFIELDS;
            case "updatedevicemetafield" :
                return UPDATE_DEVICE_METAFIELD;
            case "webgetmetafield" :
                return WEB_GET_METAFIELD;
            case "getdevicesbyreferencemetafield" :
                return MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;

            default:
                throw new IllegalArgumentException("Unsupported command");
        }
    }

}
