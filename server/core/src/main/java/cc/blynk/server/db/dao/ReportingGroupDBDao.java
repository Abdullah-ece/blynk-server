package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.Granularity;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringJoiner;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public final class ReportingGroupDBDao {

    //todo could be slow. filter by ts.
    //todo no normal way to provide IN CLAUSE for deviceIds. contribute to clickhouse jdbc driver?
    private static final String selectAverageForGroupOfDevices =
            "SELECT ts, avgMerge(value) as value FROM {TABLE} "
                    + "WHERE device_id in ({DEVICE_IDS}) and pin = ? and pin_type = ? "
                    + "group by ts order by ts desc limit 1";

    private final HikariDataSource ds;

    public ReportingGroupDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public RawEntryWithPin getAverageForGroupOfDevices(GroupRequest groupRequest) throws Exception {
        return getAverageForGroupOfDevices(groupRequest.granularity,
                groupRequest.deviceIds, groupRequest.pin, groupRequest.pinType);
    }

    private static String arrayToString(int[] array) {
        StringJoiner joiner = new StringJoiner(",");
        for (int i : array) {
            joiner.add("" + i);
        }
        return joiner.toString();
    }

    public RawEntryWithPin getAverageForGroupOfDevices(Granularity granularityType,
                                                       int[] deviceIds,
                                                       short pin,
                                                       PinType pinType) throws Exception {
        String query = selectAverageForGroupOfDevices
                .replace("{TABLE}", granularityType.tableName)
                .replace("{DEVICE_IDS}", arrayToString(deviceIds));

        RawEntryWithPin rawEntry = null;
        try (Connection connection = ds.getConnection()) {
             PreparedStatement ps = connection.prepareStatement(query);

            ps.setShort(1, pin);
            ps.setInt(2, pinType.ordinal());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rawEntry = new RawEntryWithPin(
                            rs.getTimestamp("ts").getTime(),
                            rs.getDouble("value"),
                            pin, pinType
                    );
                }

                connection.commit();
            }
        }

        return rawEntry;
    }

}

