package cc.blynk.server.web.handlers;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.logic.MobileLogoutLogic;
import cc.blynk.server.common.JsonBasedSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.CommonGetDevicesByReferenceMetafieldLogic;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.common.handlers.logic.timeline.WebGetDeviceTimelineLogic;
import cc.blynk.server.common.handlers.logic.timeline.WebResolveLogEventLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.web.WebAppStateHolder;
import cc.blynk.server.web.handlers.logic.account.WebEditAccountLogic;
import cc.blynk.server.web.handlers.logic.account.WebGetAccountLogic;
import cc.blynk.server.web.handlers.logic.device.WebCreateOrgDeviceLogic;
import cc.blynk.server.web.handlers.logic.device.WebDeleteOrgDeviceLogic;
import cc.blynk.server.web.handlers.logic.device.WebEditDeviceMetafieldLogic;
import cc.blynk.server.web.handlers.logic.device.WebEditOrgDeviceLogic;
import cc.blynk.server.web.handlers.logic.device.WebGetGraphDataLogic;
import cc.blynk.server.web.handlers.logic.device.WebGetMetaFieldLogic;
import cc.blynk.server.web.handlers.logic.device.WebGetOrgDeviceLogic;
import cc.blynk.server.web.handlers.logic.device.WebGetOrgDevicesLogic;
import cc.blynk.server.web.handlers.logic.device.WebSetAuthTokenForDeviceLogic;
import cc.blynk.server.web.handlers.logic.device.WebTrackDeviceLogic;
import cc.blynk.server.web.handlers.logic.device.control.WebAppControlHardwareLogic;
import cc.blynk.server.web.handlers.logic.organization.WebCreateOrganizationLogic;
import cc.blynk.server.web.handlers.logic.organization.WebDeleteOrganizationLogic;
import cc.blynk.server.web.handlers.logic.organization.WebEditOrganizationLogic;
import cc.blynk.server.web.handlers.logic.organization.WebEditOwnOrganizationLogic;
import cc.blynk.server.web.handlers.logic.organization.WebEditRuleGroupLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetDeviceCountFromProductIDLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetDeviceCountLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationsHierarchyLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetOrganizationsLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetProductLocationsLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetRuleGroupLogic;
import cc.blynk.server.web.handlers.logic.organization.WebGetTempSecureTokenLogic;
import cc.blynk.server.web.handlers.logic.organization.WebTrackOrganizationLogic;
import cc.blynk.server.web.handlers.logic.organization.ota.WebDeleteShipmentLogic;
import cc.blynk.server.web.handlers.logic.organization.ota.WebGetFirmwareInfoLogic;
import cc.blynk.server.web.handlers.logic.organization.ota.WebGetOrgShipmentsLogic;
import cc.blynk.server.web.handlers.logic.organization.ota.WebStartOtaLogic;
import cc.blynk.server.web.handlers.logic.organization.ota.WebStopOtaLogic;
import cc.blynk.server.web.handlers.logic.organization.roles.WebCreateRoleLogic;
import cc.blynk.server.web.handlers.logic.organization.roles.WebDeleteRoleLogic;
import cc.blynk.server.web.handlers.logic.organization.roles.WebEditRoleLogic;
import cc.blynk.server.web.handlers.logic.organization.roles.WebGetRoleLogic;
import cc.blynk.server.web.handlers.logic.organization.roles.WebGetRolesLogic;
import cc.blynk.server.web.handlers.logic.organization.roles.WebGetUserCountersByRoleLogic;
import cc.blynk.server.web.handlers.logic.organization.users.WebCanInviteUserLogic;
import cc.blynk.server.web.handlers.logic.organization.users.WebDeleteUserLogic;
import cc.blynk.server.web.handlers.logic.organization.users.WebEditUserInfoLogic;
import cc.blynk.server.web.handlers.logic.organization.users.WebGetOrgUsersLogic;
import cc.blynk.server.web.handlers.logic.organization.users.WebInviteUserLogic;
import cc.blynk.server.web.handlers.logic.product.WebCanDeleteProductLogic;
import cc.blynk.server.web.handlers.logic.product.WebCreateProductLogic;
import cc.blynk.server.web.handlers.logic.product.WebDeleteProductLogic;
import cc.blynk.server.web.handlers.logic.product.WebEditDevicesMetaInProductLogic;
import cc.blynk.server.web.handlers.logic.product.WebEditProductLogic;
import cc.blynk.server.web.handlers.logic.product.WebGetProductLogic;
import cc.blynk.server.web.handlers.logic.product.WebGetProductsLogic;
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
public class WebAppHandler extends JsonBasedSimpleChannelInboundHandler<StringMessage, WebAppStateHolder> {

    public final WebAppStateHolder state;
    private final WebAppControlHardwareLogic webAppControlHardwareLogic;
    private final WebGetGraphDataLogic webGetGraphDataLogic;
    private final WebResolveLogEventLogic webResolveLogEventLogic;
    private final WebCreateOrgDeviceLogic webCreateOrgDeviceLogic;
    private final WebGetOrgDeviceLogic webGetOrgDeviceLogic;
    private final WebGetOrganizationLogic webGetOrganizationLogic;
    private final WebGetOrganizationsLogic webGetOrganizationsLogic;
    private final WebGetOrgUsersLogic webGetOrgUsersLogic;
    private final WebGetProductLocationsLogic webGetProductLocationsLogic;
    private final WebCanInviteUserLogic webCanInviteUserLogic;
    private final WebCreateProductLogic webCreateProductLogic;
    private final WebGetProductsLogic webGetProductsLogic;
    private final WebDeleteProductLogic webDeleteProductLogic;
    private final WebEditOrgDeviceLogic webEditOrgDeviceLogic;
    private final WebEditDevicesMetaInProductLogic webEditDevicesMetaInProductLogic;
    private final WebEditUserInfoLogic webEditUserInfoLogic;
    private final WebDeleteUserLogic webDeleteUserLogic;
    private final WebCreateOrganizationLogic webCreateOrganizationLogic;
    private final WebEditOrganizationLogic webEditOrganizationLogic;
    private final WebDeleteOrganizationLogic webDeleteOrganizationLogic;
    private final WebCanDeleteProductLogic webCanDeleteProductLogic;
    private final WebInviteUserLogic webInviteUserLogic;
    private final WebGetDeviceTimelineLogic webGetDeviceTimelineLogic;
    private final WebDeleteOrgDeviceLogic webDeleteOrgDeviceLogic;
    private final WebGetOrganizationsHierarchyLogic webGetOrganizationsHierarchyLogic;
    private final WebCreateRoleLogic webCreateRoleLogic;
    private final WebEditRoleLogic webEditRoleLogic;
    private final WebGetRolesLogic webGetRolesLogic;
    private final WebDeleteRoleLogic webDeleteRoleLogic;
    private final WebGetRoleLogic webGetRoleLogic;
    private final WebGetOrgDevicesLogic webGetOrgDevicesLogic;
    private final WebGetProductLogic webGetProductLogic;
    private final WebEditProductLogic webEditProductLogic;
    private final WebSetAuthTokenForDeviceLogic webSetAuthTokenForDeviceLogic;
    private final WebEditOwnOrganizationLogic webEditOwnOrganizationLogic;
    private final WebGetFirmwareInfoLogic webGetFirmwareInfoLogic;
    private final WebStartOtaLogic webStartOtaLogic;
    private final WebStopOtaLogic webStopOtaLogic;
    private final WebDeleteShipmentLogic webCleanOtaLogic;
    private final WebTrackOrganizationLogic webTrackOrganizationLogic;
    private final WebEditDeviceMetafieldLogic webEditDeviceMetafieldLogic;
    private final WebTrackDeviceLogic webTrackDeviceLogic;
    private final WebGetDeviceCountLogic webGetDeviceCountLogic;
    private final WebGetDeviceCountFromProductIDLogic webGetDeviceCountFromProductIDLogic;
    private final WebGetUserCountersByRoleLogic webGetUserCountersByRoleLogic;
    private final WebGetRuleGroupLogic webGetRuleGroupLogic;
    private final WebEditRuleGroupLogic webEditRuleGroupLogic;
    private final WebGetOrgShipmentsLogic webGetOrgShipmentsLogic;
    private final WebGetMetaFieldLogic webGetMetaFieldLogic;
    private final WebGetTempSecureTokenLogic webGetTempSecureTokenLogic;

    private final Holder holder;

    public WebAppHandler(Holder holder, WebAppStateHolder state) {
        super(StringMessage.class);
        this.webAppControlHardwareLogic = new WebAppControlHardwareLogic(holder);
        this.webGetGraphDataLogic = new WebGetGraphDataLogic(holder);
        this.webResolveLogEventLogic = new WebResolveLogEventLogic(holder);
        this.webCreateOrgDeviceLogic = new WebCreateOrgDeviceLogic(holder);
        this.webGetOrgDeviceLogic = new WebGetOrgDeviceLogic(holder);
        this.webGetOrganizationLogic = new WebGetOrganizationLogic(holder);
        this.webGetOrganizationsLogic = new WebGetOrganizationsLogic(holder);
        this.webGetOrgUsersLogic = new WebGetOrgUsersLogic(holder);
        this.webGetProductLocationsLogic = new WebGetProductLocationsLogic(holder);
        this.webCanInviteUserLogic = new WebCanInviteUserLogic(holder);
        this.webCreateProductLogic = new WebCreateProductLogic(holder);
        this.webGetProductsLogic = new WebGetProductsLogic(holder);
        this.webDeleteProductLogic = new WebDeleteProductLogic(holder);
        this.webEditOrgDeviceLogic = new WebEditOrgDeviceLogic(holder);
        this.webEditDevicesMetaInProductLogic = new WebEditDevicesMetaInProductLogic(holder);
        this.webEditUserInfoLogic = new WebEditUserInfoLogic(holder);
        this.webDeleteUserLogic = new WebDeleteUserLogic(holder);
        this.webCreateOrganizationLogic = new WebCreateOrganizationLogic(holder);
        this.webEditOrganizationLogic = new WebEditOrganizationLogic(holder);
        this.webDeleteOrganizationLogic = new WebDeleteOrganizationLogic(holder);
        this.webCanDeleteProductLogic = new WebCanDeleteProductLogic(holder);
        this.webInviteUserLogic = new WebInviteUserLogic(holder);
        this.webGetDeviceTimelineLogic = new WebGetDeviceTimelineLogic(holder);
        this.webDeleteOrgDeviceLogic = new WebDeleteOrgDeviceLogic(holder);
        this.webGetOrganizationsHierarchyLogic = new WebGetOrganizationsHierarchyLogic(holder);
        this.webCreateRoleLogic = new WebCreateRoleLogic(holder);
        this.webEditRoleLogic = new WebEditRoleLogic(holder);
        this.webGetRolesLogic = new WebGetRolesLogic(holder);
        this.webDeleteRoleLogic = new WebDeleteRoleLogic(holder);
        this.webGetRoleLogic = new WebGetRoleLogic(holder);
        this.webGetOrgDevicesLogic = new WebGetOrgDevicesLogic(holder);
        this.webGetProductLogic = new WebGetProductLogic(holder);
        this.webEditProductLogic = new WebEditProductLogic(holder);
        this.webSetAuthTokenForDeviceLogic = new WebSetAuthTokenForDeviceLogic(holder);
        this.webEditOwnOrganizationLogic = new WebEditOwnOrganizationLogic(holder);
        this.webGetFirmwareInfoLogic = new WebGetFirmwareInfoLogic(holder);
        this.webStartOtaLogic = new WebStartOtaLogic(holder);
        this.webStopOtaLogic = new WebStopOtaLogic(holder);
        this.webCleanOtaLogic = new WebDeleteShipmentLogic(holder);
        this.webTrackOrganizationLogic = new WebTrackOrganizationLogic(holder);
        this.webEditDeviceMetafieldLogic = new WebEditDeviceMetafieldLogic(holder);
        this.webTrackDeviceLogic = new WebTrackDeviceLogic(holder);
        this.webGetDeviceCountLogic = new WebGetDeviceCountLogic(holder);
        this.webGetDeviceCountFromProductIDLogic = new WebGetDeviceCountFromProductIDLogic(holder);
        this.webGetUserCountersByRoleLogic = new WebGetUserCountersByRoleLogic(holder);
        this.webGetRuleGroupLogic = new WebGetRuleGroupLogic(holder);
        this.webEditRuleGroupLogic = new WebEditRuleGroupLogic(holder);
        this.webGetOrgShipmentsLogic = new WebGetOrgShipmentsLogic(holder);
        this.webGetMetaFieldLogic = new WebGetMetaFieldLogic(holder);
        this.webGetTempSecureTokenLogic = new WebGetTempSecureTokenLogic(holder);

        this.state = state;
        this.holder = holder;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.holder.stats.incrementWebStat();
        switch (msg.command) {
            case WEB_GET_ACCOUNT:
                WebGetAccountLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ACCOUNT:
                WebEditAccountLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE :
                webAppControlHardwareLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_TRACK_DEVICE:
                webTrackDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case GET_SUPERCHART_DATA:
                webGetGraphDataLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_RESOLVE_EVENT:
                webResolveLogEventLogic.messageReceived(ctx, state, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case WEB_CREATE_DEVICE :
                webCreateOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICE:
                webEditOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES :
                webGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE :
                webGetOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG :
                webGetOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORGS :
                webGetOrganizationsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG_USERS :
                webGetOrgUsersLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCT_LOCATIONS:
                webGetProductLocationsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CAN_INVITE_USER :
                webCanInviteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_PRODUCT :
                webCreateProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCT :
                webGetProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_PRODUCTS :
                webGetProductsLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_PRODUCT:
                webEditProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_PRODUCT :
                webDeleteProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICES_META_IN_PRODUCT:
                webEditDevicesMetaInProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_USER_INFO:
                webEditUserInfoLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_USER :
                webDeleteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_ORG :
                webCreateOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ORG:
                webEditOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_ORG :
                webDeleteOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CAN_DELETE_PRODUCT :
                webCanDeleteProductLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_INVITE_USER :
                webInviteUserLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_DEVICE_METAFIELD:
                webEditDeviceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_TIMELINE :
                webGetDeviceTimelineLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_DEVICE :
                webDeleteOrgDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_METAFIELD :
                webGetMetaFieldLogic.messageReceived(ctx, state, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case WEB_GET_TEMP_SECURE_TOKEN :
                webGetTempSecureTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case WEB_GET_ORG_HIERARCHY :
                webGetOrganizationsHierarchyLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_CREATE_ROLE :
                webCreateRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_ROLE:
                webEditRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_DELETE_ROLE :
                webDeleteRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ROLES :
                webGetRolesLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ROLE :
                webGetRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SET_AUTH_TOKEN :
                webSetAuthTokenForDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_OWN_ORG :
                webEditOwnOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_COUNT_FOR_ORG :
                webGetDeviceCountLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICE_COUNT_FOR_PRODUCT_AND_SUBPRODUCTS :
                webGetDeviceCountFromProductIDLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_OTA_GET_FIRMWARE_INFO :
                webGetFirmwareInfoLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_OTA_START :
                webStartOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_OTA_STOP :
                webStopOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_SHIPMENT_DELETE:
                webCleanOtaLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_TRACK_ORG :
                webTrackOrganizationLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_USER_COUNTERS_BY_ROLE :
                webGetUserCountersByRoleLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_DEVICES_BY_REFERENCE_METAFIELD :
                CommonGetDevicesByReferenceMetafieldLogic.messageReceived(holder, ctx, state, msg);
                break;
            case WEB_GET_RULE_GROUP :
                webGetRuleGroupLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_EDIT_RULE_GROUP :
                webEditRuleGroupLogic.messageReceived(ctx, state, msg);
                break;
            case WEB_GET_ORG_SHIPMENTS :
                webGetOrgShipmentsLogic.messageReceived(ctx, state, msg);
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
