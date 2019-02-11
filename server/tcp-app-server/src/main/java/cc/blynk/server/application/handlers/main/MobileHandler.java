package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.logic.MobileAddPushLogic;
import cc.blynk.server.application.handlers.main.logic.MobileEditProfileSettingLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetEnergyLogic;
import cc.blynk.server.application.handlers.main.logic.MobileHardwareLogic;
import cc.blynk.server.application.handlers.main.logic.MobileHardwareResendFromBTLogic;
import cc.blynk.server.application.handlers.main.logic.MobileLogoutLogic;
import cc.blynk.server.application.handlers.main.logic.MobileSetWidgetPropertyLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileCreateTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileEditTileTemplateLogic;
import cc.blynk.server.common.JsonBasedSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
import cc.blynk.server.core.stats.GlobalStats;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.server.core.protocol.enums.Command.ASSIGN_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.DASH_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.DEVICE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL;
import static cc.blynk.server.core.protocol.enums.Command.EMAIL_QR;
import static cc.blynk.server.core.protocol.enums.Command.GET_CLONE_CODE;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_CLONE_CODE;
import static cc.blynk.server.core.protocol.enums.Command.GET_PROJECT_BY_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.GET_SUPERCHART_DATA;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_RESEND_FROM_BLUETOOTH;
import static cc.blynk.server.core.protocol.enums.Command.LOGOUT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_ADD_PUSH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_GROUP_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_GROUP_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DEACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_GROUP_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_GROUP_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_DEVICE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_FACE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_GROUP_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_GROUP_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_PROFILE_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_PROJECT_SETTINGS;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EDIT_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EXPORT_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE_TIMELINE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_ENERGY;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_PROVISION_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_HARDWARE_GROUP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_LOAD_PROFILE_GZIPPED;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_RESOLVE_DEVICE_TIMELINE;
import static cc.blynk.server.core.protocol.enums.Command.PING;
import static cc.blynk.server.core.protocol.enums.Command.REDEEM;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_SHARE_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.server.core.protocol.enums.Command.SHARING;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public final class MobileHandler extends JsonBasedSimpleChannelInboundHandler<StringMessage, MobileStateHolder> {

    public final MobileStateHolder state;
    private final GlobalStats stats;
    private final MobileLogicHolder mobileLogicHolder;

    private final MobileHardwareLogic mobileHardwareLogic;

    private final MobileHardwareResendFromBTLogic mobileHardwareResendFromBTLogic;

    public MobileHandler(Holder holder, MobileLogicHolder mobileLogicHolder, MobileStateHolder state) {
        super(StringMessage.class);
        this.state = state;
        this.stats = holder.stats;
        this.mobileLogicHolder = mobileLogicHolder;

        //those handlers hold state
        this.mobileHardwareLogic = new MobileHardwareLogic(holder);
        this.mobileHardwareResendFromBTLogic = new MobileHardwareResendFromBTLogic(holder);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementAppStat();
        switch (msg.command) {
            case HARDWARE :
                mobileHardwareLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_HARDWARE_GROUP :
                mobileLogicHolder.mobileHardwareGroupLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE_RESEND_FROM_BLUETOOTH :
                mobileHardwareResendFromBTLogic.messageReceived(state, msg);
                break;
            case MOBILE_ACTIVATE_DASHBOARD :
                mobileLogicHolder.mobileActivateDashboardLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DEACTIVATE_DASHBOARD :
                mobileLogicHolder.mobileDeActivateDashboardLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_LOAD_PROFILE_GZIPPED :
                mobileLogicHolder.mobileLoadProfileGzippedLogic.messageReceived(ctx, state, msg);
                break;
            case SHARING :
                mobileLogicHolder.mobileShareLogic.messageReceived(ctx, state, msg);
                break;

            case ASSIGN_TOKEN :
                mobileLogicHolder.mobileAssignTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_ADD_PUSH_TOKEN :
                MobileAddPushLogic.messageReceived(ctx, state, msg);
                break;
            case REFRESH_TOKEN :
                mobileLogicHolder.mobileRefreshTokenLogic.messageReceived(ctx, state, msg);
                break;

            case GET_SUPERCHART_DATA :
                mobileLogicHolder.mobileGetSuperChartDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GRAPH_DATA:
                mobileLogicHolder.mobileDeleteSuperChartDataLogic.messageReceived(ctx, state.user, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;

            case GET_SHARE_TOKEN :
                mobileLogicHolder.mobileGetShareTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case REFRESH_SHARE_TOKEN :
                mobileLogicHolder.mobileRefreshShareTokenLogic.messageReceived(ctx, state, msg);
                break;

            case EMAIL :
                mobileLogicHolder.mobileMailLogic.messageReceived(ctx, state.user, msg);
                break;

            case MOBILE_CREATE_DASH :
                mobileLogicHolder.mobileCreateDashLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DASH :
                mobileLogicHolder.mobileEditDashLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_DASH :
                mobileLogicHolder.mobileDeleteDashLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_WIDGET :
                mobileLogicHolder.mobileCreateWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_WIDGET :
                mobileLogicHolder.mobileEditWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_WIDGET :
                mobileLogicHolder.mobileDeleteWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_WIDGET :
                mobileLogicHolder.mobileGetWidgetLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_TILE_TEMPLATE :
                MobileCreateTileTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_TILE_TEMPLATE :
                MobileEditTileTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_TILE_TEMPLATE :
                mobileLogicHolder.mobileDeleteTileTemplateLogic.messageReceived(ctx, state, msg);
                break;

            case REDEEM :
                mobileLogicHolder.mobileRedeemLogic.messageReceived(ctx, state.user, msg);
                break;

            case MOBILE_GET_ENERGY :
                MobileGetEnergyLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_ADD_ENERGY :
                mobileLogicHolder.mobilePurchaseLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_EDIT_PROJECT_SETTINGS :
                mobileLogicHolder.mobileEditDashSettingLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_DEVICE :
                mobileLogicHolder.mobileCreateDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DEVICE :
                mobileLogicHolder.mobileEditDeviceLogic.messageReceived(ctx, msg);
                break;
            case MOBILE_DELETE_DEVICE :
                mobileLogicHolder.mobileDeleteDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES :
                mobileLogicHolder.mobileGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DEVICE_METAFIELD :
                mobileLogicHolder.mobileEditDeviceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD :
                mobileLogicHolder.commonGetDevicesByReferenceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICE :
                mobileLogicHolder.mobileGetDeviceLogic.messageReceived(ctx, msg);
                break;

            case DEVICE_SYNC :
                mobileLogicHolder.mobileDeviceSyncLogic.messageReceived(ctx, msg);
                break;
            case DASH_SYNC :
                mobileLogicHolder.mobileDashSyncLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_APP :
                mobileLogicHolder.mobileCreateAppLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_APP :
                mobileLogicHolder.mobileEditAppLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_APP :
                mobileLogicHolder.mobileDeleteAppLogic.messageReceived(ctx, state, msg);
                break;

            case GET_PROJECT_BY_TOKEN :
                mobileLogicHolder.mobileGetProjectByTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case EMAIL_QR :
                mobileLogicHolder.mailQRsLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_FACE :
                mobileLogicHolder.mobileEditFaceLogic.messageReceived(ctx, state.user, msg);
                break;
            case GET_CLONE_CODE :
                mobileLogicHolder.mobileGetCloneCodeLogic.messageReceived(ctx, state.user, msg);
                break;
            case GET_PROJECT_BY_CLONE_CODE :
                mobileLogicHolder.mobileGetProjectByCloneCodeLogic.messageReceived(ctx, state.user, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case SET_WIDGET_PROPERTY :
                MobileSetWidgetPropertyLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_GET_PROVISION_TOKEN :
                mobileLogicHolder.mobileGetProvisionTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_DELETE_DEVICE_DATA :
                mobileLogicHolder.mobileDeleteOrgDeviceDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_REPORT :
                mobileLogicHolder.mobileCreateReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_REPORT :
                mobileLogicHolder.mobileEditReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_DELETE_REPORT :
                mobileLogicHolder.mobileDeleteReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EXPORT_REPORT :
                mobileLogicHolder.mobileExportReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_PROFILE_SETTINGS :
                MobileEditProfileSettingLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICE_TIMELINE :
                mobileLogicHolder.webGetDeviceTimelineLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_RESOLVE_DEVICE_TIMELINE :
                mobileLogicHolder.webResolveLogEventLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_GROUP_TEMPLATE :
                mobileLogicHolder.mobileCreateGroupTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_GROUP_TEMPLATE :
                mobileLogicHolder.mobileEditGroupTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GROUP_TEMPLATE :
                mobileLogicHolder.mobileDeleteGroupTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_GROUP :
                mobileLogicHolder.mobileCreateGroupLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_GROUP :
                mobileLogicHolder.mobileEditGroupLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GROUP :
                mobileLogicHolder.mobileDeleteGroupLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_GROUP_WIDGET :
                mobileLogicHolder.mobileCreateGroupWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_GROUP_WIDGET :
                mobileLogicHolder.mobileEditGroupWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GROUP_WIDGET :
                mobileLogicHolder.mobileDeleteGroupWidgetLogic.messageReceived(ctx, state, msg);
                break;
        }
    }

    @Override
    public MobileStateHolder getState() {
        return state;
    }

    @Override
    public void updateRole(Role role) {
        if (state.role.id == role.id) {
            state.setRole(role);
            log.trace("Changing mobile session role for {}.", state.user.email);
        }
    }
}
