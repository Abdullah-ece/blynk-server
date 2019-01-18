package cc.blynk.server.workers;

import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
import cc.blynk.server.db.ReportingDBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Worker that runs once a minute. Sends all data in batches to RDBMS in case DBManager was initialized.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.08.15.
 */
public class ReportingWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(ReportingWorker.class);

    private final ReportingDiskDao reportingDao;
    private final ReportingDBManager reportingDBManager;

    public ReportingWorker(ReportingDiskDao reportingDao,
                           ReportingDBManager reportingDBManager) {
        this.reportingDao = reportingDao;
        this.reportingDBManager = reportingDBManager;
    }

    @Override
    //todo this could be optimized with minute, hour and daily crons
    public void run() {
        try {
            process(reportingDao.averageAggregator.getMinute(), GraphGranularityType.MINUTE);
            process(reportingDao.averageAggregator.getHourly(), GraphGranularityType.HOURLY);
            process(reportingDao.averageAggregator.getDaily(), GraphGranularityType.DAILY);

            reportingDBManager.insertBatchDataPoints(reportingDao.rawDataProcessor.rawStorage);

            reportingDBManager.cleanOldReportingRecords(Instant.now());
        } catch (Exception e) {
            log.error("Error during reporting job.", e);
        }
    }

    /**
     * Iterates over all reporting entries that were created during last minute.
     * Copies entries to the new collection and removes from old one.
     *
     * @param map - reporting entires that were created during last minute.
     * @param type - type of reporting. Could be minute, hourly, daily.
     */
    private void process(Map<AggregationKey, AggregationValue> map, GraphGranularityType type) {
        if (map.size() != 0) {
            Map<AggregationKey, AggregationValue> removedKeys = new HashMap<>();

            long nowTruncatedToPeriod = System.currentTimeMillis() / type.period;
            for (AggregationKey keyToRemove : map.keySet()) {
                //if prev hour
                if (keyToRemove.isOutdated(nowTruncatedToPeriod)) {
                    AggregationValue value = map.remove(keyToRemove);
                    removedKeys.put(keyToRemove, value);
                }
            }

            reportingDBManager.insertReporting(removedKeys, type);
        }
    }

}
