package cc.blynk.server.application.handlers.main.logic.reporting;

import cc.blynk.server.Holder;
import cc.blynk.server.core.dao.DeviceDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.ui.reporting.BaseReportTask;
import cc.blynk.server.core.model.widgets.ui.reporting.Report;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportingWidget;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static cc.blynk.server.core.protocol.enums.Command.MOBILE_EXPORT_REPORT;
import static cc.blynk.server.internal.CommonByteBufUtil.makeUTF8StringMessage;
import static cc.blynk.server.internal.WebByteBufUtil.json;
import static cc.blynk.utils.FileUtils.CSV_DIR;
import static cc.blynk.utils.StringUtils.split2;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31/05/2018.
 *
 */
public final class MobileExportReportLogic {

    private static final Logger log = LogManager.getLogger(MobileExportReportLogic.class);

    private final static long runDelay = TimeUnit.MINUTES.toMillis(1);

    private final DeviceDao deviceDao;
    private final ReportScheduler reportScheduler;

    public MobileExportReportLogic(Holder holder) {
        this.deviceDao = holder.deviceDao;
        this.reportScheduler = holder.reportScheduler;
        createCSVFolder();
    }

    private static void createCSVFolder() {
        try {
            Files.createDirectories(Paths.get(CSV_DIR));
        } catch (IOException ioe) {
            log.error("Error creating temp '{}' folder for csv export data.", CSV_DIR);
        }
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

        Report report = reportingWidget.getReportById(reportId);
        if (report == null) {
            throw new JsonException("Cannot find report with passed id.");
        }

        if (!report.isValid()) {
            log.debug("Report is not valid {} for {}.", report, user.email);
            throw new JsonException("Report is not valid.");
        }

        long now = System.currentTimeMillis();
        if (report.lastReportAt + runDelay > now) {
            log.debug("Report {} trigger limit reached for {}.", report.id, user.email);
            throw new JsonException("Report trigger limit reached.");
        }

        reportScheduler.schedule(new BaseReportTask(user, dashId, report,
                reportScheduler.mailWrapper, reportScheduler.reportingDBDao, deviceDao,
                reportScheduler.downloadUrl) {
            @Override
            public void run() {
                try {
                    report.lastReportAt = generateReport();
                    if (ctx.channel().isWritable()) {
                        ctx.writeAndFlush(
                                makeUTF8StringMessage(MOBILE_EXPORT_REPORT, message.id, report.toString()),
                                ctx.voidPromise()
                        );
                    }
                } catch (Exception e) {
                    log.debug("Error generating export report {} for {}.", report, key.user.email, e);
                    ctx.writeAndFlush(json(message.id, "Error generating export report."), ctx.voidPromise());
                }
            }
        }, 0, TimeUnit.SECONDS);
    }
}
