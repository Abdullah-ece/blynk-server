package cc.blynk.server.workers;

import cc.blynk.server.core.reporting.ota.ShipmentStatusProcessor;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.db.dao.ReportingDBDao;
import cc.blynk.server.db.dao.ReportingOTAStatsDao;
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

    private final ReportingDBDao reportingDBDao;
    private final RawDataProcessor rawDataProcessor;

    private final ReportingOTAStatsDao reportingOTAStatsDao;
    private final ShipmentStatusProcessor shipmentStatusProcessor;

    public ReportingWorker(ReportingDBManager reportingDBManager) {
        this.reportingDBDao = reportingDBManager.reportingDBDao;
        this.rawDataProcessor = reportingDBManager.rawDataProcessor;

        this.reportingOTAStatsDao = reportingDBManager.reportingOTAStatsDao;
        this.shipmentStatusProcessor = reportingDBManager.shipmentStatusProcessor;
    }

    @Override
    public void run() {
        try {
            if (rawDataProcessor.rawStorage.size() > 0) {
                reportingDBDao.insertDataPoint(rawDataProcessor.rawStorage);
            }
        } catch (Exception e) {
            log.error("Error during insert of raw data.", e);
        }
        try {
            if (shipmentStatusProcessor.shipmentStatusStorage.size() > 0) {
                reportingOTAStatsDao.insertOTAEventsStat(shipmentStatusProcessor.shipmentStatusStorage);
            }
        } catch (Exception e) {
            log.error("Error during insert of shipments statuses.", e);
        }
    }

}
