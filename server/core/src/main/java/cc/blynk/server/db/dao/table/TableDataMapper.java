package cc.blynk.server.db.dao.table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringJoiner;

import static java.sql.JDBCType.DATE;
import static java.sql.JDBCType.INTEGER;
import static java.sql.JDBCType.TIME;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDataMapper {

    private static final Logger log = LogManager.getLogger(TableDataMapper.class);

    public final String tableName;
    public final ColumnEntry[] data;

    public static Column[] knightColumns = new Column[] {
            new Column("start_date", DATE),
            new Column("start_time", TIME),
            new Column("end_date", DATE),
            new Column("end_time", TIME),
            new Column("system_id", INTEGER),
            new Column("washer_id", INTEGER),
            new Column("formula", INTEGER),
            new Column("cycle_time", TIME),
            new Column("load_weight", INTEGER),
            new Column("saphire", INTEGER),
            new Column("boost", INTEGER),
            new Column("emulsifier", INTEGER),
            new Column("destain", INTEGER),
            new Column("bleach", INTEGER),
            new Column("sour", INTEGER),
            new Column("supreme", INTEGER),
            new Column("jasmine", INTEGER)
    };

    public TableDataMapper(String tableName, String[] values) {
        this.tableName = tableName;
        data = new ColumnEntry[values.length];
        for (int i = 0; i < values.length; i++) {
            data[i] = new ColumnEntry(knightColumns[i], values[i]);
        }
    }

    public String createSQL() {
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (Column knightColumn : knightColumns) {
            sj.add("?");
        }
        String result = "INSERT INTO " + tableName + " VALUES " + sj.toString();
        log.debug("insert sql : {}", result);
        return result;
    }

}
