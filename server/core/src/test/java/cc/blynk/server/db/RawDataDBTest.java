package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.web.SourceType;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.db.dao.table.DataQueryRequest;
import cc.blynk.utils.AppNameUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class RawDataDBTest {

    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static DBManager dbManager;
    private static BlockingIOProcessor blockingIOProcessor;
    private static User user;

    @BeforeClass
    public static void init() throws Exception {
        blockingIOProcessor = new BlockingIOProcessor(4, 10000);
        dbManager = new DBManager("db-test.properties", blockingIOProcessor, true);
        assertNotNull(dbManager.getConnection());
        user = new User();
        user.email = "test@test.com";
        user.appName = AppNameUtil.BLYNK;
    }

    @AfterClass
    public static void close() {
        dbManager.close();
    }

    @Before
    public void cleanAll() throws Exception {
        //clean everything just in case
        dbManager.executeSQL("DELETE FROM reporting_raw_data");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectSingleRawData() throws Exception {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        long now = System.currentTimeMillis();
        rawDataProcessor.collect(new BaseReportingKey(user.email, user.appName, 1, 2, PinType.VIRTUAL, (byte) 3), now, 123);

        //invoking directly dao to avoid separate thread execution
        dbManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);

        DataQueryRequest dataQueryRequest = new DataQueryRequest(2, "V3", 0, now, SourceType.RAW_DATA, null, 0, 10);
        List<AbstractMap.SimpleEntry<Long, Double>> result = (List<AbstractMap.SimpleEntry<Long, Double>>)
                dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(result);
        Map.Entry<Long, Double> entry = result.iterator().next();
        assertEquals(now, entry.getKey().longValue());
        assertEquals(123, entry.getValue(), 0.0001);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectFewRawData() throws Exception {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        long now = System.currentTimeMillis();
        rawDataProcessor.collect(new BaseReportingKey(user.email, user.appName, 1, 2, PinType.VIRTUAL, (byte) 3), now, 123);
        rawDataProcessor.collect(new BaseReportingKey(user.email, user.appName, 1, 2, PinType.VIRTUAL, (byte) 3), now + 1, 124);
        rawDataProcessor.collect(new BaseReportingKey(user.email, user.appName, 1, 2, PinType.VIRTUAL, (byte) 3), now + 2, 125);

        //invoking directly dao to avoid separate thread execution
        dbManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);

        DataQueryRequest dataQueryRequest = new DataQueryRequest(2, "V3", 0, now+2, SourceType.RAW_DATA, null, 0, 10);
        List<AbstractMap.SimpleEntry<Long, Double>> result = (List<AbstractMap.SimpleEntry<Long, Double>>)
                dbManager.reportingDBDao.getRawData(dataQueryRequest);

        assertNotNull(result);
        assertEquals(3, result.size());

        Iterator<AbstractMap.SimpleEntry<Long, Double>> iterator = result.iterator();
        Map.Entry<Long, Double> entry = iterator.next();
        assertEquals(now + 2, entry.getKey().longValue());
        assertEquals(125, entry.getValue(), 0.0001);

        entry = iterator.next();
        assertEquals(now + 1, entry.getKey().longValue());
        assertEquals(124, entry.getValue(), 0.0001);

        entry = iterator.next();
        assertEquals(now, entry.getKey().longValue());
        assertEquals(123, entry.getValue(), 0.0001);

        //test limit
        dataQueryRequest = new DataQueryRequest(2, "V3", 0, now + 2, SourceType.RAW_DATA, null, 0, 1);
        result = (List<AbstractMap.SimpleEntry<Long, Double>>) dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(result);
        assertEquals(1, result.size());

        iterator = result.iterator();
        entry = iterator.next();
        assertEquals(now + 2, entry.getKey().longValue());
        assertEquals(125, entry.getValue(), 0.0001);

        //test offset
        dataQueryRequest = new DataQueryRequest(2, "V3", 0, now + 2, SourceType.RAW_DATA, null, 2, 10);
        result = (List<AbstractMap.SimpleEntry<Long, Double>>) dbManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(result);
        assertEquals(1, result.size());

        iterator = result.iterator();
        entry = iterator.next();
        assertEquals(now, entry.getKey().longValue());
        assertEquals(123, entry.getValue(), 0.0001);
    }

    @Test
    public void testInsertStringAsRawData() throws Exception {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(new BaseReportingKey(user.email, user.appName, 1, 2, PinType.VIRTUAL, (byte) 3), 1111111111, "Lamp is ON");

        //invoking directly dao to avoid separate thread execution
        dbManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from reporting_raw_data")) {


            while (rs.next()) {
                assertEquals("test@test.com", rs.getString("email"));
                assertEquals(1, rs.getInt("project_id"));
                assertEquals(2, rs.getInt("device_id"));
                assertEquals(3, rs.getByte("pin"));
                assertEquals("v", rs.getString("pinType"));
                assertEquals(1111111111, rs.getTimestamp("ts", UTC).getTime());
                assertEquals("Lamp is ON", rs.getString("stringValue"));
                assertNull(rs.getString("doubleValue"));

            }

            connection.commit();
        }
    }

    @Test
    public void testInsertDoubleAsRawData() throws Exception {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(new BaseReportingKey(user.email, user.appName, 1, 2, PinType.VIRTUAL, (byte) 3), 1111111111, 1.33D);

        //invoking directly dao to avoid separate thread execution
        dbManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from reporting_raw_data")) {


            while (rs.next()) {
                assertEquals("test@test.com", rs.getString("email"));
                assertEquals(1, rs.getInt("project_id"));
                assertEquals(2, rs.getInt("device_id"));
                assertEquals(3, rs.getByte("pin"));
                assertEquals("v", rs.getString("pinType"));
                assertEquals(1111111111, rs.getTimestamp("ts", UTC).getTime());
                assertNull(rs.getString("stringValue"));
                assertEquals(1.33D, rs.getDouble("doubleValue"), 0.0000001);

            }

            connection.commit();
        }

    }

    //todo tests for large batches.


}
