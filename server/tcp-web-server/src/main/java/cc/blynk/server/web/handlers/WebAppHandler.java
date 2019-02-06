package cc.blynk.server.web.handlers;

import cc.blynk.server.application.handlers.main.logic.MobileLogoutLogic;
import cc.blynk.server.common.JsonBasedSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.web.handlers.logic.account.WebEditAccountLogic;
import cc.blynk.server.web.handlers.logic.account.WebGetAccountLogic;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.LOGOUT;
import static cc.blynk.server.core.protocol.enums.Command.PING;
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
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_RULE_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.WEB_EDIT_USER_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ACCOUNT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_COUNT_FOR_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_COUNT_FOR_PRODUCT_AND_SUBPRODUCTS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_DEVICE_TIMELINE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORGS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_HIERARCHY;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_SHIPMENTS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ORG_USERS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCTS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_PRODUCT_LOCATIONS;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_ROLES;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_RULE_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_TEMP_SECURE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_GET_USER_COUNTERS_BY_ROLE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_INVITE_USER;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_GET_FIRMWARE_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_START;
import static cc.blynk.server.core.protocol.enums.Command.WEB_OTA_STOP;
import static cc.blynk.server.core.protocol.enums.Command.WEB_RESOLVE_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SET_AUTH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SHIPMENT_DELETE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_TRACK_ORG;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class WebAppHandler extends JsonBasedSimpleChannelInboundHandler<StringMessage, WebAppStateHolder> {

    public final WebAppStateHolder state;
    private final GlobalStats stats;
    private final WebAppLogicHolder webAppLogicHolder;

    public WebAppHandler(GlobalStats stats, WebAppLogicHolder webAppLogicHolder, WebAppStateHolder state) {
        super(StringMessage.class);
        this.state = state;
        this.stats = stats;
        this.webAppLogicHolder = webAppLogicHolder;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementWebStat();
        switch (msg.command) {
            case WEB_GET_ACCOUNT:
                WebGetAccountLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ACCOUNT:
                WebEditAccountLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE :
                webAppLogicHolder.webAppControlHardwareLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_TRACK_DEVICE:
                webAppLogicHolder.webTrackDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case GET_SUPERCHART_DATA:
                webAppLogicHolder.webGetGraphDataLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_RESOLVE_EVENT:
                webAppLogicHolder.webResolveLogEventLogic.messageReceived(ctx, state, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case WEB_CREATE_DEVICE :
                webAppLogicHolder.webCreateOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICE:
                webAppLogicHolder.webEditOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES :
                webAppLogicHolder.webGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE :
                webAppLogicHolder.webGetOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG :
                webAppLogicHolder.webGetOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORGS :
                webAppLogicHolder.webGetOrganizationsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG_USERS :
                webAppLogicHolder.webGetOrgUsersLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCT_LOCATIONS:
                webAppLogicHolder.webGetProductLocationsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CAN_INVITE_USER :
                webAppLogicHolder.webCanInviteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_PRODUCT :
                webAppLogicHolder.webCreateProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCT :
                webAppLogicHolder.webGetProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCTS :
                webAppLogicHolder.webGetProductsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_PRODUCT:
                webAppLogicHolder.webEditProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_PRODUCT :
                webAppLogicHolder.webDeleteProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICES_META_IN_PRODUCT:
                webAppLogicHolder.webEditDevicesMetaInProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_USER_INFO:
                webAppLogicHolder.webEditUserInfoLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_USER :
                webAppLogicHolder.webDeleteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_ORG :
                webAppLogicHolder.webCreateOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ORG:
                webAppLogicHolder.webEditOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_ORG :
                webAppLogicHolder.webDeleteOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CAN_DELETE_PRODUCT :
                webAppLogicHolder.webCanDeleteProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_INVITE_USER :
                webAppLogicHolder.webInviteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICE_METAFIELD:
                webAppLogicHolder.webEditDeviceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_TIMELINE :
                webAppLogicHolder.webGetDeviceTimelineLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_DEVICE :
                webAppLogicHolder.webDeleteOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_METAFIELD :
                webAppLogicHolder.webGetMetaFieldLogic.messageReceived(ctx, state, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case WEB_GET_TEMP_SECURE_TOKEN :
                webAppLogicHolder.webGetTempSecureTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case WEB_GET_ORG_HIERARCHY :
                webAppLogicHolder.webGetOrganizationsHierarchyLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_ROLE :
                webAppLogicHolder.webCreateRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ROLE:
                webAppLogicHolder.webEditRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_ROLE :
                webAppLogicHolder.webDeleteRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ROLES :
                webAppLogicHolder.webGetRolesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ROLE :
                webAppLogicHolder.webGetRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SET_AUTH_TOKEN :
                webAppLogicHolder.webSetAuthTokenForDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_OWN_ORG :
                webAppLogicHolder.webEditOwnOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_COUNT_FOR_ORG :
                webAppLogicHolder.webGetDeviceCountLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_COUNT_FOR_PRODUCT_AND_SUBPRODUCTS :
                webAppLogicHolder.webGetDeviceCountFromProductIDLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_OTA_GET_FIRMWARE_INFO :
                webAppLogicHolder.webGetFirmwareInfoLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_OTA_START :
                webAppLogicHolder.webStartOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_OTA_STOP :
                webAppLogicHolder.webStopOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SHIPMENT_DELETE:
                webAppLogicHolder.webCleanOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_TRACK_ORG :
                webAppLogicHolder.webTrackOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_USER_COUNTERS_BY_ROLE :
                webAppLogicHolder.webGetUserCountersByRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES_BY_REFERENCE_METAFIELD :
                webAppLogicHolder.commonGetDevicesByReferenceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_RULE_GROUP :
                webAppLogicHolder.webGetRuleGroupLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_RULE_GROUP :
                webAppLogicHolder.webEditRuleGroupLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG_SHIPMENTS :
                webAppLogicHolder.webGetOrgShipmentsLogic.messageReceived(ctx, state, msg);
                break;
        }
    }

    @Override
    public WebAppStateHolder getState() {
        return state;
    }

    @Override
    public void updateRole(Role role) {
        if (state.role.id == role.id) {
            state.setRole(role);
            log.trace("Changing ws session role for {}.", state.user.email);
        }
    }
}
