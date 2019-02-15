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
import static cc.blynk.server.core.protocol.enums.Command.WEB_RESOLVE_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SET_AUTH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SHIPMENT_DELETE;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SHIPMENT_GET_FIRMWARE_INFO;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SHIPMENT_START;
import static cc.blynk.server.core.protocol.enums.Command.WEB_SHIPMENT_STOP;
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
    private final WebLogicHolder webLogicHolder;

    public WebAppHandler(GlobalStats stats, WebLogicHolder webLogicHolder, WebAppStateHolder state) {
        super(StringMessage.class);
        this.state = state;
        this.stats = stats;
        this.webLogicHolder = webLogicHolder;
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
                webLogicHolder.webAppControlHardwareLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_TRACK_DEVICE:
                webLogicHolder.webTrackDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case GET_SUPERCHART_DATA:
                webLogicHolder.webGetGraphDataLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_RESOLVE_EVENT:
                webLogicHolder.webResolveLogEventLogic.messageReceived(ctx, state, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case WEB_CREATE_DEVICE :
                webLogicHolder.webCreateOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICE:
                webLogicHolder.webEditOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES :
                webLogicHolder.webGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE :
                webLogicHolder.webGetOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG :
                webLogicHolder.webGetOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORGS :
                webLogicHolder.webGetOrganizationsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG_USERS :
                webLogicHolder.webGetOrgUsersLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCT_LOCATIONS:
                webLogicHolder.webGetProductLocationsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CAN_INVITE_USER :
                webLogicHolder.webCanInviteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_PRODUCT :
                webLogicHolder.webCreateProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCT :
                webLogicHolder.webGetProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCTS :
                webLogicHolder.webGetProductsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_PRODUCT:
                webLogicHolder.webEditProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_PRODUCT :
                webLogicHolder.webDeleteProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICES_META_IN_PRODUCT:
                webLogicHolder.webEditDevicesMetaInProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_USER_INFO:
                webLogicHolder.webEditUserInfoLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_USER :
                webLogicHolder.webDeleteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_ORG :
                webLogicHolder.webCreateOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ORG:
                webLogicHolder.webEditOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_ORG :
                webLogicHolder.webDeleteOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CAN_DELETE_PRODUCT :
                webLogicHolder.webCanDeleteProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_INVITE_USER :
                webLogicHolder.webInviteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICE_METAFIELD:
                webLogicHolder.webEditDeviceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_TIMELINE :
                webLogicHolder.webGetDeviceTimelineLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_DEVICE :
                webLogicHolder.webDeleteOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_METAFIELD :
                webLogicHolder.webGetMetaFieldLogic.messageReceived(ctx, state, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case WEB_GET_TEMP_SECURE_TOKEN :
                webLogicHolder.webGetTempSecureTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case WEB_GET_ORG_HIERARCHY :
                webLogicHolder.webGetOrganizationsHierarchyLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_ROLE :
                webLogicHolder.webCreateRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ROLE:
                webLogicHolder.webEditRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_ROLE :
                webLogicHolder.webDeleteRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ROLES :
                webLogicHolder.webGetRolesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ROLE :
                webLogicHolder.webGetRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SET_AUTH_TOKEN :
                webLogicHolder.webSetAuthTokenForDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_OWN_ORG :
                webLogicHolder.webEditOwnOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_COUNT_FOR_ORG :
                webLogicHolder.webGetDeviceCountLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_COUNT_FOR_PRODUCT_AND_SUBPRODUCTS :
                webLogicHolder.webGetDeviceCountFromProductIDLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SHIPMENT_GET_FIRMWARE_INFO:
                webLogicHolder.webGetFirmwareInfoLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SHIPMENT_START:
                webLogicHolder.webStartShipmentLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SHIPMENT_STOP:
                webLogicHolder.webStopShipmentLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SHIPMENT_DELETE:
                webLogicHolder.webCleanOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_TRACK_ORG :
                webLogicHolder.webTrackOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_USER_COUNTERS_BY_ROLE :
                webLogicHolder.webGetUserCountersByRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES_BY_REFERENCE_METAFIELD :
                webLogicHolder.commonGetDevicesByReferenceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_RULE_GROUP :
                webLogicHolder.webGetRuleGroupLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_RULE_GROUP :
                webLogicHolder.webEditRuleGroupLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG_SHIPMENTS :
                webLogicHolder.webGetOrgShipmentsLogic.messageReceived(ctx, state, msg);
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
