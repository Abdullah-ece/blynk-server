package cc.blynk.server.db.dao;

import cc.blynk.server.db.dao.table.ColumnValue;
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

    private static final Logger log = LogManager.getLogger(KnightDBDao.class);
    private final HikariDataSource ds;

    public KnightDBDao(HikariDataSource ds) {
        this.ds = ds;
    }

    public void insertDataPoint(TableDataMapper tableDataMapper) {
        String query = tableDataMapper.tableDescriptor.insertQueryString;
        try (Connection connection = ds.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (int i = 0; i < tableDataMapper.data.length; i++) {
                ColumnValue entry = tableDataMapper.data[i];
                log.trace("Index {}, value {}.", i + 1, entry.value);
                ps.setObject(i + 1, entry.value);
            }
            ps.executeUpdate();

            connection.commit();
        } catch (Exception e){
            log.error("Error inserting knight data.", e);
        }
    }

}
