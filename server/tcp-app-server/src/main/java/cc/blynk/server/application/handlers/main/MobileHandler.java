package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.logic.DashSyncLogic;
import cc.blynk.server.application.handlers.main.logic.DeviceSyncLogic;
import cc.blynk.server.application.handlers.main.logic.MobileActivateDashboardLogic;
import cc.blynk.server.application.handlers.main.logic.MobileAddPushLogic;
import cc.blynk.server.application.handlers.main.logic.MobileAssignTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileDeActivateDashboardLogic;
import cc.blynk.server.application.handlers.main.logic.MobileEditProfileSettingLogic;
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
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileCreateDashLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileDeleteDashLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileEditDashLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.MobileEditDashSettingLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileCreateDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileDeleteDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileEditDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileEditDeviceMetafieldLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileGetDeviceLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.device.MobileGetOrgDevicesLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileCreateWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileDeleteWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileEditWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.MobileGetWidgetLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.MobileCreateGroupLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.MobileDeleteGroupLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.MobileEditGroupLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.template.MobileCreateGroupTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.template.MobileDeleteGroupTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.group.template.MobileEditGroupTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileCreateTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileDeleteTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileEditTileTemplateLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileCreateAppLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileDeleteAppLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileEditAppLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileEditFaceLogic;
import cc.blynk.server.application.handlers.main.logic.face.MobileMailQRsLogic;
import cc.blynk.server.application.handlers.main.logic.graph.MobileDeleteOrgDeviceDataLogic;
import cc.blynk.server.application.handlers.main.logic.graph.MobileDeleteSuperChartDataLogic;
import cc.blynk.server.application.handlers.main.logic.graph.MobileGetSuperChartDataLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileCreateReportLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileDeleteReportLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileEditReportLogic;
import cc.blynk.server.application.handlers.main.logic.reporting.MobileExportReportLogic;
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
    private final MobileHardwareLogic hardwareLogic;
    private final MobileHardwareGroupLogic mobileHardwareGroupLogic;

    private final MobileHardwareResendFromBTLogic hardwareResendFromBTLogic;
    private final MobileMailLogic mobileMailLogic;
    private final MobilePurchaseLogic purchaseLogic;
    private final MobileDeleteAppLogic deleteAppLogic;
    private final MobileMailQRsLogic mailQRsLogic;
    private final MobileGetProjectByClonedTokenLogic getProjectByCloneCodeLogic;
    private final MobileGetOrgDevicesLogic mobileGetOrgDevicesLogic;
    private final MobileDeleteOrgDeviceDataLogic mobileDeleteOrgDeviceDataLogic;
    private final MobileLoadProfileGzippedLogic mobileLoadProfileGzippedLogic;
    private final MobileGetWidgetLogic mobileGetWidgetLogic;
    private final WebGetDeviceTimelineLogic webGetDeviceTimelineLogic;
    private final WebResolveLogEventLogic webResolveLogEventLogic;
    private final MobileGetSuperChartDataLogic mobileGetSuperChartDataLogic;
    private final MobileCreateGroupTemplateLogic mobileCreateGroupTemplateLogic;
    private final MobileEditGroupTemplateLogic mobileEditGroupTemplateLogic;
    private final MobileDeleteGroupTemplateLogic mobileDeleteGroupTemplateLogic;
    private final MobileCreateGroupLogic mobileCreateGroupLogic;
    private final MobileEditGroupLogic mobileEditGroupLogic;
    private final MobileDeleteGroupLogic mobileDeleteGroupLogic;
    private final CommonGetDevicesByReferenceMetafieldLogic commonGetDevicesByReferenceMetafieldLogic;
    private final MobileCreateReportLogic mobileCreateReportLogic;
    private final MobileEditReportLogic mobileEditReportLogic;
    private final MobileDeleteReportLogic mobileDeleteReportLogic;
    private final MobileExportReportLogic mobileExportReportLogic;
    private final MobileGetProvisionTokenLogic mobileGetProvisionTokenLogic;
    private final MobileGetCloneCodeLogic mobileGetCloneCodeLogic;
    private final MobileEditFaceLogic mobileEditFaceLogic;
    private final MobileGetProjectByTokenLogic mobileGetProjectByTokenLogic;
    private final DashSyncLogic dashSyncLogic;
    private final DeviceSyncLogic deviceSyncLogic;
    private final MobileGetDeviceLogic mobileGetDeviceLogic;
    private final MobileEditDeviceMetafieldLogic mobileEditDeviceMetafieldLogic;
    private final MobileDeleteDeviceLogic mobileDeleteDeviceLogic;
    private final MobileEditDeviceLogic mobileEditDeviceLogic;
    private final MobileCreateDeviceLogic mobileCreateDeviceLogic;
    private final MobileRedeemLogic mobileRedeemLogic;
    private final MobileDeleteTileTemplateLogic mobileDeleteTileTemplateLogic;
    private final MobileDeleteWidgetLogic mobileDeleteWidgetLogic;
    private final MobileEditWidgetLogic mobileEditWidgetLogic;
    private final MobileCreateWidgetLogic mobileCreateWidgetLogic;
    private final MobileDeleteDashLogic mobileDeleteDashLogic;
    private final MobileEditDashLogic mobileEditDashLogic;
    private final MobileCreateDashLogic mobileCreateDashLogic;
    private final MobileRefreshShareTokenLogic mobileRefreshShareTokenLogic;
    private final MobileGetShareTokenLogic mobileGetShareTokenLogic;
    private final MobileDeleteSuperChartDataLogic mobileDeleteSuperChartDataLogic;
    private final MobileRefreshTokenLogic mobileRefreshTokenLogic;
    private final MobileAssignTokenLogic mobileAssignTokenLogic;
    private final MobileShareLogic mobileShareLogic;
    private final MobileDeActivateDashboardLogic mobileDeActivateDashboardLogic;
    private final MobileActivateDashboardLogic mobileActivateDashboardLogic;
    private final MobileEditAppLogic mobileEditAppLogic;
    private final MobileEditDashSettingLogic mobileEditDashSettingLogic;
    private final MobileCreateAppLogic mobileCreateAppLogic;

    public MobileHandler(Holder holder, MobileStateHolder state) {
        super(StringMessage.class);
        this.state = state;
        this.stats = holder.stats;

        this.hardwareLogic = new MobileHardwareLogic(holder);
        this.mobileHardwareGroupLogic = new MobileHardwareGroupLogic(holder);
        this.mobileGetOrgDevicesLogic = new MobileGetOrgDevicesLogic(holder);
        this.mobileLoadProfileGzippedLogic = new MobileLoadProfileGzippedLogic(holder);
        this.mobileGetWidgetLogic = new MobileGetWidgetLogic(holder);
        this.webGetDeviceTimelineLogic = new WebGetDeviceTimelineLogic(holder);
        this.webResolveLogEventLogic = new WebResolveLogEventLogic(holder);
        this.mobileGetSuperChartDataLogic = new MobileGetSuperChartDataLogic(holder);
        this.mobileDeleteOrgDeviceDataLogic = new MobileDeleteOrgDeviceDataLogic(holder);
        this.mobileCreateGroupTemplateLogic = new MobileCreateGroupTemplateLogic(holder);
        this.mobileEditGroupTemplateLogic = new MobileEditGroupTemplateLogic();
        this.mobileDeleteGroupTemplateLogic = new MobileDeleteGroupTemplateLogic(holder);
        this.mobileCreateGroupLogic = new MobileCreateGroupLogic();
        this.mobileEditGroupLogic = new MobileEditGroupLogic();
        this.mobileDeleteGroupLogic = new MobileDeleteGroupLogic();
        this.commonGetDevicesByReferenceMetafieldLogic = new CommonGetDevicesByReferenceMetafieldLogic(holder);
        this.mobileCreateReportLogic = new MobileCreateReportLogic(holder);
        this.mobileEditReportLogic = new MobileEditReportLogic(holder);
        this.mobileDeleteReportLogic = new MobileDeleteReportLogic(holder);
        this.mobileExportReportLogic = new MobileExportReportLogic(holder);
        this.mobileGetProvisionTokenLogic = new MobileGetProvisionTokenLogic(holder);
        this.getProjectByCloneCodeLogic = new MobileGetProjectByClonedTokenLogic(holder);
        this.mobileGetCloneCodeLogic = new MobileGetCloneCodeLogic(holder);
        this.mobileEditFaceLogic = new MobileEditFaceLogic(holder);
        this.mailQRsLogic = new MobileMailQRsLogic(holder);
        this.mobileGetProjectByTokenLogic = new MobileGetProjectByTokenLogic(holder);
        this.deleteAppLogic = new MobileDeleteAppLogic(holder);
        this.dashSyncLogic = new DashSyncLogic(holder);
        this.deviceSyncLogic = new DeviceSyncLogic(holder);
        this.mobileGetDeviceLogic = new MobileGetDeviceLogic(holder);
        this.mobileEditDeviceMetafieldLogic = new MobileEditDeviceMetafieldLogic(holder);
        this.mobileDeleteDeviceLogic = new MobileDeleteDeviceLogic(holder);
        this.mobileEditDeviceLogic = new MobileEditDeviceLogic(holder);
        this.mobileCreateDeviceLogic = new MobileCreateDeviceLogic(holder);
        this.purchaseLogic = new MobilePurchaseLogic(holder);
        this.mobileRedeemLogic = new MobileRedeemLogic(holder);
        this.mobileDeleteTileTemplateLogic = new MobileDeleteTileTemplateLogic(holder);
        this.mobileDeleteWidgetLogic = new MobileDeleteWidgetLogic(holder);
        this.mobileEditWidgetLogic = new MobileEditWidgetLogic(holder);
        this.mobileCreateWidgetLogic = new MobileCreateWidgetLogic(holder);
        this.mobileDeleteDashLogic = new MobileDeleteDashLogic(holder);
        this.mobileEditDashLogic = new MobileEditDashLogic(holder);
        this.mobileCreateDashLogic = new MobileCreateDashLogic(holder);
        this.mobileMailLogic = new MobileMailLogic(holder);
        this.mobileRefreshShareTokenLogic = new MobileRefreshShareTokenLogic(holder);
        this.mobileGetShareTokenLogic = new MobileGetShareTokenLogic(holder);
        this.mobileDeleteSuperChartDataLogic = new MobileDeleteSuperChartDataLogic(holder);
        this.mobileRefreshTokenLogic = new MobileRefreshTokenLogic(holder);
        this.mobileAssignTokenLogic = new MobileAssignTokenLogic(holder);
        this.mobileShareLogic = new MobileShareLogic(holder);
        this.mobileDeActivateDashboardLogic = new MobileDeActivateDashboardLogic(holder);
        this.mobileActivateDashboardLogic = new MobileActivateDashboardLogic(holder);
        this.hardwareResendFromBTLogic = new MobileHardwareResendFromBTLogic(holder);
        this.mobileEditAppLogic = new MobileEditAppLogic(holder);
        this.mobileEditDashSettingLogic = new MobileEditDashSettingLogic(holder);
        this.mobileCreateAppLogic = new MobileCreateAppLogic(holder);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementAppStat();
        switch (msg.command) {
            case HARDWARE :
                hardwareLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_HARDWARE_GROUP :
                mobileHardwareGroupLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE_RESEND_FROM_BLUETOOTH :
                hardwareResendFromBTLogic.messageReceived(state, msg);
                break;
            case MOBILE_ACTIVATE_DASHBOARD :
                mobileActivateDashboardLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DEACTIVATE_DASHBOARD :
                mobileDeActivateDashboardLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_LOAD_PROFILE_GZIPPED :
                mobileLoadProfileGzippedLogic.messageReceived(ctx, state, msg);
                break;
            case SHARING :
                mobileShareLogic.messageReceived(ctx, state, msg);
                break;

            case ASSIGN_TOKEN :
                mobileAssignTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_ADD_PUSH_TOKEN :
                MobileAddPushLogic.messageReceived(ctx, state, msg);
                break;
            case REFRESH_TOKEN :
                mobileRefreshTokenLogic.messageReceived(ctx, state, msg);
                break;

            case GET_SUPERCHART_DATA :
                mobileGetSuperChartDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GRAPH_DATA:
                mobileDeleteSuperChartDataLogic.messageReceived(ctx, state.user, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;

            case GET_SHARE_TOKEN :
                mobileGetShareTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case REFRESH_SHARE_TOKEN :
                mobileRefreshShareTokenLogic.messageReceived(ctx, state, msg);
                break;

            case EMAIL :
                mobileMailLogic.messageReceived(ctx, state.user, msg);
                break;

            case MOBILE_CREATE_DASH :
                mobileCreateDashLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DASH :
                mobileEditDashLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_DASH :
                mobileDeleteDashLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_WIDGET :
                mobileCreateWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_WIDGET :
                mobileEditWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_WIDGET :
                mobileDeleteWidgetLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_WIDGET :
                mobileGetWidgetLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_TILE_TEMPLATE :
                MobileCreateTileTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_TILE_TEMPLATE :
                MobileEditTileTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_TILE_TEMPLATE :
                mobileDeleteTileTemplateLogic.messageReceived(ctx, state, msg);
                break;

            case REDEEM :
                mobileRedeemLogic.messageReceived(ctx, state.user, msg);
                break;

            case MOBILE_GET_ENERGY :
                MobileGetEnergyLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_ADD_ENERGY :
                purchaseLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_EDIT_PROJECT_SETTINGS :
                mobileEditDashSettingLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_DEVICE :
                mobileCreateDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DEVICE :
                mobileEditDeviceLogic.messageReceived(ctx, msg);
                break;
            case MOBILE_DELETE_DEVICE :
                mobileDeleteDeviceLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES :
                mobileGetOrgDevicesLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_DEVICE_METAFIELD :
                mobileEditDeviceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICES_BY_REFERENCE_METAFIELD :
                commonGetDevicesByReferenceMetafieldLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICE :
                mobileGetDeviceLogic.messageReceived(ctx, msg);
                break;

            case DEVICE_SYNC :
                deviceSyncLogic.messageReceived(ctx, msg);
                break;
            case DASH_SYNC :
                dashSyncLogic.messageReceived(ctx, state, msg);
                break;

            case MOBILE_CREATE_APP :
                mobileCreateAppLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_APP :
                mobileEditAppLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_APP :
                deleteAppLogic.messageReceived(ctx, state, msg);
                break;

            case GET_PROJECT_BY_TOKEN :
                mobileGetProjectByTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case EMAIL_QR :
                mailQRsLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_FACE :
                mobileEditFaceLogic.messageReceived(ctx, state.user, msg);
                break;
            case GET_CLONE_CODE :
                mobileGetCloneCodeLogic.messageReceived(ctx, state.user, msg);
                break;
            case GET_PROJECT_BY_CLONE_CODE :
                getProjectByCloneCodeLogic.messageReceived(ctx, state.user, msg);
                break;
            case LOGOUT :
                MobileLogoutLogic.messageReceived(ctx, state.user, msg);
                break;
            case SET_WIDGET_PROPERTY :
                MobileSetWidgetPropertyLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_GET_PROVISION_TOKEN :
                mobileGetProvisionTokenLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_DELETE_DEVICE_DATA :
                mobileDeleteOrgDeviceDataLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_REPORT :
                mobileCreateReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_REPORT :
                mobileEditReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_DELETE_REPORT :
                mobileDeleteReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EXPORT_REPORT :
                mobileExportReportLogic.messageReceived(ctx, state.user, msg);
                break;
            case MOBILE_EDIT_PROFILE_SETTINGS :
                MobileEditProfileSettingLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_GET_DEVICE_TIMELINE :
                webGetDeviceTimelineLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_RESOLVE_DEVICE_TIMELINE :
                webResolveLogEventLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_GROUP_TEMPLATE :
                mobileCreateGroupTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_GROUP_TEMPLATE :
                mobileEditGroupTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GROUP_TEMPLATE :
                mobileDeleteGroupTemplateLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_CREATE_GROUP :
                mobileCreateGroupLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_EDIT_GROUP :
                mobileEditGroupLogic.messageReceived(ctx, state, msg);
                break;
            case MOBILE_DELETE_GROUP :
                mobileDeleteGroupLogic.messageReceived(ctx, state, msg);
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
