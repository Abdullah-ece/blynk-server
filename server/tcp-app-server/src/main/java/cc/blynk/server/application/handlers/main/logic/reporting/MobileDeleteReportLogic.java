package cc.blynk.server.application.handlers.main.logic.reporting;

import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.server.internal.CommonByteBufUtil.ok;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31/05/2018.
 *
 */
public final class MobileDeleteReportLogic {

    private static final Logger log = LogManager.getLogger(MobileDeleteReportLogic.class);

    private final ReportScheduler reportScheduler;

    public MobileDeleteReportLogic(Holder holder) {
        this.reportScheduler = holder.reportScheduler;
    }

    public void messageReceived(ChannelHandlerContext ctx,
                                User user, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new JsonException("Wrong income message format.");
        }

        int dashId = Integer.parseInt(split[0]);
        int reportId = Integer.parseInt(split[1]);

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        ReportingWidget reportingWidget = dash.getReportingWidget();

        if (reportingWidget == null) {
            throw new JsonException("Project has no reporting widget.");
        }

        int existingReportIndex = reportingWidget.getReportIndexById(reportId);
        if (existingReportIndex == -1) {
            throw new JsonException("Cannot find report with provided id.");
        }

        Report reportToDel = reportingWidget.reports[existingReportIndex];
        reportingWidget.reports = ArrayUtil.remove(reportingWidget.reports, existingReportIndex, Report.class);
        dash.updatedAt = System.currentTimeMillis();

        if (reportToDel.isPeriodic()) {
            boolean isRemoved = reportScheduler.cancelStoredFuture(user, dashId, reportId);
            log.debug("Deleting reportId {} in scheduler for {}. Is removed: {}?.",
                    reportToDel.id, user.email, isRemoved);
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
