package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.db.dao.RawEntry;
import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import cc.blynk.utils.AppNameUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class RawDataDBTest {

    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static ReportingDBManager reportingDBManager;
    private static BlockingIOProcessor blockingIOProcessor;
    private static User user;

    @BeforeClass
    public static void init() throws Exception {
        blockingIOProcessor = new BlockingIOProcessor(4, 10000);
        reportingDBManager = new ReportingDBManager("db-test.properties", blockingIOProcessor, true);
        assertNotNull(reportingDBManager.getConnection());
        user = new User();
        user.email = "test@test.com";
        user.appName = AppNameUtil.BLYNK;
    }

    @AfterClass
    public static void close() {
        reportingDBManager.close();
    }

    @Before
    public void cleanAll() throws Exception {
        //clean everything just in case
        reportingDBManager.executeSQL("DELETE FROM blynk_default");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectSingleRawData() {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(2, PinType.VIRTUAL, (byte) 3, "123");

        //invoking directly dao to avoid separate thread execution
        reportingDBManager.reportingDBDao.insertDataPoint(rawDataProcessor.rawStorage);

        DataQueryRequestDTO dataQueryRequest = new DataQueryRequestDTO(RAW_DATA, 2,
                PinType.VIRTUAL, (byte) 3, null, null, null, null, 0, 10, 0, System.currentTimeMillis());
        List<RawEntry> result = (List<RawEntry>)
                reportingDBManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(result);
        RawEntry entry = result.get(0);
        assertEquals(System.currentTimeMillis(), entry.getKey(), 5000);
        assertEquals(123, entry.getValue(), 0.0001);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectFewRawData() {
        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(2, PinType.VIRTUAL, (byte) 3, "123");
        rawDataProcessor.collect(2, PinType.VIRTUAL, (byte) 3, "124");
        rawDataProcessor.collect(2, PinType.VIRTUAL, (byte) 3, "125.25");

        //invoking directly dao to avoid separate thread execution
        reportingDBManager.reportingDBDao.insertDataPoint(rawDataProcessor.rawStorage);

        DataQueryRequestDTO dataQueryRequest = new DataQueryRequestDTO(RAW_DATA, 2,
                PinType.VIRTUAL, (byte) 3, null, null, null, null, 0, 10, 0, System.currentTimeMillis());
        List<RawEntry> result = (List<RawEntry>)
                reportingDBManager.reportingDBDao.getRawData(dataQueryRequest);

        assertNotNull(result);
        assertEquals(3, result.size());

        Iterator<RawEntry> iterator = result.iterator();
        RawEntry entry = iterator.next();

        assertEquals(System.currentTimeMillis(), entry.getKey(), 1000);
        assertEquals(123, entry.getValue(), 0.0001);

        entry = iterator.next();
        assertEquals(System.currentTimeMillis(), entry.getKey(), 1000);
        assertEquals(124, entry.getValue(), 0.0001);

        entry = iterator.next();
        assertEquals(System.currentTimeMillis(), entry.getKey(), 1000);
        assertEquals(125.25, entry.getValue(), 0.0001);

        //test limit
        dataQueryRequest = new DataQueryRequestDTO(RAW_DATA, 2,
                PinType.VIRTUAL, (byte) 3, null, null, null, null, 0, 1, 0, System.currentTimeMillis());
        result = (List<RawEntry>) reportingDBManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(result);
        assertEquals(1, result.size());

        iterator = result.iterator();
        entry = iterator.next();
        assertEquals(System.currentTimeMillis(), entry.getKey(), 1000);
        assertEquals(125.25, entry.getValue(), 0.0001);

        //test offset
        dataQueryRequest = new DataQueryRequestDTO(RAW_DATA, 2,
                PinType.VIRTUAL, (byte) 3, null, null, null, null, 2, 10, 0, System.currentTimeMillis());
        result = (List<RawEntry>) reportingDBManager.reportingDBDao.getRawData(dataQueryRequest);
        assertNotNull(result);
        assertEquals(1, result.size());

        iterator = result.iterator();
        entry = iterator.next();
        assertEquals(System.currentTimeMillis(), entry.getKey(), 1000);
        assertEquals(123, entry.getValue(), 0.0001);
    }
}
