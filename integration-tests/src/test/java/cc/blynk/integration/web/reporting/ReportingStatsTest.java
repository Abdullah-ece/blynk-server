package cc.blynk.integration.web.reporting;

import cc.blynk.integration.SingleServerInstancePerTestWithDB;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.stats.model.Stat;
import cc.blynk.server.workers.StatsWorker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_GET_DEVICE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 01.02.19.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportingStatsTest extends SingleServerInstancePerTestWithDB {

    private static final String UA = "ua";

    @Before
    public void clearDB() throws Exception {
        // clickhouse doesn't have normal way of data removal, so using "hack"
        holder.reportingDBManager.executeSQL("ALTER TABLE reporting_app_stat_minute DELETE where region <> \'" + "" + "\'");
        holder.reportingDBManager.executeSQL("ALTER TABLE reporting_command_stat_minute DELETE where region <> \'" + "" + "\'");
    }

    @Test
    public void testInsertStatWorks() throws Exception {
        var stat = new Stat(holder.sessionDao, holder.userDao, holder.blockingIOProcessor,
                holder.stats, holder.reportScheduler);

        int i = 0;

        for (Map.Entry<Short, Integer> entry: stat.allCommands.stats.entrySet()) {
            entry.setValue(++i);
        }

        holder.reportingDBManager.insertStat(UA, stat);

        try (Connection connection = holder.reportingDBManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select * from reporting_app_stat_minute")) {

            while (rs.next()) {
                assertEquals(UA, rs.getString("region"));
                rs.getTimestamp("ts");

                assertEquals(0, rs.getInt("minute_rate"));
                assertEquals(holder.userDao.users.size(), rs.getInt("registrations"));
                assertEquals(0, rs.getInt("active"));
                assertEquals(0, rs.getInt("active_week"));
                assertEquals(0, rs.getInt("active_month"));
                assertEquals(1, rs.getInt("connected"));
                assertEquals(1, rs.getInt("online_apps"));
                assertEquals(1, rs.getInt("total_online_apps"));
                assertEquals(1, rs.getInt("online_hards"));
                assertEquals(1, rs.getInt("total_online_hards"));
            }

            connection.commit();
        }

        try (Connection connection = holder.reportingDBManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select region, command_code, counter from reporting_command_stat_minute")) {
            while (rs.next()) {
                assertEquals(UA, rs.getString("region"));

                short commandCode = rs.getShort("command_code");
                assertEquals(stat.allCommands.stats.get(commandCode), (Integer) rs.getInt("counter"));
            }

            connection.commit();
        }
    }

    @Test
    public void testStatsWorkerHavingHardwareRecord() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        var statsWorker = new StatsWorker(holder);

        Device device = new Device();
        device.name = "My New Device";

        clientPair.appClient.createDevice(device);
        device = clientPair.appClient.parseDevice(1);
        assertNotNull(device);

        clientPair.appClient.getDevice(device.id);
        device = clientPair.appClient.parseDevice(2);

        clientPair.hardwareClient.send("hardware vw 1 1");
        clientPair.appClient.verifyResult(hardware(1, device.id - 1 + " vw 1 1"));

        statsWorker.run();

        try (Connection connection = holder.reportingDBManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select region, command_code, counter from reporting_command_stat_minute")) {
            while (rs.next()) {
                assertEquals("test-region", rs.getString("region"));

                short commandCode = rs.getShort("command_code");
                int counter   = rs.getInt("counter");
                if (commandCode == HARDWARE ||
                        commandCode == MOBILE_GET_DEVICE) {
                    assertTrue(counter > 0);
                }
            }

            connection.commit();
        }
    }
}
