package cc.blynk.server.db.dao;

import cc.blynk.server.core.stats.model.Stat;
import cc.blynk.utils.DateTimeUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 28.01.19.
 */
public final class ReportingStatsDao {

    private static final String insertStatMinute =
            "INSERT INTO reporting_app_stat_minute (region, ts, active, active_week, active_month, "
                    + "minute_rate, connected, online_apps, online_hards, "
                    + "total_online_apps, total_online_hards, registrations) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String insertStatAllCommandsMinute =
            "INSERT INTO reporting_command_stat_minute (region, ts, command_code, counter) "
                    + "VALUES(?,?,?,?)";

    private static final String selectMonthlyCommandStat =
            "SELECT command_code, sum(counter) AS counter FROM reporting_command_stat_monthly "
                    + "WHERE ts = toStartOfMonth(toDateTime(?)) GROUP BY command_code";

    private static final Logger log = LogManager.getLogger(ReportingStatsDao.class);

    private final HikariDataSource ds;

    public ReportingStatsDao(HikariDataSource hikariDataSource) {
        this.ds = hikariDataSource;
    }

    public boolean insertStat(String region, Stat stat) {
        final long ts = (stat.ts / DateTimeUtils.MINUTE) * DateTimeUtils.MINUTE;
        final Timestamp timestamp = new Timestamp(ts);

        try (Connection connection = ds.getConnection();
             PreparedStatement appStatPS = connection.prepareStatement(insertStatMinute);
             PreparedStatement allCommandsStatPS = connection.prepareStatement(insertStatAllCommandsMinute)) {

            insertAppStat(appStatPS, stat, region, timestamp);
            insertAllCommandsStat(allCommandsStatPS, stat, region, timestamp);

            appStatPS.executeBatch();
            allCommandsStatPS.executeBatch();

            connection.commit();
            return true;
        } catch (Exception e) {
            log.error("Error inserting real time stat in DB.", e);
        }
        return false;
    }

    public Map<Short, Long> selectMonthlyCommandsStat(long ts) {
        final Timestamp timestamp = new Timestamp(ts);
        Map<Short, Long> commands = new HashMap<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement commandStatPS = connection.prepareStatement(selectMonthlyCommandStat)) {
            commandStatPS.setTimestamp(1, timestamp);

            try (ResultSet rs = commandStatPS.executeQuery()) {
                while (rs.next()) {
                    short commandCode = rs.getShort("command_code");
                    long  counter     = rs.getLong("counter");
                    commands.put(commandCode, counter);
                }

                connection.commit();
            }
        } catch (Exception e) {
            log.error("Error receiving month command stat from DB.", e);
        }
        return commands;
    }

    private static void insertAppStat(PreparedStatement appStatPS,
                                Stat stat, String region, Timestamp ts) throws SQLException {
        appStatPS.setString(1, region);
        appStatPS.setTimestamp(2, ts);
        appStatPS.setInt(3, stat.active);
        appStatPS.setInt(4, stat.activeWeek);
        appStatPS.setInt(5, stat.activeMonth);
        appStatPS.setInt(6, stat.oneMinRate);
        appStatPS.setInt(7, stat.connected);
        appStatPS.setInt(8, stat.onlineApps);
        appStatPS.setInt(9, stat.onlineHards);
        appStatPS.setInt(10, stat.totalOnlineApps);
        appStatPS.setInt(11, stat.totalOnlineHards);
        appStatPS.setInt(12, stat.registrations);

        appStatPS.addBatch();
    }

    private static void insertAllCommandsStat(PreparedStatement allCommandsStatPS,
                                        Stat stat, String region, Timestamp ts) throws SQLException {
        for (Map.Entry<Short, Integer> entry: stat.statsForDB.entrySet()) {
            setAndExecuteCommand(allCommandsStatPS, region, ts, entry.getKey(), entry.getValue());
        }
    }

    private static void setAndExecuteCommand(PreparedStatement preparedStatement,
                                      String region, Timestamp ts,
                                      short command, int dataToInsert) throws SQLException {
        preparedStatement.setString(1, region);
        preparedStatement.setTimestamp(2, ts);
        preparedStatement.setInt(3, command);
        preparedStatement.setInt(4, dataToInsert);
        preparedStatement.addBatch();
    }
}
