package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.reporting.MobileGraphRequest;
import cc.blynk.server.core.reporting.WebGraphRequest;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.BaseReportingValue;
import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import cc.blynk.server.db.dao.descriptor.DeviceRawDataTableDescriptor;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.DateTimeUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public class ReportingDBDao {

    public static final String insertRaw =
            "INSERT INTO reporting_device_raw_data (device_id, pin, pin_type, ts, value) "
                    + "VALUES (?, ?, ?, ?, ?)";
    public static final String insertMinute =
            "INSERT INTO reporting_average_minute (device_id, pin, pin_type, ts, value) "
                    + "VALUES (?, ?, ?, ?, ?)";
    private static final String insertHourly =
            "INSERT INTO reporting_average_hourly (device_id, pin, pin_type, ts, value) "
                    + "VALUES (?, ?, ?, ?, ?)";
    private static final String insertDaily =
            "INSERT INTO reporting_average_daily (device_id, pin, pin_type, ts, value) "
                    + "VALUES (?, ?, ?, ?, ?)";

    public static final String selectMinute =
            "SELECT ts, value FROM reporting_average_minute WHERE ts > ? ORDER BY ts DESC limit ?";
    public static final String selectHourly =
            "SELECT ts, value FROM reporting_average_hourly WHERE ts > ? ORDER BY ts DESC limit ?";
    public static final String selectDaily =
            "SELECT ts, value FROM reporting_average_daily WHERE ts > ? ORDER BY ts DESC limit ?";

    private static final String deleteMinute = "DELETE FROM reporting_average_minute WHERE ts < ?";
    private static final String deleteHour = "DELETE FROM reporting_average_hourly WHERE ts < ?";
    public static final String deleteDaily = "DELETE FROM reporting_average_daily WHERE ts < ?";

    private static final String deleteDeviceMinute =
            "DELETE FROM reporting_average_minute WHERE device_id = ? and pin = ? and pin_type = ?";
    private static final String deleteDeviceHourly =
            "DELETE FROM reporting_average_hourly WHERE device_id = ? and pin = ? and pin_type = ?";
    private static final String deleteDeviceDaily =
            "DELETE FROM reporting_average_daily WHERE device_id = ? and pin = ? and pin_type = ?";
    private static final String deleteDeviceRaw =
            "DELETE FROM reporting_device_raw_data WHERE device_id = ? and pin = ? and pin_type = ?";

    private static final String deleteAllDeviceMinute = "DELETE FROM reporting_average_minute WHERE device_id = ?";
    private static final String deleteAllDeviceHourly = "DELETE FROM reporting_average_hourly WHERE device_id = ?";
    private static final String deleteAllDeviceDaily = "DELETE FROM reporting_average_daily WHERE device_id = ?";
    private static final String deleteAllDeviceRaw = "DELETE FROM reporting_device_raw_data WHERE device_id = ?";
    private static final String deleteAllDeviceEvents = "DELETE FROM reporting_events WHERE device_id = ?";
    private static final String deleteAllDeviceEventsLastSeen =
            "DELETE FROM reporting_events_last_seen WHERE device_id = ?";

    private static final Logger log = LogManager.getLogger(ReportingDBDao.class);

    private final HikariDataSource ds;

    public ReportingDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public static void prepareReportingInsert(PreparedStatement ps,
                                              int deviceId,
                                              short pin,
                                              PinType pinType,
                                              long ts,
                                              double value) throws SQLException {
        ps.setInt(1, deviceId);
        ps.setShort(2, pin);
        ps.setInt(3, pinType.ordinal());
        ps.setTimestamp(4, new Timestamp(ts));
        ps.setDouble(5, value);
    }

    public List<RawEntry> getReportingDataByTs(GraphGranularityType granularityType, int deviceId,
                                               short pin, PinType pinType,
                                               long from, long to, int offset, int limit) throws Exception {
        List<RawEntry> result;
        try (Connection connection = ds.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            result = create.select(field("ts"), field("value"))
                    .from(getTableByGranularity(granularityType))
                    .where(DeviceRawDataTableDescriptor.DEVICE_ID.eq(deviceId)
                            .and(DeviceRawDataTableDescriptor.PIN.eq(pin))
                            .and(DeviceRawDataTableDescriptor.PIN_TYPE.eq(pinType.ordinal()))
                            .and(DeviceRawDataTableDescriptor.TS
                                    .betweenSymmetric(new Timestamp(from))
                                    .and(new Timestamp(to))))
                    .orderBy(DeviceRawDataTableDescriptor.TS.asc())
                    .offset(offset)
                    .limit(limit)
                    .fetchInto(RawEntry.class);

            connection.commit();
            return result;
        }
    }

    public List<RawEntry> getReportingDataByTs(WebGraphRequest webGraphRequest) throws Exception {
        return getReportingDataByTs(
                webGraphRequest.type, webGraphRequest.deviceId,
                webGraphRequest.pin, webGraphRequest.pinType,
                webGraphRequest.from, webGraphRequest.to,
                0, webGraphRequest.count
        );
    }

    public List<RawEntry> getReportingDataByTs(MobileGraphRequest mobileMobileGraphRequest) throws Exception {
        return getReportingDataByTs(
                mobileMobileGraphRequest.type, mobileMobileGraphRequest.deviceId,
                mobileMobileGraphRequest.pin, mobileMobileGraphRequest.pinType,
                mobileMobileGraphRequest.from, mobileMobileGraphRequest.to,
                mobileMobileGraphRequest.offset, mobileMobileGraphRequest.limit
        );
    }

    private static String getTableByGranularity(GraphGranularityType graphGranularityType) {
        switch (graphGranularityType) {
            case DAILY:
                return "reporting_average_daily";
            case HOURLY:
                return "reporting_average_hourly";
            default:
                return "reporting_average_minute";
        }
    }

    public static void prepareReportingSelect(PreparedStatement ps, long ts, int limit) throws SQLException {
        ps.setTimestamp(1, new Timestamp(ts));
        ps.setInt(2, limit);
    }

    private static void prepareReportingInsert(PreparedStatement ps,
                                               Map.Entry<AggregationKey, AggregationValue> entry,
                                               GraphGranularityType type) throws SQLException {
        AggregationKey key = entry.getKey();
        AggregationValue value = entry.getValue();
        prepareReportingInsert(ps, key.getDeviceId(),
                key.getPin(), key.getPinType(), key.getTs(type), value.calcAverage());
    }

    private static String getTableByGraphType(GraphGranularityType graphGranularityType) {
        switch (graphGranularityType) {
            case MINUTE :
                return insertMinute;
            case HOURLY :
                return insertHourly;
            default :
                return insertDaily;
        }
    }

    public void insert(Map<AggregationKey, AggregationValue> map, GraphGranularityType graphGranularityType) {
        long start = System.currentTimeMillis();

        log.info("Storing {} reporting...", graphGranularityType.name());

        String insertSQL = getTableByGraphType(graphGranularityType);

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertSQL)) {

            for (Map.Entry<AggregationKey, AggregationValue> entry : map.entrySet()) {
                prepareReportingInsert(ps, entry, graphGranularityType);
                ps.addBatch();
            }

            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            log.error("Error inserting reporting data in DB.", e);
        }

        log.info("Storing {} reporting finished. Time {}. Records saved {}",
                graphGranularityType.name(), System.currentTimeMillis() - start, map.size());
    }

    public void delete(int... deviceIds) {
        for (int deviceId : deviceIds) {
            delete(deviceId);
        }
    }

    public void delete(int deviceId) {
        int count = 0;
        count += delete(deleteAllDeviceMinute, deviceId);
        count += delete(deleteAllDeviceHourly, deviceId);
        count += delete(deleteAllDeviceDaily, deviceId);
        count += delete(deleteAllDeviceRaw, deviceId);
        count += delete(deleteAllDeviceEvents, deviceId);
        count += delete(deleteAllDeviceEventsLastSeen, deviceId);
        log.debug("Removed all reporting records for device {} - {}{}. Deleted records: {}",
                deviceId, count);
    }

    private int delete(String query, int deviceId) {
        try (Connection connection = ds.getConnection();
             PreparedStatement deleteQuery = connection.prepareStatement(query)) {

            deleteQuery.setInt(1, deviceId);

            int counter = deleteQuery.executeUpdate();
            connection.commit();
            return counter;
        } catch (Exception e) {
            log.error("Error deleting all reporting data for device.", e);
        }
        return 0;
    }

    public int delete(int deviceId, DataStream... dataStreams) {
        return delete(Collections.singletonMap(deviceId, Arrays.asList(dataStreams)));
    }

    public int delete(Map<Integer, List<DataStream>> map) {
        int count = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement deleteMinute = connection.prepareStatement(deleteDeviceMinute);
             PreparedStatement deleteHourly = connection.prepareStatement(deleteDeviceHourly);
             PreparedStatement deleteDaily  = connection.prepareStatement(deleteDeviceDaily);
             PreparedStatement deleteRaw    = connection.prepareStatement(deleteDeviceRaw)) {

            for (Map.Entry<Integer, List<DataStream>> mapEntry: map.entrySet()) {
                for (DataStream dataStream: mapEntry.getValue()) {
                    delete(deleteMinute, mapEntry.getKey(), dataStream.pin, dataStream.pinType);
                    delete(deleteHourly, mapEntry.getKey(), dataStream.pin, dataStream.pinType);
                    delete(deleteDaily,  mapEntry.getKey(), dataStream.pin, dataStream.pinType);
                    delete(deleteRaw,    mapEntry.getKey(), dataStream.pin, dataStream.pinType);
                }
            }

            count  = ArrayUtil.getSumOfPositiveCells(deleteMinute.executeBatch());
            count += ArrayUtil.getSumOfPositiveCells(deleteHourly.executeBatch());
            count += ArrayUtil.getSumOfPositiveCells(deleteDaily .executeBatch());
            count += ArrayUtil.getSumOfPositiveCells(deleteRaw   .executeBatch());

            connection.commit();
        } catch (Exception e) {
            log.error("Error removing reporting records for devices", e);
        }
        log.debug("Removed reporting records for devices. Deleted records: {}", count);
        return count;
    }

    private void delete(PreparedStatement deleteStatement,
                       int deviceId, short pin, PinType pinType) throws Exception {
        deleteStatement.setInt(1, deviceId);
        deleteStatement.setShort(2, pin);
        deleteStatement.setInt(3, pinType.ordinal());
        deleteStatement.addBatch();
    }

    public int delete(int deviceId, short pin, PinType pinType) {
        int count = 0;
        count += delete(deleteDeviceMinute, deviceId, pin, pinType);
        count += delete(deleteDeviceHourly, deviceId, pin, pinType);
        count += delete(deleteDeviceDaily, deviceId, pin, pinType);
        count += delete(deleteDeviceRaw, deviceId, pin, pinType);
        log.debug("Removed reporting records for device {} - {}{}. Deleted records: {}",
                deviceId, pinType.pintTypeChar, pin, count);
        return count;
    }

    private int delete(String query, int deviceId, short pin, PinType pinType) {
        try (Connection connection = ds.getConnection();
             PreparedStatement deleteQuery = connection.prepareStatement(query)) {

            deleteQuery.setInt(1, deviceId);
            deleteQuery.setShort(2, pin);
            deleteQuery.setInt(3, pinType.ordinal());

            int counter = deleteQuery.executeUpdate();
            connection.commit();
            return counter;
        } catch (Exception e) {
            log.error("Error deleting reporting data for device.", e);
        }
        return 0;
    }

    public void cleanOldReportingRecords(Instant now) {
        log.info("Removing old reporting records...");

        int minuteRecordsRemoved = 0;
        int hourRecordsRemoved = 0;

        try (Connection connection = ds.getConnection();
             PreparedStatement psMinute = connection.prepareStatement(deleteMinute);
             PreparedStatement psHour = connection.prepareStatement(deleteHour)) {

            //for minute table we store only data for last 24 hours
            psMinute.setTimestamp(1, new Timestamp(now.minus(Period.DAY.numberOfPoints + 1,
                    ChronoUnit.MINUTES).toEpochMilli()), DateTimeUtils.UTC_CALENDAR);

            //for hour table we store only data for last 3 months
            psHour.setTimestamp(1, new Timestamp(now.minus(Period.THREE_MONTHS.numberOfPoints + 1,
                    ChronoUnit.HOURS).toEpochMilli()), DateTimeUtils.UTC_CALENDAR);

            minuteRecordsRemoved = psMinute.executeUpdate();
            hourRecordsRemoved = psHour.executeUpdate();

            connection.commit();
        } catch (Exception e) {
            log.error("Error inserting reporting data in DB.", e);
        }
        log.info("Removing finished. Minute records {}, hour records {}. Time {}",
                minuteRecordsRemoved, hourRecordsRemoved, System.currentTimeMillis() - now.toEpochMilli());
    }

    public void insertDataPoint(int deviceId, short pin, PinType pinType, long ts, double value) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertRaw)) {
            prepareReportingInsert(ps, deviceId, pin, pinType, ts, value);

            ps.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            log.error("Error inserting single point reporting data in DB.", e);
        }
    }

    public void insertDataPoint(Map<BaseReportingKey, Queue<BaseReportingValue>> tableDataMappers)  {

        try (Connection connection = ds.getConnection()) {
            DSLContext create = DSL.using(connection, POSTGRES_9_4);

            BatchBindStep batchBindStep = create.batch(
                    create.insertInto(table(DeviceRawDataTableDescriptor.NAME), DeviceRawDataTableDescriptor.fields())
                            .values(DeviceRawDataTableDescriptor.values()));

            for (var entry : tableDataMappers.entrySet()) {
                BaseReportingKey key = entry.getKey();
                Queue<BaseReportingValue> values = entry.getValue();
                if (values != null) {
                    Iterator<BaseReportingValue> iterator = values.iterator();
                    while (iterator.hasNext()) {
                        BaseReportingValue dataPoint = iterator.next();
                        batchBindStep.bind(key.deviceId, key.pin, key.pinType.ordinal(), dataPoint.ts, dataPoint.value);
                        iterator.remove();
                    }
                }
            }

            batchBindStep.execute();
            connection.commit();
        } catch (Exception e) {
            log.error("Error inserting data.", e);
        }
    }

    public Object getRawData(DataQueryRequestDTO dataQueryRequest) {
        switch (dataQueryRequest.sourceType) {
            case RAW_DATA:
                List<RawEntry> result = new ArrayList<>();
                try (Connection connection = ds.getConnection()) {
                    DSLContext create = DSL.using(connection, POSTGRES_9_4);

                    result = create.select(DeviceRawDataTableDescriptor.TS, DeviceRawDataTableDescriptor.VALUE)
                          .from(DeviceRawDataTableDescriptor.NAME)
                          .where(DeviceRawDataTableDescriptor.DEVICE_ID.eq(dataQueryRequest.deviceId)
                                  .and(DeviceRawDataTableDescriptor.PIN.eq(dataQueryRequest.pin))
                                  .and(DeviceRawDataTableDescriptor.PIN_TYPE.eq(dataQueryRequest.pinType.ordinal()))
                                  .and(DeviceRawDataTableDescriptor.TS
                                          .between(new Timestamp(dataQueryRequest.from))
                                          .and(new Timestamp(dataQueryRequest.to))))
                          .orderBy(DeviceRawDataTableDescriptor.TS.desc())
                          .offset(dataQueryRequest.offset)
                          .limit(dataQueryRequest.limit)
                          .fetchInto(RawEntry.class);

                    connection.commit();
                } catch (Exception e) {
                    log.error("Error getting raw data from DB.", e);
                }

                Collections.reverse(result);
                return result;
            case SUM:
            case AVG:
            case MAX:
            case MIN:
            case MED:
            case COUNT:
            default:
                throw new RuntimeException("Other types of aggregation is not supported yet.");

        }
    }

}

