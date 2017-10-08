package cc.blynk.server.db.dao.table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringJoiner;

import static java.sql.Types.DATE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIME;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDataMapper {

    public static final String KNIGHT_TABLE_NAME = "knight_laundry";

    private static final Logger log = LogManager.getLogger(TableDataMapper.class);

    public final String tableName;
    public final ColumnValue[] data;
    public final String insertQueryString;

    public static Column[] knightColumns = new Column[] {
            new Column("start_date", DATE, ofPattern("MM/dd/yy")),
            new Column("start_time", TIME, ofPattern("HH:mm:ss")),
            new Column("end_date", DATE, ofPattern("MM/dd/yy")),
            new Column("end_time", TIME, ofPattern("HH:mm:ss")),
            new Column("system_id", INTEGER),
            new Column("washer_id", INTEGER),
            new Column("formula", INTEGER),
            new Column("cycle_time", TIME, ofPattern("HH:mm:ss")),
            new Column("load_weight", INTEGER, value -> value.replace(" KG", "")),
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
        data = new ColumnValue[values.length];
        for (int i = 0; i < values.length; i++) {
            Column knightColumn = knightColumns[i];
            Object value = knightColumn.parse(values[i]);
            log.trace("In {}, out {}. Type {}", values[i], value, value.getClass().getSimpleName());
            data[i] = new ColumnValue(knightColumn.columnName, value);
        }
        this.insertQueryString = createInsertSQL();
    }

    private String createInsertSQL() {
        String result = "INSERT INTO " + tableName + " "
                + makeColumnsString(knightColumns) + " VALUES " + makeQuestionMarksString(data.length);
        log.debug("insert sql : {}", result);
        return result;
    }

    private static String makeColumnsString(Column[] columns) {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (Column column : columns) {
            sj.add(column.columnName);
        }
        return sj.toString();
    }

    private static String makeQuestionMarksString(int length) {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < length; i++) {
            sj.add("?");
        }
        return sj.toString();
    }

}
