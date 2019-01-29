package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.reporting.MobileGraphRequest;
import cc.blynk.server.core.reporting.WebGraphRequest;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.BaseReportingValue;
import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import cc.blynk.server.db.dao.descriptor.DeviceRawDataTableDescriptor;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.jooq.SQLDialect.POSTGRES_9_4;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public class ReportingDBDao {

    private static final String insertRaw =
            "INSERT INTO reporting_device_raw_data (device_id, pin, pin_type, ts, value) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String selectFromAverage =
            "SELECT ts, avgMerge(value) as value FROM {TABLE} GROUP BY device_id, pin, pin_type, ts "
                    + "HAVING device_id = ? and pin = ? and pin_type = ? and ts between ? and ? "
                    + "ORDER BY ts DESC limit ?,?";

    private static final String deleteDeviceData =
            "ALTER TABLE {TABLE} delete WHERE device_id = ?";

    private static final String deleteDevicePinData =
            "ALTER TABLE {TABLE} delete WHERE device_id = ? and pin = ? and pin_type = ?";

    private static final Logger log = LogManager.getLogger(ReportingDBDao.class);

    private final HikariDataSource ds;

    public ReportingDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    private static void prepareReportingInsert(PreparedStatement ps,
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
        List<RawEntry> result = new ArrayList<>();
        String query = selectFromAverage.replace("{TABLE}", granularityType.tableName);

        try (Connection connection = ds.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, deviceId);
            ps.setShort(2, pin);
            ps.setInt(3, pinType.ordinal());
            ps.setTimestamp(4, new Timestamp(from));
            ps.setTimestamp(5, new Timestamp(to));
            ps.setInt(6, offset);
            ps.setInt(7, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RawEntry rawEntry = new RawEntry(
                            rs.getTimestamp("ts").getTime(),
                            rs.getDouble("value")
                    );
                    result.add(rawEntry);
                }

                connection.commit();
            }
        }

        return result;
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

    public void delete(int... deviceIds) {
        for (int deviceId : deviceIds) {
            delete(deviceId);
        }
    }

    public void delete(int deviceId) {
        int count = 0;
        for (GraphGranularityType graphGranularityType : GraphGranularityType.getValues()) {
            count += delete(deleteDeviceData.replace("{TABLE}", graphGranularityType.tableName), deviceId);
        }
        log.debug("Removed all reporting records for device {}. Deleted records: {}", deviceId, count);
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

    public int delete(int deviceId, short pin, PinType pinType) {
        int count = 0;
        for (GraphGranularityType graphGranularityType : GraphGranularityType.getValues()) {
            String query = deleteDevicePinData.replace("{TABLE}", graphGranularityType.tableName);
            count += delete(query, deviceId, pin, pinType);
        }
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
            log.error("Error deleting pin data reporting for device.", e);
        }
        return 0;
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

    public void insertDataPoint(Map<BaseReportingKey, Queue<BaseReportingValue>> tableDataMappers) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertRaw)) {

            for (var entry : tableDataMappers.entrySet()) {
                BaseReportingKey key = entry.getKey();
                Queue<BaseReportingValue> values = entry.getValue();
                if (values != null) {
                    Iterator<BaseReportingValue> iterator = values.iterator();
                    while (iterator.hasNext()) {
                        BaseReportingValue dataPoint = iterator.next();
                        prepareReportingInsert(ps, key.deviceId, key.pin, key.pinType, dataPoint.ts, dataPoint.value);
                        ps.addBatch();
                        iterator.remove();
                    }
                }
            }

            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            log.error("Error inserting batch points reporting data in DB.", e);
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

