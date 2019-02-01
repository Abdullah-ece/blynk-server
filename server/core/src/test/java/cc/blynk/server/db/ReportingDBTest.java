package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;
import cc.blynk.server.core.reporting.WebGraphRequest;
import cc.blynk.server.db.dao.RawEntry;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static cc.blynk.server.core.model.enums.PinType.VIRTUAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class ReportingDBTest {

    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static ReportingDBManager reportingDBManager;
    private static BlockingIOProcessor blockingIOProcessor;

    @BeforeClass
    public static void init() throws Exception {
        blockingIOProcessor = new BlockingIOProcessor(4, 10000);
        reportingDBManager = new ReportingDBManager("db-test.properties", blockingIOProcessor);
        assertNotNull(reportingDBManager.getConnection());
    }

    @AfterClass
    public static void close() {
        reportingDBManager.close();
    }

    @Before
    public void cleanAll() throws Exception {
        //clean everything just in case
        reportingDBManager.executeSQL("DELETE FROM reporting_average_minute");
        reportingDBManager.executeSQL("DELETE FROM reporting_average_hourly");
        reportingDBManager.executeSQL("DELETE FROM reporting_average_daily");
    }

    @Test
    public void test() throws Exception {

    }

    @Test
    public void testSelectFromEmptyReportingAverageTable() throws Exception {
        DataStream dataStream = new DataStream((byte) 1, VIRTUAL);
        WebGraphRequest graphPinRequest = new WebGraphRequest(0, dataStream, Period.N_DAY,
                0, AggregationFunctionType.AVG, 0L, 1L);
        List<RawEntry> rawEntries = reportingDBManager.reportingDBDao.getReportingDataByTs(graphPinRequest);
        assertNotNull(rawEntries);
        assertEquals(0, rawEntries.size());
    }

}
