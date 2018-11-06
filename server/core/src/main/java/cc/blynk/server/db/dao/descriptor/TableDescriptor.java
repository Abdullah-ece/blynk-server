package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.widgets.web.FieldType;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.sql.Types.DOUBLE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIMESTAMP;
import static org.jooq.impl.DSL.field;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDescriptor {

    private static final String BLYNK_DEFAULT_NAME = "blynk_default";
    public static final String SHIFTS_METAINFO_NAME = "Shifts";

    //todo move somewhere?
    public static final Field<Integer> DEVICE_ID = field("device_id", Integer.class);
    public static final Field<Integer> PIN = field("pin", Integer.class);
    public static final Field<Integer> PIN_TYPE = field("pin_type", Integer.class);
    public static final Field<Timestamp> CREATED = field("created", Timestamp.class);
    public static final Field<Timestamp> TS = field("ts", Timestamp.class);
    public static final Field<Double> VALUE = field("value", Double.class);

    public static final TableDescriptor BLYNK_DEFAULT_INSTANCE = new TableDescriptor(BLYNK_DEFAULT_NAME, new Column[] {
            //default blynk columns
            new Column("Device Id", INTEGER),
            new Column("Pin", SMALLINT),
            new Column("Pin Type", SMALLINT),
            new Column("Created", TIMESTAMP),

            new Column("Value", DOUBLE)
    });

    public final String tableName;
    public final Column[] columns;

    @JsonCreator
    public TableDescriptor(@JsonProperty("tableName") String tableName,
                           @JsonProperty("columns") Column[] columns) {
        this.tableName = tableName;
        this.columns = columns;
        checkFieldNameAreUnique();
    }

    private void checkFieldNameAreUnique() {
        HashSet<String> names = new HashSet<>();
        for (Column column : columns) {
            if (!names.add(column.columnName)) {
                throw new RuntimeException(column.columnName + " is not unique. Please check table descriptor.");
            }
        }
    }

    public void findMatchingColumn(SelectSelectStep<Record> step, SelectedColumn selectedGroupByColumn) {
        if (selectedGroupByColumn.type == FieldType.COLUMN) {
            for (Column column : columns) {
                if (column.columnName.equals(selectedGroupByColumn.name)) {
                    Field field = field(column.columnName);
                    step.select(field).groupBy(field);
                    break;
                }
            }
        } else {
            for (Column column : columns) {
                for (MetaField metaField : column.metaFields) {
                    if (metaField.name.equals(selectedGroupByColumn.name)) {
                        Field field = field(column.columnName);
                        field = metaField.prepareField(step, field);
                        step.select(metaField.applyMapping(step, field)).groupBy(field);
                        break;
                    }
                }
            }
        }
    }

    public Field[] fields() {
        Field[] fields = new Field[columns.length];
        int i = 0;
        for (Column column : columns) {
            fields[i++] = field(column.columnName, column.getType());
        }
        return fields;
    }

    public List<String> values() {
        List<String> list = new ArrayList<>(columns.length);
        for (Column column : columns) {
            list.add("?");
        }
        return list;
    }

}
