package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod;
import cc.blynk.server.core.reporting.GraphPinRequest;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
import cc.blynk.server.db.dao.RawEntry;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static cc.blynk.server.core.model.enums.PinType.VIRTUAL;
import static cc.blynk.server.core.reporting.average.AverageAggregatorProcessor.MINUTE;
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
        reportingDBManager = new ReportingDBManager("db-test.properties", blockingIOProcessor, true);
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
        GraphPinRequest graphPinRequest = new GraphPinRequest(0, 0, dataStream, GraphPeriod.N_DAY,
                0, AggregationFunctionType.AVG, 0L, 1L);
        List<RawEntry> rawEntries = reportingDBManager.reportingDBDao.getReportingDataByTs(graphPinRequest);
        assertNotNull(rawEntries);
        assertEquals(0, rawEntries.size());
    }

    @Test
    public void testSelectFromReportingAverageTable() throws Exception {
        var aggregationValue = new AggregationValue();
        aggregationValue.update(1.0D);
        Map<AggregationKey, AggregationValue> data = new HashMap<>();
        data.put(
                new AggregationKey("123", 1, 1, 1, PinType.VIRTUAL, (byte) 1, System.currentTimeMillis() / MINUTE),
                aggregationValue
        );
        reportingDBManager.reportingDBDao.insert(data, GraphGranularityType.MINUTE);

        DataStream dataStream = new DataStream((byte) 1, VIRTUAL);
        GraphPinRequest graphPinRequest = new GraphPinRequest(1, 1, dataStream, GraphPeriod.TWELVE_HOURS,
                0, AggregationFunctionType.AVG, System.currentTimeMillis() - 61000, System.currentTimeMillis());
        List<RawEntry> rawEntries = reportingDBManager.reportingDBDao.getReportingDataByTs(graphPinRequest);
        assertNotNull(rawEntries);
        assertEquals(1, rawEntries.size());
        assertEquals(System.currentTimeMillis(), rawEntries.get(0).getKey(), 5000);
        assertEquals(1, rawEntries.get(0).getValue(), 0.001D);
    }

}
