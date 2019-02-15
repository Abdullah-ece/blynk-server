package cc.blynk.server.workers;

import cc.blynk.server.db.ReportingDBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Worker that runs once a minute. Sends all data in batches to RDBMS in case DBManager was initialized.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.08.15.
 */
public final class ReportingWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(ReportingWorker.class);

    private final ReportingDBManager reportingDBManager;

    public ReportingWorker(ReportingDBManager reportingDBManager) {
        this.reportingDBManager = reportingDBManager;
    }

    @Override
    public void run() {
        try {
            reportingDBManager.insertBatchDataPoints(reportingDBManager.rawDataProcessor.rawStorage);
        } catch (Exception e) {
            log.error("Error during reporting job.", e);
        }
        try {
            reportingDBManager.insertBatchOTAStats(reportingDBManager.otaStatusProcessor.otaStatusesStorage);
        } catch (Exception e) {
            log.error("Error during reporting job.", e);
        }
    }

}
