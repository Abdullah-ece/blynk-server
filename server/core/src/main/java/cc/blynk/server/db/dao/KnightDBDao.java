package cc.blynk.server.db.dao;

import cc.blynk.server.db.dao.table.ColumnEntry;
import cc.blynk.server.db.dao.table.TableDataMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.03.16.
 */
public class KnightDBDao {

    public static final String insertDataPoint =
            "INSERT INTO knight_laundry values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final Logger log = LogManager.getLogger(KnightDBDao.class);
    private final HikariDataSource ds;

    public KnightDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public void insertDataPoint(TableDataMapper tableDataMapper) throws Exception {
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertDataPoint)) {

            for (int i = 0; i < tableDataMapper.data.length; i++) {
                ColumnEntry entry = tableDataMapper.data[i];
                ps.setObject(i + 1, entry.value, entry.column.type);
            }
            ps.executeUpdate();

            connection.commit();
        }
    }

}
