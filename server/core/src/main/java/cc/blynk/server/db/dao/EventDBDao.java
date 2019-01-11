package cc.blynk.server.db.dao;

import cc.blynk.server.common.handlers.logic.timeline.TimelineDTO;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.db.dao.descriptor.LogEventTableDescriptor;
import cc.blynk.server.db.dao.descriptor.LogEventDTO;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.blynk.utils.DateTimeUtils.UTC_CALENDAR;
import static org.jooq.SQLDialect.POSTGRES_9_4;
import static org.jooq.impl.DSL.coalesce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public class EventDBDao {

    private static final Logger log = LogManager.getLogger(EventDBDao.class);

    private static final String resolveLogEvent =
            "UPDATE reporting_events SET is_resolved = TRUE, resolved_by = ?, "
                    + "resolved_at = ?, resolved_comment = ? where id = ?";
    private static final String insertEvent =
            "INSERT INTO reporting_events (device_id, type, ts, event_hashcode, description, is_resolved) "
                    + "values (?, ?, ?, ?, ?, ?)";
    private static final String insertSystemEvent =
            "INSERT INTO reporting_events (device_id, type, event_hashcode) values (?, ?, ?)";

    private static final String selectEvents =
            "select * from reporting_events where device_id = ? and ts BETWEEN ? and ? "
                    + "order by COALESCE(resolved_at, ts) desc offset ? limit ?";
    private static final String selectEventsResolvedFilter =
            "select * from reporting_events where device_id = ? and ts BETWEEN ? and ? and is_resolved = ? "
                    + "order by COALESCE(resolved_at, ts) desc offset ? limit ?";
    private static final String selectEventsTypeAndResolvedFilter =
            "select * from reporting_events where device_id = ? and type = ? and ts BETWEEN ? and ? "
                    + "and is_resolved = ? order by COALESCE(resolved_at, ts) desc offset ? limit ?";
    private static final String selectEventsTypeFilter = "select * from reporting_events where device_id = ? "
            + "and type = ? and ts BETWEEN ? and ? order by COALESCE(resolved_at, ts) desc offset ? limit ?";

    private static final String selectEventsCountSinceLastView =
            "select ev.device_id, ev.type, count(*) "
            + "from reporting_events ev LEFT JOIN reporting_events_last_seen ls "
            + "ON (ev.device_id = ls.device_id and ls.email=?) "
            + "where ls.ts IS NULL OR ev.ts > ls.ts and "
            + "ev.is_resolved = false "
            + "group by ev.device_id, ev.type";

    private static final String selectEventsCountTotalForPeriod = "select type, is_resolved, count(*) "
            + "from reporting_events where ts BETWEEN ? and ? and device_id = ? group by type, is_resolved";

    private static final String upsertLastSeen = "INSERT INTO reporting_events_last_seen (device_id, email) "
            + "VALUES (?, ?) ON CONFLICT (device_id, email) DO UPDATE SET ts = NOW() at time zone 'utc'";

    private final HikariDataSource ds;

    public EventDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public Map<LogEventCountKey, Integer> getEventsSinceLastView(String email) throws Exception {
        Map<LogEventCountKey, Integer> events = new HashMap<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsCountSinceLastView)) {

            statement.setString(1, email);

            log.debug(statement);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    LogEventCountKey logEvent = new LogEventCountKey(
                            rs.getInt("device_id"),
                            EventType.values()[rs.getInt("type")],
                            false
                    );
                    events.put(logEvent, rs.getInt("count"));
                }

                connection.commit();
            }
        }

        return events;
    }

    public Map<LogEventCountKey, Integer> getEventsTotalCounters(long from, long to, int deviceId) throws Exception {
        Map<LogEventCountKey, Integer> events = new HashMap<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsCountTotalForPeriod)) {

            statement.setTimestamp(1, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(2, new Timestamp(to), UTC_CALENDAR);
            statement.setInt(3, deviceId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    LogEventCountKey logEvent = new LogEventCountKey(
                            deviceId,
                            EventType.values()[rs.getInt("type")],
                            rs.getBoolean("is_resolved")
                    );
                    events.put(logEvent, rs.getInt("count"));
                }

                connection.commit();
            }
        }


        return events;
    }


    public List<LogEventDTO> getEvents(TimelineDTO timelineDTO) throws Exception {
        return getEvents(timelineDTO.deviceId, timelineDTO.eventType, timelineDTO.from,
                timelineDTO.to, timelineDTO.offset, timelineDTO.limit, timelineDTO.isResolved);
    }

    public List<LogEventDTO> getEvents(int deviceId, EventType eventType, long from, long to,
                                    int offset, int limit, Boolean isResolved) throws Exception {
        List<LogEventDTO> events;

        try (Connection connection = ds.getConnection();
             DSLContext create = DSL.using(connection, POSTGRES_9_4)) {
            Condition condition = LogEventTableDescriptor.DEVICE_ID.eq(deviceId)
                    .and(LogEventTableDescriptor.TS
                            .between(new Timestamp(from))
                            .and(new Timestamp(to)));

            if (eventType  != null) {
                condition = condition.and(LogEventTableDescriptor.TYPE.eq(eventType.ordinal()));
            }
            if (isResolved != null) {
                condition = condition.and(LogEventTableDescriptor.IS_RESOLVED.eq(isResolved));
            }

            events = create.select()
                    .from(LogEventTableDescriptor.tableName)
                    .where(condition)
                    .orderBy(coalesce(LogEventTableDescriptor.RESOLVED_AT, LogEventTableDescriptor.TS).desc())
                    .offset(offset)
                    .limit(limit)
                    .fetchInto(LogEventDTO.class);

            connection.commit();
        }

        return events;
    }

    public List<LogEvent> getEvents(int deviceId, long from, long to, int offset,
                                    int limit, boolean isResolved) throws Exception {
        List<LogEvent> events = new ArrayList<>(limit);

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsResolvedFilter)) {

            statement.setInt(1, deviceId);
            statement.setTimestamp(2, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(3, new Timestamp(to), UTC_CALENDAR);
            statement.setBoolean(4, isResolved);
            statement.setInt(5, offset);
            statement.setInt(6, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    LogEvent logEvent = readEvent(rs);
                    events.add(logEvent);
                }

                connection.commit();
            }
        }

        return events;
    }

    public List<LogEvent> getEvents(int deviceId, long from, long to, int offset, int limit) throws Exception {
        List<LogEvent> events = new ArrayList<>(limit);

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEvents)) {

            statement.setInt(1, deviceId);
            statement.setTimestamp(2, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(3, new Timestamp(to), UTC_CALENDAR);
            statement.setInt(4, offset);
            statement.setInt(5, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    LogEvent logEvent = readEvent(rs);
                    events.add(logEvent);
                }

                connection.commit();
            }
        }

        return events;
    }

    public List<LogEvent> getEvents(int deviceId, EventType eventType, long from, long to,
                                    int offset, int limit) throws Exception {
        List<LogEvent> events = new ArrayList<>(limit);

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsTypeFilter)) {

            statement.setInt(1, deviceId);
            statement.setInt(2, eventType.ordinal());
            statement.setTimestamp(3, new Timestamp(from), UTC_CALENDAR);
            statement.setTimestamp(4, new Timestamp(to), UTC_CALENDAR);
            statement.setInt(5, offset);
            statement.setInt(6, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    LogEvent logEvent = readEvent(rs);
                    events.add(logEvent);
                }

                connection.commit();
            }
        }

        return events;
    }

    public LogEvent readEvent(ResultSet rs) throws Exception {
        Timestamp resolvedAt = rs.getTimestamp("resolved_at", UTC_CALENDAR);
        return new LogEvent(
                rs.getInt("id"),
                rs.getInt("device_id"),
                EventType.values()[rs.getInt("type")],
                rs.getTimestamp("ts", UTC_CALENDAR).getTime(),
                rs.getInt("event_hashcode"),
                rs.getString("description"),
                rs.getBoolean("is_resolved"),
                rs.getString("resolved_by"),
                resolvedAt == null ? 0 : resolvedAt.getTime(),
                rs.getString("resolved_comment")
        );
    }

    public void insertSystemEvent(int deviceId, EventType eventType) {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertSystemEvent)) {

            ps.setInt(1, deviceId);
            ps.setInt(2, eventType.ordinal());
            ps.setInt(3, eventType.name().hashCode());

            ps.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            log.error("Error insert system event. Reason: {}", e.getMessage());
        }
    }

    public void insert(int deviceId, EventType eventType, long ts, int eventHashcode,
                       String description, boolean isResolved) throws Exception {
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

    public void upsertLastSeen(int deviceId, String email) throws Exception {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(upsertLastSeen)) {

            ps.setInt(1, deviceId);
            ps.setString(2, email);
            ps.executeUpdate();

            connection.commit();
        }
    }

    public boolean resolveEvent(long id, String name, String comment) throws Exception {
        int result;
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(resolveLogEvent)) {

            ps.setString(1, name);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()), UTC_CALENDAR);
            ps.setString(3, comment == null ? "" : comment);
            ps.setLong(4, id);

            result = ps.executeUpdate();
            connection.commit();
        }
        return result == 1;
    }

    public void insert(LogEvent logEvent) throws Exception {
        insert(logEvent.deviceId, logEvent.eventType, logEvent.ts, logEvent.eventHashcode,
                logEvent.description, logEvent.isResolved);
    }
}
