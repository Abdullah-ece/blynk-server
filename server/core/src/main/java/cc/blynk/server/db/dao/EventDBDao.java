package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.db.model.LogEvent;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static cc.blynk.utils.DateTimeUtils.UTC_CALENDAR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public class EventDBDao {

    private static final Logger log = LogManager.getLogger(EventDBDao.class);

    public static final String insertEvent= "INSERT INTO reporting_events (device_id, type, ts, event_hashcode, description, is_resolved) values (?, ?, ?, ?, ?, ?)";
    public static final String selectEvents = "select * from reporting_events where device_id = ? and ts BETWEEN ? and ? order by ts desc offset ? limit ?";
    public static final String selectEventsResolvedFilter = "select * from reporting_events where device_id = ? and ts BETWEEN ? and ? and is_resolved = ? order by ts desc offset ? limit ?";
    public static final String selectEventsTypeFilter = "select * from reporting_events where device_id = ? and type = ? and ts BETWEEN ? and ? order by ts desc offset ? limit ?";

    private final HikariDataSource ds;

    public EventDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public List<LogEvent> getEvents(int deviceId, long from, long to, int offset, int limit, boolean isResolved) throws Exception {
        ResultSet rs = null;
        List<LogEvent> events = new ArrayList<>(limit);

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsResolvedFilter)) {

            statement.setInt(1, deviceId);
            statement.setTimestamp(2, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(3, new Timestamp(to), UTC_CALENDAR);
            statement.setBoolean(4, isResolved);
            statement.setInt(5, offset);
            statement.setInt(6, limit);

            rs = statement.executeQuery();

            while (rs.next()) {
                LogEvent logEvent = readEvent(rs);
                events.add(logEvent);
            }

            connection.commit();
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return events;
    }

    public List<LogEvent> getEvents(int deviceId, long from, long to, int offset, int limit) throws Exception {
        ResultSet rs = null;
        List<LogEvent> events = new ArrayList<>(limit);

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEvents)) {

            statement.setInt(1, deviceId);
            statement.setTimestamp(2, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(3, new Timestamp(to), UTC_CALENDAR);
            statement.setInt(4, offset);
            statement.setInt(5, limit);

            rs = statement.executeQuery();

            while (rs.next()) {
                LogEvent logEvent = readEvent(rs);
                events.add(logEvent);
            }

            connection.commit();
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return events;
    }

    public List<LogEvent> getEvents(int deviceId, EventType eventType, long from, long to, int offset, int limit) throws Exception {
        ResultSet rs = null;
        List<LogEvent> events = new ArrayList<>(limit);

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsTypeFilter)) {

            statement.setInt(1, deviceId);
            statement.setInt(2, eventType.ordinal());
            statement.setTimestamp(3, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(4, new Timestamp(to), UTC_CALENDAR);
            statement.setInt(5, offset);
            statement.setInt(6, limit);

            rs = statement.executeQuery();

            while (rs.next()) {
                LogEvent logEvent = readEvent(rs);
                events.add(logEvent);
            }

            connection.commit();
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return events;
    }

    public LogEvent readEvent(ResultSet rs) throws Exception {
        return new LogEvent(
                rs.getInt("device_id"),
                EventType.values()[rs.getInt("type")],
                rs.getTimestamp("ts", UTC_CALENDAR).getTime(),
                rs.getInt("event_hashcode"),
                rs.getString("description"),
                rs.getBoolean("is_resolved")
        );
    }

    public void insert(int deviceId, EventType eventType, long ts, int eventHashcode, String description, boolean isResolved) throws Exception {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertEvent)) {

            ps.setInt(1, deviceId);
            ps.setInt(2, eventType.ordinal());
            ps.setTimestamp(3, new Timestamp(ts), UTC_CALENDAR);
            ps.setInt(4, eventHashcode);
            ps.setString(5, description);
            ps.setBoolean(6, isResolved);

            ps.executeUpdate();
            connection.commit();
        }
    }

    public void insert(LogEvent logEvent) throws Exception {
        insert(logEvent.deviceId, logEvent.eventType, logEvent.ts, logEvent.eventHashcode, logEvent.description, logEvent.isResolved);
    }
}
