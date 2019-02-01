package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.logic.DashSyncLogic;
import cc.blynk.server.application.handlers.main.logic.DeviceSyncLogic;
import cc.blynk.server.application.handlers.main.logic.MobileActivateDashboardLogic;
import cc.blynk.server.application.handlers.main.logic.MobileAddPushLogic;
import cc.blynk.server.application.handlers.main.logic.MobileAssignTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileDeActivateDashboardLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetCloneCodeLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetEnergyLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetProjectByClonedTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetProjectByTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetProvisionTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileHardwareGroupLogic;
import cc.blynk.server.application.handlers.main.logic.MobileHardwareLogic;
import cc.blynk.server.application.handlers.main.logic.MobileHardwareResendFromBTLogic;
import cc.blynk.server.application.handlers.main.logic.MobileLoadProfileGzippedLogic;
import cc.blynk.server.application.handlers.main.logic.MobileLogoutLogic;
import cc.blynk.server.application.handlers.main.logic.MobileMailLogic;
import cc.blynk.server.application.handlers.main.logic.MobilePurchaseLogic;
import cc.blynk.server.application.handlers.main.logic.MobileRedeemLogic;
import cc.blynk.server.application.handlers.main.logic.MobileRefreshTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileSetWidgetPropertyLogic;
import cc.blynk.server.application.handlers.main.logic.MobileUpdateProfileSettingLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileCreateDashLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileDeleteDashLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileUpdateDashLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileUpdateDashSettingLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileCreateDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileDeleteDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileGetDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileGetOrgDevicesLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileUpdateDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileUpdateDeviceMetafieldLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileCreateWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileDeleteWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileGetWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileUpdateWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileCreateTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileDeleteTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileUpdateTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileCreateAppLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileDeleteAppLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileMailQRsLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileUpdateAppLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileUpdateFaceLogic;
import cc.blynk.server.application.handlers.main.logic.graph.MobileDeleteOrgDeviceDataLogic;
import cc.blynk.server.application.handlers.main.logic.graph.MobileDeleteSuperChartDataLogic;
import cc.blynk.server.application.handlers.main.logic.graph.MobileGetSuperChartDataLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileCreateReportLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileDeleteReportLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileExportReportLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileUpdateReportLogic;
import cc.blynk.server.application.handlers.main.logic.sharing.MobileGetShareTokenLogic;
import cc.blynk.server.application.handlers.main.logic.sharing.MobileRefreshShareTokenLogic;
import cc.blynk.server.application.handlers.main.logic.sharing.MobileShareLogic;
import cc.blynk.server.common.JsonBasedSimpleChannelInboundHandler;
import cc.blynk.server.common.handlers.CommonGetDevicesByReferenceMetafieldLogic;
import cc.blynk.server.common.handlers.logic.PingLogic;
import cc.blynk.server.common.handlers.logic.timeline.WebGetDeviceTimelineLogic;
import cc.blynk.server.common.handlers.logic.timeline.WebResolveLogEventLogic;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.core.session.mobile.MobileStateHolder;
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
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_REPORT;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_TILE_TEMPLATE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_CREATE_WIDGET;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DEACTIVATE_DASHBOARD;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_APP;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DASH;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_DEVICE_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_GRAPH_DATA;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_DELETE_REPORT;
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
public class MobileHandler extends JsonBasedSimpleChannelInboundHandler<StringMessage, MobileStateHolder> {

    public final MobileStateHolder state;
    private final Holder holder;
    private final MobileHardwareLogic hardwareLogic;
    private final MobileHardwareGroupLogic mobileHardwareGroupLogic;

    private MobileHardwareResendFromBTLogic hardwareResendFromBTLogic;
    private MobileMailLogic mailLogic;
    private MobilePurchaseLogic purchaseLogic;
    private MobileDeleteAppLogic deleteAppLogic;
    private MobileMailQRsLogic mailQRsLogic;
    private MobileGetProjectByClonedTokenLogic getProjectByCloneCodeLogic;
    private final MobileGetOrgDevicesLogic mobileGetOrgDevicesLogic;
    private final MobileDeleteOrgDeviceDataLogic mobileDeleteOrgDeviceDataLogic;
    private final MobileLoadProfileGzippedLogic mobileLoadProfileGzippedLogic;
    private final MobileGetWidgetLogic mobileGetWidgetLogic;
    private final WebGetDeviceTimelineLogic webGetDeviceTimelineLogic;
    private final WebResolveLogEventLogic webResolveLogEventLogic;
    private final MobileGetSuperChartDataLogic mobileGetSuperChartDataLogic;

    public MobileHandler(Holder holder, MobileStateHolder state) {
        super(StringMessage.class);
        this.state = state;
        this.holder = holder;

        this.hardwareLogic = new MobileHardwareLogic(holder);
        this.mobileHardwareGroupLogic = new MobileHardwareGroupLogic(holder);
        this.mobileGetOrgDevicesLogic = new MobileGetOrgDevicesLogic(holder);
        this.mobileLoadProfileGzippedLogic = new MobileLoadProfileGzippedLogic(holder);
        this.mobileGetWidgetLogic = new MobileGetWidgetLogic(holder);
        this.webGetDeviceTimelineLogic = new WebGetDeviceTimelineLogic(holder);
        this.webResolveLogEventLogic = new WebResolveLogEventLogic(holder);
        this.mobileGetSuperChartDataLogic = new MobileGetSuperChartDataLogic(holder);
        this.mobileDeleteOrgDeviceDataLogic = new MobileDeleteOrgDeviceDataLogic(holder);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        holder.stats.incrementAppStat();
        switch (msg.command) {
            case HARDWARE :
                hardwareLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_HARDWARE_GROUP :
                mobileHardwareGroupLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE_RESEND_FROM_BLUETOOTH :
                if (hardwareResendFromBTLogic == null) {
                    this.hardwareResendFromBTLogic = new MobileHardwareResendFromBTLogic(holder);
                }
                hardwareResendFromBTLogic.messageReceived(state, msg);
                break;
            case MOBILE_ACTIVATE_DASHBOARD :
                MobileActivateDashboardLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_DEACTIVATE_DASHBOARD :
                MobileDeActivateDashboardLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_LOAD_PROFILE_GZIPPED :
                mobileLoadProfileGzippedLogic.messageReceived(ctx, state, msg);
                break;
            case SHARING :
                MobileShareLogic.messageReceived(holder, ctx, state, msg);
                break;

            case ASSIGN_TOKEN :
                MobileAssignTokenLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_ADD_PUSH_TOKEN :
                MobileAddPushLogic.messageReceived(ctx, state, msg);
                break;
            case REFRESH_TOKEN :
                MobileRefreshTokenLogic.messageReceived(holder, ctx, state, msg);
                break;

            case GET_SUPERCHART_DATA :
                mobileGetSuperChartDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GRAPH_DATA:
                MobileDeleteSuperChartDataLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;

            case GET_SHARE_TOKEN :
                MobileGetShareTokenLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case REFRESH_SHARE_TOKEN :
                MobileRefreshShareTokenLogic.messageReceived(holder, ctx, state, msg);
                break;

            case EMAIL :
                if (mailLogic == null) {
                    this.mailLogic = new MobileMailLogic(holder);
                }
                mailLogic.messageReceived(ctx, state.user, msg);
                break;

            case MOBILE_CREATE_DASH :
                MobileCreateDashLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_EDIT_DASH :
                MobileUpdateDashLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_DELETE_DASH :
                MobileDeleteDashLogic.messageReceived(holder, ctx, state, msg);
                break;

            case MOBILE_CREATE_WIDGET :
                MobileCreateWidgetLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_EDIT_WIDGET :
                MobileUpdateWidgetLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_DELETE_WIDGET :
                MobileDeleteWidgetLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_GET_WIDGET :
                mobileGetWidgetLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_TILE_TEMPLATE :
                MobileCreateTileTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_TILE_TEMPLATE :
                MobileUpdateTileTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_TILE_TEMPLATE :
                MobileDeleteTileTemplateLogic.messageReceived(holder, ctx, state, msg);
                break;

            case REDEEM :
                MobileRedeemLogic.messageReceived(holder, ctx, state.user, msg);
                break;

            case MOBILE_GET_ENERGY :
                MobileGetEnergyLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_ADD_ENERGY :
                if (purchaseLogic == null) {
                    this.purchaseLogic = new MobilePurchaseLogic(holder);
                }
                purchaseLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_EDIT_PROJECT_SETTINGS :
                MobileUpdateDashSettingLogic.messageReceived(ctx, state, msg, holder.limits.widgetSizeLimitBytes);
                break;

            case MOBILE_CREATE_DEVICE :
                MobileCreateDeviceLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_EDIT_DEVICE :
                MobileUpdateDeviceLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_DELETE_DEVICE :
                MobileDeleteDeviceLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES :
                mobileGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DEVICE_METAFIELD :
                MobileUpdateDeviceMetafieldLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD :
                CommonGetDevicesByReferenceMetafieldLogic.messageReceived(holder, ctx, state, msg);
                break;
            case MOBILE_GET_DEVICE :
                MobileGetDeviceLogic.messageReceived(holder, ctx, msg);
                break;

            case DEVICE_SYNC :
                DeviceSyncLogic.messageReceived(holder, ctx, msg);
                break;
            case DASH_SYNC :
                DashSyncLogic.messageReceived(holder, ctx, state, msg);
                break;

            case MOBILE_CREATE_APP :
                MobileCreateAppLogic.messageReceived(ctx, state, msg, holder.limits.widgetSizeLimitBytes);
                break;
            case MOBILE_EDIT_APP :
                MobileUpdateAppLogic.messageReceived(ctx, state, msg, holder.limits.widgetSizeLimitBytes);
                break;
            case MOBILE_DELETE_APP :
                if (deleteAppLogic == null) {
                    this.deleteAppLogic = new MobileDeleteAppLogic(holder);
                }
                deleteAppLogic.messageReceived(ctx, state, msg);
                break;

            case GET_PROJECT_BY_TOKEN :
                MobileGetProjectByTokenLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case EMAIL_QR :
                if (mailQRsLogic == null) {
                    this.mailQRsLogic = new MobileMailQRsLogic(holder);
                }
                mailQRsLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_FACE :
                MobileUpdateFaceLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case GET_CLONE_CODE :
                MobileGetCloneCodeLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case GET_PROJECT_BY_CLONE_CODE :
                if (getProjectByCloneCodeLogic == null) {
                    this.getProjectByCloneCodeLogic = new MobileGetProjectByClonedTokenLogic(holder);
                }
                getProjectByCloneCodeLogic.messageReceived(ctx, state.user, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case SET_WIDGET_PROPERTY :
                MobileSetWidgetPropertyLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_GET_PROVISION_TOKEN :
                MobileGetProvisionTokenLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_DELETE_DEVICE_DATA :
                mobileDeleteOrgDeviceDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_REPORT :
                MobileCreateReportLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_EDIT_REPORT :
                MobileUpdateReportLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_DELETE_REPORT :
                MobileDeleteReportLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_EXPORT_REPORT :
                MobileExportReportLogic.messageReceived(holder, ctx, state.user, msg);
                break;
            case MOBILE_EDIT_PROFILE_SETTINGS :
                MobileUpdateProfileSettingLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICE_TIMELINE :
                webGetDeviceTimelineLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_RESOLVE_DEVICE_TIMELINE :
                webResolveLogEventLogic.messageReceived(ctx, state, msg);
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
