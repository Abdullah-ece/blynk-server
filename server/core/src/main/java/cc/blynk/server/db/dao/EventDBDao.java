package cc.blynk.server.db.dao;

import cc.blynk.server.common.handlers.logic.timeline.ResolveEventDTO;
import cc.blynk.server.common.handlers.logic.timeline.TimelineDTO;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.db.dao.descriptor.LogEventDTO;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public final class EventDBDao {

    private static final Logger log = LogManager.getLogger(EventDBDao.class);

    private static final String insertEvent =
            "INSERT INTO reporting_events (id, device_id, type, ts, event_hashcode, description) "
                    + "values (?, ?, ?, ?, ?, ?)";
    private static final String insertSystemEvent =
            "INSERT INTO reporting_events (id, device_id, type, event_hashcode) values (?, ?, ?, ?)";
    private static final String resolveLogEvent =
            "INSERT INTO reporting_events_resolved (id, resolved_by, resolved_comment) values (?, ?, ?)";

    private static final String selectEventsCountSinceLastView =
            "select ev.device_id, ev.type, count(*) "
            + "from reporting_events ev LEFT JOIN reporting_events_last_seen ls "
            + "ON (ev.device_id = ls.device_id and ls.email=?) "
            + "where ls.ts IS NULL OR ev.ts > ls.ts and "
            + "ev.is_resolved = false "
            + "group by ev.device_id, ev.type";

    private static final String selectEventsCountTotalForPeriod = "select type, is_resolved, count(*) as counter "
            + "from (select * from reporting_events where ts BETWEEN ? and ? and device_id = ?) "
            + "ANY LEFT JOIN (select * from reporting_events_resolved) "
            + "USING id "
            + "group by type, is_resolved";

    private static final String insertLastSeen =
            "INSERT INTO reporting_events_last_seen (device_id, email) VALUES (?, ?)";

    private final HikariDataSource ds;
    private final AtomicLong idCounter;

    public EventDBDao(HikariDataSource ds) {
        this.ds = ds;
        try {
            long counter = getCounter() + 1;
            this.idCounter = new AtomicLong(counter);
        } catch (Exception e) {
            log.error("Error init id counter for log events.", e);
            throw new RuntimeException("Error init id counter for log events.");
        }
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
                            EventType.getValues()[rs.getInt("type")],
                            false
                    );
                    events.put(logEvent, rs.getInt("count"));
                }

                connection.commit();
            }
        }

        return events;
    }

    public long getCounter() throws Exception {
        long result = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement("select max(id) as counter from reporting_events")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getLong("counter");
                }
                connection.commit();
            }
        }
        return result;
    }

    public Map<LogEventCountKey, Integer> getEventsTotalCounters(long from, long to, int deviceId) throws Exception {
        Map<LogEventCountKey, Integer> events = new HashMap<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectEventsCountTotalForPeriod)) {

            statement.setTimestamp(1, new Timestamp(from));
            statement.setTimestamp(2, new Timestamp(to));
            statement.setInt(3, deviceId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    LogEventCountKey logEvent = new LogEventCountKey(
                            deviceId,
                            EventType.getValues()[rs.getInt("type")],
                            rs.getBoolean("is_resolved")
                    );
                    events.put(logEvent, rs.getInt("counter"));
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
        List<LogEventDTO> events = new ArrayList<>();

        String eventTypeSql = eventType == null ? "" : "and type = ?";
        String isResolvedSql = isResolved == null ? "" : "where is_resolved = ? ";

        String query = "select * "
                + "from (select * from reporting_events where device_id = ? and ts BETWEEN ? and ? "
                + eventTypeSql
                + ") "
                + "ANY LEFT JOIN "
                + " (select * from reporting_events_resolved) "
                + "USING id "
                +  isResolvedSql
                + "order by COALESCE(resolved_at, ts) desc, id desc limit ?, ?";

        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            int i = 0;
            ps.setInt(++i, deviceId);
            ps.setTimestamp(++i, new Timestamp(from));
            ps.setTimestamp(++i, new Timestamp(to));
            if (eventType != null) {
                ps.setInt(++i, eventType.ordinal());
            }
            if (isResolved != null) {
                ps.setBoolean(++i, isResolved);
            }
            ps.setInt(++i, offset);
            ps.setInt(++i, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LogEventDTO logEvent = readEvent(rs);
                    events.add(logEvent);
                }

                connection.commit();
            }
        }

        return events;
    }

    public LogEventDTO readEvent(ResultSet rs) throws Exception {
        Timestamp resolvedAt = rs.getTimestamp("resolved_at");
        return new LogEventDTO(
                rs.getLong("id"),
                rs.getInt("device_id"),
                EventType.getValues()[rs.getInt("type")],
                rs.getTimestamp("ts").getTime(),
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

            ps.setLong(1, idCounter.incrementAndGet());
            ps.setInt(2, deviceId);
            ps.setInt(3, eventType.ordinal());
            ps.setInt(4, eventType.name().hashCode());
            ps.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            log.error("Error insert system event. Reason: {}", e.getMessage());
        }
    }

    public void insert(int deviceId, EventType eventType, long ts, int eventHashcode,
                       String description) throws Exception {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertEvent)) {

            ps.setLong(1, idCounter.incrementAndGet());
            ps.setInt(2, deviceId);
            ps.setInt(3, eventType.ordinal());
            ps.setTimestamp(4, new Timestamp(ts));
            ps.setInt(5, eventHashcode);
            ps.setString(6, description == null ? "" : description);

            ps.executeUpdate();
            connection.commit();
        }
    }

    public void insertLastSeen(int deviceId, String email) throws Exception {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertLastSeen)) {

            ps.setInt(1, deviceId);
            ps.setString(2, email);
            ps.executeUpdate();

            connection.commit();
        }
    }

    public boolean resolveEvent(ResolveEventDTO resolveEventDTO, String resolvedBy) throws Exception {
        return resolveEvent(resolveEventDTO.logEventId, resolvedBy, resolveEventDTO.resolveComment);
    }

    public boolean resolveEvent(long id, String resolvedBy, String comment) throws Exception {
        int result;
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(resolveLogEvent)) {

            ps.setLong(1, id);
            ps.setString(2, resolvedBy);
            ps.setString(3, comment == null ? "" : comment);

            result = ps.executeUpdate();
            connection.commit();
        }
        return result == 1;
    }

    public void insert(LogEvent logEvent) throws Exception {
        insert(logEvent.deviceId, logEvent.eventType, logEvent.ts,
                logEvent.eventHashcode, logEvent.description);
    }
}
