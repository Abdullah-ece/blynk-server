package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.db.dao.descriptor.fucntions.ColumnFunction;
import cc.blynk.server.internal.EmptyArraysUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.Record;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARCHAR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class Column {

    public final String label;
    public final String columnName;
    public final int type;
    public final MetaDataFormatters formatterTemplate;
    public final ColumnFunction<String, String> filterFunction;
    public MetaField[] metaFields = EmptyArraysUtil.EMPTY_META_FIELDS;

    @JsonCreator
    public Column(@JsonProperty("label") String label,
                  @JsonProperty("columnName") String columnName,
                  @JsonProperty("type") int type,
                  @JsonProperty("formatterTemplate") MetaDataFormatters formatterTemplate,
                  @JsonProperty("filterFunction") ColumnFunction<String, String> filterFunction,
                  @JsonProperty("metaFields") MetaField[] metaFields) {
        this.label = label;
        this.columnName = columnName;
        this.type = type;
        this.formatterTemplate = formatterTemplate;
        this.filterFunction = filterFunction;
        this.metaFields = metaFields;
    }

    public Column(String label, int type) {
        this.label = label;
        this.columnName = labelTrim(label);
        this.type = type;
        this.formatterTemplate = null;
        this.filterFunction = null;
    }

    public Column(String label, String columnName, int type) {
        this.label = label;
        this.columnName = columnName;
        this.type = type;
        this.formatterTemplate = null;
        this.filterFunction = null;
    }

    public Column(String label, int type, MetaDataFormatters formatter) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatterTemplate = formatter;
        this.filterFunction = null;
    }

    public Column(String label, int type, MetaDataFormatters formatter, MetaField[] metaFields) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatterTemplate = formatter;
        this.filterFunction = null;
        this.metaFields = metaFields;
    }

    public Column(String label, int type, MetaField[] metaFields) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatterTemplate = null;
        this.filterFunction = null;
        this.metaFields = metaFields;
    }

    public Column(String label, int type, ColumnFunction<String, String> filterFunction) {
        this.label = label;
        this.columnName = label.toLowerCase().replace(" ", "_");
        this.type = type;
        this.formatterTemplate = null;
        this.filterFunction = filterFunction;
    }

    public static String labelTrim(String columnLabel) {
        return columnLabel.toLowerCase().replace(" ", "_");
    }

    public Object parse(Object val) {
        String filter;
        switch (type) {
            case DATE :
                checkFormatter();
                return LocalDate.parse((String) val, formatterTemplate.formatter);
            case TIME :
                checkFormatter();
                return LocalTime.parse((String) val, formatterTemplate.formatter);
            case TIMESTAMP :
                checkFormatter();
                return LocalDateTime.parse((String) val, formatterTemplate.formatter);
            case INTEGER :
                if (filterFunction != null) {
                    filter = filterFunction.apply((String) val);
                    return Integer.valueOf(filter);
                }
                if (val instanceof Integer) {
                    return val;
                }
                return Integer.parseInt((String) val);
            case SMALLINT :
            case TINYINT :
                if (filterFunction != null) {
                    filter = filterFunction.apply((String) val);
                    return Short.valueOf(filter);
                }
                if (val instanceof Short) {
                    return val;
                }
                return Short.parseShort((String) val);
            case DOUBLE :
            case FLOAT :
                if (filterFunction != null) {
                    filter = filterFunction.apply((String) val);
                    return Double.valueOf(filter);
                }
                if (val instanceof Double) {
                    return val;
                }
                return Double.parseDouble((String) val);
            case CHAR :
                filter = (String) val;
                return filter.charAt(0);
            case VARCHAR :
                filter = (String) val;
                return filter;
            default:
                throw new RuntimeException("Datatype is not supported yet.");
        }
    }

    public Class<?> getType() {
        switch (type) {
            case DATE :
                return LocalDate.class;
            case TIME :
                return LocalTime.class;
            case TIMESTAMP :
                return LocalDateTime.class;
            case INTEGER :
                return Integer.class;
            case TINYINT :
            case SMALLINT :
                return Short.class;
            case FLOAT :
            case DOUBLE :
                return Double.class;
            case CHAR :
                return Character.class;
            default:
                throw new RuntimeException("Datatype " + type + " is not supported yet.");
        }
    }

    public Object get(Record rs) throws SQLException {
        return rs.get(columnName);
    }

    private void checkFormatter() {
        if (formatterTemplate == null) {
            throw new RuntimeException("No formatterTemplate for column " + columnName);
        }
    }
}
