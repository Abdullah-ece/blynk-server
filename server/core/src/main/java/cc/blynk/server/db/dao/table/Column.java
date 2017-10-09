package cc.blynk.server.db.dao.table;

import org.jooq.Record;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static java.sql.Types.DATE;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.TIME;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class Column {

    public final String label;
    public final String columnName;
    public final int type;
    public final DateTimeFormatter formatter;
    public final Function<String, String> filterFunction;

    public Column(String label, int type) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatter = null;
        this.filterFunction = null;
    }

    public Column(String label, int type, DateTimeFormatter formatter) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatter = formatter;
        this.filterFunction = null;
    }

    public Column(String label, int type, Function<String, String> filterFunction) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatter = null;
        this.filterFunction = filterFunction;
    }

    public Object parse(String val) {
        String filter;
        switch (type) {
            case DATE :
                checkFormatter();
                return LocalDate.parse(val, formatter);
            case TIME :
                checkFormatter();
                return LocalTime.parse(val, formatter);
            case INTEGER :
                filter = val;
                if (filterFunction != null) {
                    filter = filterFunction.apply(filter);
                }
                return Integer.valueOf(filter);
            case DOUBLE :
            case FLOAT :
                filter = val;
                if (filterFunction != null) {
                    filter = filterFunction.apply(filter);
                }
                return Double.valueOf(filter);
            default:
                throw new RuntimeException("Datatype is not supported yet.");
        }
    }

    public Object get(Record rs) throws SQLException {
        return rs.get(columnName);
    }

    private void checkFormatter() {
        if (formatter == null) {
            throw new RuntimeException("No formatter for column " + columnName);
        }
    }
}
