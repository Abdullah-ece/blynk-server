package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.Granularity;
import com.zaxxer.hikari.HikariDataSource;
import ru.yandex.clickhouse.ClickHouseArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public final class ReportingGroupDBDao {

    //todo could be slow. filter by ts.
    private static final String selectAverageForGroupOfDevices =
            "SELECT ts, avgMerge(value) as value FROM {TABLE} " +
                    "WHERE device_id in (?) and pin = ? and pin_type = ? group by ts order by ts desc limit 1";

    private final HikariDataSource ds;

    public ReportingGroupDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public RawEntryWithPin getAverageForGroupOfDevices(GroupRequest groupRequest) throws Exception {
        return getAverageForGroupOfDevices(groupRequest.granularity,
                groupRequest.deviceIds, groupRequest.pin, groupRequest.pinType);
    }

    public RawEntryWithPin getAverageForGroupOfDevices(Granularity granularityType,
                                                       int[] deviceIds,
                                                       short pin,
                                                       PinType pinType) throws Exception {
        String query = selectAverageForGroupOfDevices.replace("{TABLE}", granularityType.tableName);

        RawEntryWithPin rawEntry = null;
        try (Connection connection = ds.getConnection()) {
             PreparedStatement ps = connection.prepareStatement(query);

            ps.setArray(1, new ClickHouseArray(Types.INTEGER, deviceIds));
            ps.setShort(2, pin);
            ps.setInt(3, pinType.ordinal());

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

