package cc.blynk.server.application.handlers.main;

import cc.blynk.server.Holder;
import cc.blynk.server.application.handlers.main.logic.MobileActivateDashboardLogic;
import cc.blynk.server.application.handlers.main.logic.MobileAssignTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileDashSyncLogic;
import cc.blynk.server.application.handlers.main.logic.MobileDeActivateDashboardLogic;
import cc.blynk.server.application.handlers.main.logic.MobileDeviceSyncLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetCloneCodeLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetProjectByClonedTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetProjectByTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileGetProvisionTokenLogic;
import cc.blynk.server.application.handlers.main.logic.MobileHardwareGroupLogic;
import cc.blynk.server.application.handlers.main.logic.MobileLoadProfileGzippedLogic;
import cc.blynk.server.application.handlers.main.logic.MobileMailLogic;
import cc.blynk.server.application.handlers.main.logic.MobilePurchaseLogic;
import cc.blynk.server.application.handlers.main.logic.MobileRedeemLogic;
import cc.blynk.server.application.handlers.main.logic.MobileRefreshTokenLogic;
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
import cc.blynk.server.application.handlers.main.logic.dashboard.widget.tile.MobileDeleteTileTemplateLogic;
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
import cc.blynk.server.common.handlers.CommonGetDevicesByReferenceMetafieldLogic;
import cc.blynk.server.common.handlers.logic.timeline.WebGetDeviceTimelineLogic;
import cc.blynk.server.common.handlers.logic.timeline.WebResolveLogEventLogic;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.02.19.
 */
public final class MobileLogicHolder {

    public final MobileGetOrgDevicesLogic mobileGetOrgDevicesLogic;
    public final MobileDeleteOrgDeviceDataLogic mobileDeleteOrgDeviceDataLogic;
    public final MobileGetSuperChartDataLogic mobileGetSuperChartDataLogic;
    public final MobileDashSyncLogic mobileDashSyncLogic;
    public final MobileDeviceSyncLogic mobileDeviceSyncLogic;
    final MobileMailLogic mobileMailLogic;
    final MobilePurchaseLogic mobilePurchaseLogic;
    final MobileDeleteAppLogic mobileDeleteAppLogic;
    final MobileMailQRsLogic mailQRsLogic;
    final MobileGetProjectByClonedTokenLogic mobileGetProjectByCloneCodeLogic;
    final MobileLoadProfileGzippedLogic mobileLoadProfileGzippedLogic;
    final MobileGetWidgetLogic mobileGetWidgetLogic;
    final MobileCreateGroupTemplateLogic mobileCreateGroupTemplateLogic;
    final MobileEditGroupTemplateLogic mobileEditGroupTemplateLogic;
    final MobileDeleteGroupTemplateLogic mobileDeleteGroupTemplateLogic;
    final MobileCreateGroupLogic mobileCreateGroupLogic;
    final MobileEditGroupLogic mobileEditGroupLogic;
    final MobileDeleteGroupLogic mobileDeleteGroupLogic;
    final MobileCreateReportLogic mobileCreateReportLogic;
    final MobileEditReportLogic mobileEditReportLogic;
    final MobileDeleteReportLogic mobileDeleteReportLogic;
    final MobileExportReportLogic mobileExportReportLogic;
    final MobileGetProvisionTokenLogic mobileGetProvisionTokenLogic;
    final MobileGetCloneCodeLogic mobileGetCloneCodeLogic;
    final MobileEditFaceLogic mobileEditFaceLogic;
    final MobileGetProjectByTokenLogic mobileGetProjectByTokenLogic;
    final MobileGetDeviceLogic mobileGetDeviceLogic;
    final MobileEditDeviceMetafieldLogic mobileEditDeviceMetafieldLogic;
    final MobileDeleteDeviceLogic mobileDeleteDeviceLogic;
    final MobileEditDeviceLogic mobileEditDeviceLogic;
    final MobileCreateDeviceLogic mobileCreateDeviceLogic;
    final MobileRedeemLogic mobileRedeemLogic;
    final MobileDeleteTileTemplateLogic mobileDeleteTileTemplateLogic;
    final MobileDeleteWidgetLogic mobileDeleteWidgetLogic;
    final MobileEditWidgetLogic mobileEditWidgetLogic;
    final MobileCreateWidgetLogic mobileCreateWidgetLogic;
    final MobileDeleteDashLogic mobileDeleteDashLogic;
    final MobileEditDashLogic mobileEditDashLogic;
    final MobileCreateDashLogic mobileCreateDashLogic;
    final MobileRefreshShareTokenLogic mobileRefreshShareTokenLogic;
    final MobileGetShareTokenLogic mobileGetShareTokenLogic;
    final MobileDeleteSuperChartDataLogic mobileDeleteSuperChartDataLogic;
    final MobileRefreshTokenLogic mobileRefreshTokenLogic;
    final MobileAssignTokenLogic mobileAssignTokenLogic;
    final MobileShareLogic mobileShareLogic;
    final MobileDeActivateDashboardLogic mobileDeActivateDashboardLogic;
    final MobileActivateDashboardLogic mobileActivateDashboardLogic;
    final MobileEditAppLogic mobileEditAppLogic;
    final MobileEditDashSettingLogic mobileEditDashSettingLogic;
    final MobileCreateAppLogic mobileCreateAppLogic;
    final MobileHardwareGroupLogic mobileHardwareGroupLogic;

    //common handler
    final CommonGetDevicesByReferenceMetafieldLogic commonGetDevicesByReferenceMetafieldLogic;

    //resued web handlers
    final WebGetDeviceTimelineLogic webGetDeviceTimelineLogic;
    final WebResolveLogEventLogic webResolveLogEventLogic;

    public MobileLogicHolder(Holder holder) {
        this.mobileGetOrgDevicesLogic = new MobileGetOrgDevicesLogic(holder);
        this.mobileLoadProfileGzippedLogic = new MobileLoadProfileGzippedLogic(holder);
        this.mobileGetWidgetLogic = new MobileGetWidgetLogic(holder);
        this.mobileGetSuperChartDataLogic = new MobileGetSuperChartDataLogic(holder);
        this.mobileDeleteOrgDeviceDataLogic = new MobileDeleteOrgDeviceDataLogic(holder);
        this.mobileCreateGroupTemplateLogic = new MobileCreateGroupTemplateLogic(holder);
        this.mobileEditGroupTemplateLogic = new MobileEditGroupTemplateLogic();
        this.mobileDeleteGroupTemplateLogic = new MobileDeleteGroupTemplateLogic(holder);
        this.mobileCreateGroupLogic = new MobileCreateGroupLogic();
        this.mobileEditGroupLogic = new MobileEditGroupLogic();
        this.mobileDeleteGroupLogic = new MobileDeleteGroupLogic();
        this.mobileCreateReportLogic = new MobileCreateReportLogic(holder);
        this.mobileEditReportLogic = new MobileEditReportLogic(holder);
        this.mobileDeleteReportLogic = new MobileDeleteReportLogic(holder);
        this.mobileExportReportLogic = new MobileExportReportLogic(holder);
        this.mobileGetProvisionTokenLogic = new MobileGetProvisionTokenLogic(holder);
        this.mobileGetProjectByCloneCodeLogic = new MobileGetProjectByClonedTokenLogic(holder);
        this.mobileGetCloneCodeLogic = new MobileGetCloneCodeLogic(holder);
        this.mobileEditFaceLogic = new MobileEditFaceLogic(holder);
        this.mailQRsLogic = new MobileMailQRsLogic(holder);
        this.mobileGetProjectByTokenLogic = new MobileGetProjectByTokenLogic(holder);
        this.mobileDeleteAppLogic = new MobileDeleteAppLogic(holder);
        this.mobileDashSyncLogic = new MobileDashSyncLogic(holder);
        this.mobileDeviceSyncLogic = new MobileDeviceSyncLogic(holder);
        this.mobileGetDeviceLogic = new MobileGetDeviceLogic(holder);
        this.mobileEditDeviceMetafieldLogic = new MobileEditDeviceMetafieldLogic(holder);
        this.mobileDeleteDeviceLogic = new MobileDeleteDeviceLogic(holder);
        this.mobileEditDeviceLogic = new MobileEditDeviceLogic(holder);
        this.mobileCreateDeviceLogic = new MobileCreateDeviceLogic(holder);
        this.mobilePurchaseLogic = new MobilePurchaseLogic(holder);
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
        this.mobileEditAppLogic = new MobileEditAppLogic(holder);
        this.mobileEditDashSettingLogic = new MobileEditDashSettingLogic(holder);
        this.mobileCreateAppLogic = new MobileCreateAppLogic(holder);
        this.mobileHardwareGroupLogic = new MobileHardwareGroupLogic(holder);

        this.commonGetDevicesByReferenceMetafieldLogic = new CommonGetDevicesByReferenceMetafieldLogic(holder);

        this.webGetDeviceTimelineLogic = new WebGetDeviceTimelineLogic(holder);
        this.webResolveLogEventLogic = new WebResolveLogEventLogic(holder);
    }
}
