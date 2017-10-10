package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.db.dao.descriptor.fucntions.ReplaceFunction;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringJoiner;

import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDescriptor {

    private static final Logger log = LogManager.getLogger(TableDescriptor.class);

    private static final String KNIGHT_TABLE_NAME = "knight_laundry";
    private static final String BLYNK_DEFAULT_NAME = "reporting_raw_data";

    public static final MetaField[] metafields = new MetaField[] {
            new RangeTimeMetaField(1, "Shift 1", Role.ADMIN, false, "07:59:59", "16:00:00"),
            new RangeTimeMetaField(2, "Shift 2", Role.ADMIN, false, "15:59:59", "23:59:59"),
            new RangeTimeMetaField(3, "Shift 3", Role.ADMIN, false, "00:00:00", "08:00:00")
    };

    public static final TableDescriptor KNIGHT_INSTANCE = new TableDescriptor(KNIGHT_TABLE_NAME, new Column[] {
            new Column("Start Date", DATE, ofPattern("MM/dd/yy")),
            new Column("Start Time", TIME, ofPattern("HH:mm:ss"), metafields),
            new Column("End Date", DATE, ofPattern("MM/dd/yy")),
            new Column("End Time", TIME, ofPattern("HH:mm:ss")),
            new Column("System Id", INTEGER),
            new Column("Washer Id", INTEGER),
            new Column("Formula", INTEGER),
            new Column("Cycle Time", TIME, ofPattern("HH:mm:ss")),
            new Column("Load Weight", INTEGER, new ReplaceFunction(" KG")),
            new Column("Saphire", INTEGER),
            new Column("Boost", INTEGER),
            new Column("Emulsifier", INTEGER),
            new Column("Destain", INTEGER),
            new Column("Bleach", INTEGER),
            new Column("Sour", INTEGER),
            new Column("Supreme", INTEGER),
            new Column("Jasmine", INTEGER)
    });

    public static final TableDescriptor BLYNK_DEFAULT_INSTANCE = new TableDescriptor(BLYNK_DEFAULT_NAME, new Column[] {
            new Column("Email", VARCHAR),
            new Column("Project Id", INTEGER),
            new Column("Device Id", INTEGER),
            new Column("Pin", INTEGER),
            new Column("Pin Type", "pinType", CHAR),
            new Column("Timestamp", "ts", TIMESTAMP),
            new Column("String value", "stringValue", VARCHAR),
            new Column("Double value", "doubleValue", DOUBLE)
    });

    public final String tableName;
    public final String insertQueryString;
    public final Column[] columns;

    @JsonCreator
    public TableDescriptor(@JsonProperty("tableName") String tableName,
                           @JsonProperty("columns") Column[] columns) {
        this.tableName = tableName;
        this.columns = columns;
        //todo build on the fly
        this.insertQueryString = createInsertSQL();
    }

    private String createInsertSQL() {
        String result = "INSERT INTO " + tableName + " "
                + makeColumnsString(columns) + " VALUES " + makeQuestionMarksString(columns.length);
        log.debug("Generated insert sql : {}", result);
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
