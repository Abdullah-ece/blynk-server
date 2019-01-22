package cc.blynk.server.core.stats.model;

import static cc.blynk.server.core.protocol.enums.Command.BLYNK_INTERNAL;
import static cc.blynk.server.core.protocol.enums.Command.BRIDGE;
import static cc.blynk.server.core.protocol.enums.Command.CONNECT_REDIRECT;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_CONNECTED;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL;
import static cc.blynk.server.core.protocol.enums.Command.EVENTOR;
import static cc.blynk.server.core.protocol.enums.Command.GET_SERVER;
import static cc.blynk.server.core.protocol.enums.Command.GET_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_PUSH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DEACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_REGISTER;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.PUSH_NOTIFICATION;
import static cc.blynk.server.core.protocol.enums.Command.REDEEM;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.RESPONSE;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.core.protocol.enums.Command.SHARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.SHARING;
import static cc.blynk.server.core.protocol.enums.Command.SMS;
import static cc.blynk.server.core.protocol.enums.Command.TWEET;
import static cc.blynk.server.core.protocol.enums.Command.WEB_HOOKS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SOCKETS;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.01.17.
 */
public class CommandStat {

    public int response;
    public int redeem;
    public int hardwareConnected;
    public int register;
    public int login;
    public int hardwareLogin;
    public int loadProfile;
    public int appSync;
    public int sharing;
    public int getToken;
    public int ping;
    public int activate;
    public int deactivate;
    public int refreshToken;
    public int getGraphData;
    public int setWidgetProperty;
    public int bridge;
    public int hardware;
    public int hardwareLogEvent;
    public int getSharedDash;
    public int getShareToken;
    public int refreshShareToken;
    public int shareLogin;
    public int createProject;
    public int updateProject;
    public int deleteProject;
    public int hardwareSync;
    public int internal;

    public int sms;
    public int tweet;
    public int email;
    public int push;
    public int addPushToken;

    public int createWidget;
    public int updateWidget;
    public int deleteWidget;

    public int createDevice;
    public int updateDevice;
    public int deleteDevice;
    public int getDevices;

    public int createTag;
    public int updateTag;
    public int deleteTag;
    public int getTags;

    public int addEnergy;
    public int getEnergy;

    public int getServer;
    public int connectRedirect;

    public int webSockets;

    public int eventor;
    public int webhooks;

    public int appTotal;
    public int webTotal;

    void assign(short field, int val) {
        switch (field) {
            case RESPONSE :
                this.response = val;
                break;
            case REDEEM :
                this.redeem = val;
                break;
            case DEVICE_CONNECTED:
                this.hardwareConnected = val;
                break;
            case MOBILE_REGISTER:
                this.register = val;
                break;
            case LOGIN :
                this.login = val;
                break;
            case HARDWARE_LOGIN :
                this.hardwareLogin = val;
                break;
            case MOBILE_LOAD_PROFILE_GZIPPED:
                this.loadProfile = val;
                break;
            case DEVICE_SYNC:
                this.appSync = val;
                break;
            case SHARING :
                this.sharing = val;
                break;
            case PING :
                this.ping = val;
                break;
            case SMS :
                this.sms = val;
                break;
            case MOBILE_ACTIVATE_DASHBOARD:
                this.activate = val;
                break;
            case MOBILE_DEACTIVATE_DASHBOARD:
                this.deactivate = val;
                break;
            case REFRESH_TOKEN :
                this.refreshToken = val;
                break;
            case SET_WIDGET_PROPERTY :
                this.setWidgetProperty = val;
                break;
            case BRIDGE :
                this.bridge = val;
                break;
            case HARDWARE :
                this.hardware = val;
                break;
            case HARDWARE_LOG_EVENT :
                this.hardwareLogEvent = val;
                break;
            case GET_SHARE_TOKEN :
                this.getShareToken = val;
                break;
            case REFRESH_SHARE_TOKEN :
                this.refreshShareToken = val;
                break;
            case SHARE_LOGIN :
                this.shareLogin = val;
                break;
            case MOBILE_CREATE_DASH:
                this.createProject = val;
                break;
            case MOBILE_EDIT_DASH:
                this.updateProject = val;
                break;
            case MOBILE_DELETE_DASH:
                this.deleteProject = val;
                break;
            case HARDWARE_SYNC :
                this.hardwareSync = val;
                break;
            case BLYNK_INTERNAL :
                this.internal = val;
                break;
            case MOBILE_ADD_PUSH_TOKEN:
                this.addPushToken = val;
                break;
            case TWEET :
                this.tweet = val;
                break;
            case EMAIL :
                this.email = val;
                break;
            case PUSH_NOTIFICATION :
                this.push = val;
                break;
            case MOBILE_CREATE_WIDGET:
                this.createWidget = val;
                break;
            case MOBILE_EDIT_WIDGET:
                this.updateWidget = val;
                break;
            case MOBILE_DELETE_WIDGET:
                this.deleteWidget = val;
                break;
            case MOBILE_CREATE_DEVICE:
                this.createDevice = val;
                break;
            case MOBILE_EDIT_DEVICE:
                this.updateDevice = val;
                break;
            case MOBILE_DELETE_DEVICE:
                this.deleteDevice = val;
                break;
            case MOBILE_GET_DEVICES:
                this.getDevices = val;
                break;
            case MOBILE_ADD_ENERGY:
                this.addEnergy = val;
                break;
            case MOBILE_GET_ENERGY:
                this.getEnergy = val;
                break;
            case GET_SERVER :
                this.getServer = val;
                break;
            case CONNECT_REDIRECT :
                this.connectRedirect = val;
                break;
            case WEB_SOCKETS :
                this.webSockets = val;
                break;
            case EVENTOR :
                this.eventor = val;
                break;
            case WEB_HOOKS :
                this.webhooks = val;
                break;
        }
    }

}
