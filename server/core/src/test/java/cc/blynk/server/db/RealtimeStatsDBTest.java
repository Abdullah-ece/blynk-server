package cc.blynk.server.db;

import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.widgets.ui.reporting.ReportScheduler;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.server.core.stats.model.Stat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class RealtimeStatsDBTest {

    private static ReportingDBManager reportingDBManager;
    private static BlockingIOProcessor blockingIOProcessor;
    private static final String UA = "ua";

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
        //clickhouse doesn't have normal way of data removal, so using "hack"
        reportingDBManager.executeSQL("ALTER TABLE reporting_app_stat_minute DELETE where region = \'" + UA + "\'");
        reportingDBManager.executeSQL("ALTER TABLE reporting_command_stat_minute DELETE where region = \'" + UA + "\'");
    }

    @Test
    public void testRealTimeStatsInsertWorks() throws Exception {
        String region = UA;

        SessionDao sessionDao = new SessionDao();
        UserDao userDao = new UserDao(new ConcurrentHashMap<>(), "test", "127.0.0.1");
        BlockingIOProcessor blockingIOProcessor = new BlockingIOProcessor(6, 1000);

        Stat stat = new Stat(sessionDao, userDao, blockingIOProcessor, new GlobalStats(), new ReportScheduler(1, "http://localhost/", null, null, Collections.emptyMap(), null));
        int i = 0;

        for (Map.Entry<Short, Integer> entry: stat.allCommands.stats.entrySet()) {
            entry.setValue(i++);
        }

        boolean insertResult = reportingDBManager.reportingStatsDao.insertStat(region, stat);
        assertTrue(insertResult);

        try (Connection connection = reportingDBManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from reporting_app_stat_minute")) {

            while (rs.next()) {
                assertEquals(region, rs.getString("region"));
                rs.getTimestamp("ts");

                assertEquals(0, rs.getInt("minute_rate"));
                assertEquals(0, rs.getInt("registrations"));
                assertEquals(0, rs.getInt("active"));
                assertEquals(0, rs.getInt("active_week"));
                assertEquals(0, rs.getInt("active_month"));
                assertEquals(0, rs.getInt("connected"));
                assertEquals(0, rs.getInt("online_apps"));
                assertEquals(0, rs.getInt("total_online_apps"));
                assertEquals(0, rs.getInt("online_hards"));
                assertEquals(0, rs.getInt("total_online_hards"));
            }

            connection.commit();
        }

        try (Connection connection = reportingDBManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from reporting_command_stat_minute")) {
            while (rs.next()) {
                assertEquals(region, rs.getString("region"));
                rs.getTimestamp("ts");

                short commandCode = rs.getShort("command_code");
                assertEquals(stat.allCommands.stats.get(commandCode), (Integer) rs.getInt("counter"));
            }

            connection.commit();
        }

    }
}
