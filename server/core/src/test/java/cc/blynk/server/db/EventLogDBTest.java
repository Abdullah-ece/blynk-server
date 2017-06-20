package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class EventLogDBTest {

    private static DBManager dbManager;
    private static BlockingIOProcessor blockingIOProcessor;

    @BeforeClass
    public static void init() throws Exception {
        blockingIOProcessor = new BlockingIOProcessor(4, 10000);
        dbManager = new DBManager("db-test.properties", blockingIOProcessor, true);
        assertNotNull(dbManager.getConnection());
    }

    @AfterClass
    public static void close() {
        dbManager.close();
    }

    @Before
    public void cleanAll() throws Exception {
        //clean everything just in case
        dbManager.executeSQL("DELETE FROM reporting_events");
    }

    @Test
    public void insertSingleRowEvent() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";
        LogEvent logEvent = new LogEvent(1, EventType.INFORMATION, now, eventCode.hashCode(), null);

        dbManager.eventDBDao.insert(logEvent);

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from reporting_events")) {


            while (rs.next()) {
                logEvent = dbManager.eventDBDao.readEvent(rs);
                assertEquals(1, logEvent.deviceId);
                assertEquals(EventType.INFORMATION, logEvent.eventType);
                assertEquals(now, logEvent.ts);
                assertEquals(eventCode.hashCode(), logEvent.eventHashcode);
                assertNull(logEvent.description);
                assertFalse(logEvent.isResolved);
            }

            connection.commit();
        }
    }

    @Test
    public void insert100RowEvents() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";

        for (int i = 0; i < 100; i++) {
            LogEvent logEvent = new LogEvent(1, EventType.INFORMATION, now, eventCode.hashCode(), null);
            dbManager.eventDBDao.insert(logEvent);
        }

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select count(*) from reporting_events")) {

            while (rs.next()) {
                assertEquals(100, rs.getInt(1));
            }
        }
    }

    @Test
    public void selectBasicQueryForSingleEntry() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";

        LogEvent logEvent = new LogEvent(1, EventType.INFORMATION, now, eventCode.hashCode(), null);
        dbManager.eventDBDao.insert(logEvent);

        List<LogEvent> logEvents = dbManager.eventDBDao.getEvents(1, EventType.INFORMATION, now, now, 0, 1);
        assertNotNull(logEvents);
        assertEquals(1, logEvents.size());
    }

    @Test
    public void insertSingleSystemEvent() throws Exception {
        dbManager.eventDBDao.insertSystemEvent(1, EventType.ONLINE);

        List<LogEvent> logEvents = dbManager.eventDBDao.getEvents(1, EventType.ONLINE, 0, System.currentTimeMillis(), 0, 1);
        assertNotNull(logEvents);
        assertEquals(1, logEvents.size());
        assertEquals(EventType.ONLINE, logEvents.get(0).eventType);
    }

    @Test
    public void testResolveEvent() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";

        LogEvent logEvent = new LogEvent(1, EventType.INFORMATION, now, eventCode.hashCode(), null);
        dbManager.eventDBDao.insert(logEvent);

        List<LogEvent> logEvents = dbManager.eventDBDao.getEvents(1, EventType.INFORMATION, now, now, 0, 1);
        assertNotNull(logEvents);
        assertEquals(1, logEvents.size());
        assertFalse(logEvents.get(0).isResolved);

        dbManager.eventDBDao.resolveEvent(logEvents.get(0).id, "Pupkin Vasya", "My Comment");

        logEvents = dbManager.eventDBDao.getEvents(1, EventType.INFORMATION, now, now, 0, 1);
        assertNotNull(logEvents);
        assertEquals(1, logEvents.size());
        assertTrue(logEvents.get(0).isResolved);
        assertEquals("My Comment", logEvents.get(0).resolvedComment);
    }


    @Test
    public void selectBasicQueryForSingleEntryThatIsResolved() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";

        LogEvent logEvent = new LogEvent(1, 1, EventType.INFORMATION, now, eventCode.hashCode(), null, true, "Pupkin Vasya", 0, null);
        dbManager.eventDBDao.insert(logEvent);

        List<LogEvent> logEvents = dbManager.eventDBDao.getEvents(1, now, now, 0, 1, true);
        assertNotNull(logEvents);
        assertEquals(1, logEvents.size());

        logEvents = dbManager.eventDBDao.getEvents(1, now, now, 0, 1, false);
        assertNotNull(logEvents);
        assertEquals(0, logEvents.size());
    }

    @Test
    public void selectBasicQueryFor100Entries() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";


        for (int i = 0; i < 100; i++) {
            LogEvent logEvent = new LogEvent(1, EventType.INFORMATION, now++, eventCode.hashCode(), null);
            dbManager.eventDBDao.insert(logEvent);
        }

        for (int i = 0; i < 10; i += 10) {
            List<LogEvent> logEvents = dbManager.eventDBDao.getEvents(1, EventType.INFORMATION, now - 100, now, i * 10, 10);
            assertNotNull(logEvents);
            assertEquals(10, logEvents.size());
            for (LogEvent logEvent : logEvents) {
                assertEquals(--now, logEvent.ts);

                assertEquals(1, logEvent.deviceId);
                assertEquals(EventType.INFORMATION, logEvent.eventType);
                assertEquals(eventCode.hashCode(), logEvent.eventHashcode);
                assertNull(logEvent.description);
                assertFalse(logEvent.isResolved);
            }
        }
    }

    @Test
    public void selectEventsSinceLastLogin() throws Exception {
        long now = System.currentTimeMillis();
        String eventCode = "something";
        LogEvent logEvent;

        logEvent = new LogEvent(1, EventType.INFORMATION, now, eventCode.hashCode(), null);
        dbManager.eventDBDao.insert(logEvent);
        logEvent = new LogEvent(1, EventType.INFORMATION, now + 1, eventCode.hashCode(), null);
        dbManager.eventDBDao.insert(logEvent);

        logEvent = new LogEvent(2, EventType.INFORMATION, now + 2, eventCode.hashCode(), null);
        dbManager.eventDBDao.insert(logEvent);


        Map<LogEventCountKey, Integer> lastViewEvents = dbManager.eventDBDao.getEventsSinceLastLogin(now - 1);
        assertEquals(2, lastViewEvents.size());

        Integer lastView = lastViewEvents.get(new LogEventCountKey(1, EventType.INFORMATION, false));
        assertEquals(2, lastView.intValue());

        lastView = lastViewEvents.get(new LogEventCountKey(2, EventType.INFORMATION, false));
        assertEquals(1, lastView.intValue());

        lastViewEvents = dbManager.eventDBDao.getEventsSinceLastLogin(now);
        assertEquals(2, lastViewEvents.size());

        lastView = lastViewEvents.get(new LogEventCountKey(1, EventType.INFORMATION, false));
        assertEquals(1, lastView.intValue());

        lastView = lastViewEvents.get(new LogEventCountKey(2, EventType.INFORMATION, false));
        assertEquals(1, lastView.intValue());

        lastViewEvents = dbManager.eventDBDao.getEventsSinceLastLogin(now + 1);
        assertEquals(1, lastViewEvents.size());

        lastView = lastViewEvents.get(new LogEventCountKey(2, EventType.INFORMATION, false));
        assertEquals(1, lastView.intValue());
    }


}
