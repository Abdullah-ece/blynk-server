package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.reporting.ota.DeviceShipmentEvent;
import cc.blynk.server.core.reporting.ota.ShipmentStatusProcessor;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.BaseReportingValue;
import cc.blynk.server.core.reporting.raw.RawDataCacheForGraphProcessor;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.core.stats.model.Stat;
import cc.blynk.server.db.dao.EventDBDao;
import cc.blynk.server.db.dao.RawEntry;
import cc.blynk.server.db.dao.ReportingDBDao;
import cc.blynk.server.db.dao.ReportingGroupDBDao;
import cc.blynk.server.db.dao.ReportingOTAStatsDao;
import cc.blynk.server.db.dao.ReportingStatsDao;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.properties.BaseProperties;
import cc.blynk.utils.properties.DBProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.clickhouse.ClickHouseDriver;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Queue;

import static cc.blynk.utils.properties.DBProperties.DB_PROPERTIES_FILENAME;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public final class ReportingDBManager implements Closeable {

    private static final Logger log = LogManager.getLogger(ReportingDBManager.class);
    private final HikariDataSource ds;

    private final BlockingIOProcessor blockingIOProcessor;
    public final RawDataCacheForGraphProcessor rawDataCacheForGraphProcessor;
    public final RawDataProcessor rawDataProcessor;
    public final ShipmentStatusProcessor shipmentStatusProcessor;

    public final EventDBDao eventDBDao;

    public final ReportingDBDao reportingDBDao;
    public final ReportingGroupDBDao reportingGroupDBDao;
    public final ReportingStatsDao reportingStatsDao;
    public final ReportingOTAStatsDao reportingOTAStatsDao;

    public ReportingDBManager(BlockingIOProcessor blockingIOProcessor) {
        this(DB_PROPERTIES_FILENAME, blockingIOProcessor);
    }

    public ReportingDBManager(String propsFilename, BlockingIOProcessor blockingIOProcessor) {
        this.blockingIOProcessor = blockingIOProcessor;

        DBProperties dbProperties = new DBProperties(propsFilename);
        HikariConfig config = initConfig(dbProperties);

        log.info("Reporting DB url : {}", config.getJdbcUrl());
        log.info("Reporting DB user : {}", config.getUsername());
        log.info("Connecting to reporting DB...");

        HikariDataSource hikariDataSource = new HikariDataSource(config);

        this.ds = hikariDataSource;
        this.reportingDBDao = new ReportingDBDao(hikariDataSource);
        this.reportingGroupDBDao = new ReportingGroupDBDao(hikariDataSource);
        this.reportingStatsDao = new ReportingStatsDao(hikariDataSource);
        this.reportingOTAStatsDao = new ReportingOTAStatsDao(hikariDataSource);
        this.eventDBDao = new EventDBDao(hikariDataSource);
        this.rawDataCacheForGraphProcessor = new RawDataCacheForGraphProcessor();
        this.rawDataProcessor = new RawDataProcessor();
        this.shipmentStatusProcessor = new ShipmentStatusProcessor();

        log.info("Connected to reporting database successfully.");
    }

    private HikariConfig initConfig(BaseProperties serverProperties) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(ClickHouseDriver.class.getName());
        config.setJdbcUrl(serverProperties.getProperty("reporting.jdbc.url"));
        config.setUsername(serverProperties.getProperty("reporting.user"));
        config.setPassword(serverProperties.getProperty("reporting.password"));

        config.setAutoCommit(false);
        config.setConnectionTimeout(serverProperties.getLongProperty("reporting.connection.timeout.millis"));
        config.setMaximumPoolSize(5);
        config.setMaxLifetime(0);
        config.setConnectionTestQuery("SELECT 1");
        return config;
    }

    public void process(Device device, short pin, PinType pinType, double doubleVal, long ts) {
        if (doubleVal != NumberUtil.NO_RESULT) {
            BaseReportingKey key = new BaseReportingKey(device.id, pinType, pin);

            rawDataProcessor.collect(key, ts, doubleVal);
            if (device.webDashboard.needRawDataForGraph(pin, pinType) /* || dash.needRawDataForGraph(pin, pinType)*/) {
                rawDataCacheForGraphProcessor.collect(key, new RawEntry(ts, doubleVal));
            }
        }
    }

    public void collectEvent(int shipmentId, Device device) {
        shipmentStatusProcessor.collect(shipmentId, device.id, device.updatedAt, device.deviceShipmentInfo.status);
    }

    public void insertStat(String region, Stat stat) {
        if (isDBEnabled()) {
            reportingStatsDao.insertStat(region, stat);
        }
    }

    public void insertBatchOTAStats(Queue<DeviceShipmentEvent> eventsDataBatch) {
        if (isDBEnabled() && eventsDataBatch.size() > 0) {
            blockingIOProcessor.executeDB(() -> reportingOTAStatsDao.insertOTAEventsStat(eventsDataBatch));
        }
    }

    public void insertBatchDataPoints(Map<BaseReportingKey, Queue<BaseReportingValue>> rawDataBatch) {
        if (isDBEnabled() && rawDataBatch.size() > 0) {
            blockingIOProcessor.executeDB(() -> reportingDBDao.insertDataPoint(rawDataBatch));
        }
    }

    public void insertSystemEvent(int deviceId, EventType eventType) {
        if (isDBEnabled()) {
            blockingIOProcessor.executeReportingEvent(() -> {
                log.trace("Executing system event {} for deviceId {}.", eventType, deviceId);
                eventDBDao.insertSystemEvent(deviceId, eventType);
            });
        }
    }

    public void insertEvent(int deviceId, EventType eventType, long ts,
                            int eventHashcode, String description) throws Exception {
        if (isDBEnabled()) {
            eventDBDao.insert(deviceId, eventType, ts, eventHashcode, description);
        }
    }

    public boolean isDBEnabled() {
        return !(ds == null || ds.isClosed());
    }

    public void executeSQL(String sql) throws Exception {
        try (Connection connection = ds.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            connection.commit();
        }
    }

    public Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    @Override
    public void close() {
        if (isDBEnabled()) {
            System.out.println("Closing Reporting DB...");
            ds.close();
        }
    }

}
