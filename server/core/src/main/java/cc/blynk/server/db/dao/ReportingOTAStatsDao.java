package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.reporting.ota.DeviceShipmentEvent;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 02.13.19.
 */
public class ReportingOTAStatsDao {

    private static final String insertOTAReportingEventsStat =
            "INSERT INTO reporting_ota_events_stat (shipment_id, device_id, ts, status) VALUES(?,?,?,?)";

    private static final String selectOTAStatusMessagesCount =
            "SELECT status, count() FROM reporting_ota_events_stat WHERE shipment_id = ? GROUP BY status";

    private static final Logger log = LogManager.getLogger(ReportingOTAStatsDao.class);

    private final HikariDataSource ds;

    public ReportingOTAStatsDao(HikariDataSource hikariDataSource) {
        this.ds = hikariDataSource;
    }

    public Map<OTADeviceStatus, Integer> selectOTAStatusMessagesCount(int shipmentId) {
        Map<OTADeviceStatus, Integer> messagesCount = new EnumMap<>(OTADeviceStatus.class);
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(selectOTAStatusMessagesCount)) {
            ps.setInt(1, shipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OTADeviceStatus otaEventName = OTADeviceStatus.values()[rs.getByte("status")];
                    int             count        = rs.getInt("count()");

                    messagesCount.put(otaEventName, count);
                }

                connection.commit();
            }
        } catch (Exception e) {
            log.error("Error inserting batch of OTA events reporting data in DB.", e);
        }
        return messagesCount;
    }

    public boolean insertOTAEventsStat(Queue<DeviceShipmentEvent> values) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertOTAReportingEventsStat)) {

            if (values != null) {
                Iterator<DeviceShipmentEvent> iterator = values.iterator();
                while (iterator.hasNext()) {
                    DeviceShipmentEvent value = iterator.next();
                    prepareInsertOTAEventStat(ps, value.shipmentId, value.deviceId, value.ts, value.otaDeviceStatus);
                    ps.addBatch();
                    iterator.remove();
                }
            }

            ps.executeBatch();
            connection.commit();
            return true;
        } catch (Exception e) {
            log.error("Error inserting batch of OTA events reporting data in DB.", e);
        }
        return false;
    }

    private static void prepareInsertOTAEventStat(PreparedStatement ps,
                                                  int shipmentId, int deviceId, long ts,
                                                  OTADeviceStatus otaDeviceStatus) throws SQLException {
        ps.setInt(1, shipmentId);
        ps.setInt(2, deviceId);
        ps.setTimestamp(3, new Timestamp(ts));
        ps.setInt(4, otaDeviceStatus.ordinal());
    }

}
