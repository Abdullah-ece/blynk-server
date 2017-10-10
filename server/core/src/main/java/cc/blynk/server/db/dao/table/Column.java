package cc.blynk.server.db.dao.table;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.internal.EmptyArraysUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;

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
    public MetaField[] metaFields = EmptyArraysUtil.EMPTY_META_FIELDS;

    @JsonCreator
    public Column(@JsonProperty("label") String label,
                  @JsonProperty("columnName") String columnName,
                  @JsonProperty("type") int type,
                  @JsonProperty("formatter") DateTimeFormatter formatter,
                  @JsonProperty("filterFunction") Function<String, String> filterFunction,
                  @JsonProperty("metaFields") MetaField[] metaFields) {
        this.label = label;
        this.columnName = columnName;
        this.type = type;
        this.formatter = formatter;
        this.filterFunction = filterFunction;
        this.metaFields = metaFields;
    }

    public Column(String label, int type) {
        this.label = label;
        this.columnName = labelTrim(label);
        this.type = type;
        this.formatter = null;
        this.filterFunction = null;
    }

    public Column(String label, String columnName, int type) {
        this.label = label;
        this.columnName = columnName;
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

    public Column(String label, int type, DateTimeFormatter formatter, MetaField[] metaFields) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatter = formatter;
        this.filterFunction = null;
        this.metaFields = metaFields;
    }

    public Column(String label, int type, Function<String, String> filterFunction) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatter = null;
        this.filterFunction = filterFunction;
    }

    public static String labelTrim(String columnLabel) {
        return columnLabel.toLowerCase().replace(" ", "_");
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

    public void attachQuery(SelectSelectStep<Record> step, String[] groupBy) {
        for (MetaField metaField : metaFields) {
            if (metaField.isSameName(groupBy)) {
                //special kind of grouping - range grouping...
                //implementing via additional query
                if (metaField instanceof RangeTimeMetaField) {
                    Field<Integer> groupField = ((RangeTimeMetaField) metaField).attachQuery(step, columnName);
                    step.select(groupField);
                }
            }
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
