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
public class TableDescriptor {

    private static final Logger log = LogManager.getLogger(TableDescriptor.class);

    private static final String KNIGHT_TABLE_NAME = "knight_laundry";
    public static final TableDescriptor KNIGHT_INSTANCE = new TableDescriptor(KNIGHT_TABLE_NAME, new Column[] {
            new Column("Start Date", DATE, ofPattern("MM/dd/yy")),
            new Column("Start Time", TIME, ofPattern("HH:mm:ss")),
            new Column("End Date", DATE, ofPattern("MM/dd/yy")),
            new Column("End Time", TIME, ofPattern("HH:mm:ss")),
            new Column("System Id", INTEGER),
            new Column("Washer Id", INTEGER),
            new Column("Formula", INTEGER),
            new Column("Cycle Time", TIME, ofPattern("HH:mm:ss")),
            new Column("Load Weight", INTEGER, value -> value.replace(" KG", "")),
            new Column("Saphire", INTEGER),
            new Column("Boost", INTEGER),
            new Column("Emulsifier", INTEGER),
            new Column("Destain", INTEGER),
            new Column("Bleach", INTEGER),
            new Column("Sour", INTEGER),
            new Column("Supreme", INTEGER),
            new Column("Jasmine", INTEGER)
    });

    public final String tableName;
    public final String insertQueryString;
    public final Column[] columns;

    private TableDescriptor(String tableName, Column[] columns) {
        this.tableName = tableName;
        this.columns = columns;
        this.insertQueryString = createInsertSQL();
    }

    private String createInsertSQL() {
        String result = "INSERT INTO " + tableName + " "
                + makeColumnsString(columns) + " VALUES " + makeQuestionMarksString(columns.length);
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
