package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.Shift;
import cc.blynk.server.core.model.web.product.metafields.ShiftMetaField;
import cc.blynk.server.core.model.widgets.web.FieldType;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import cc.blynk.server.db.dao.descriptor.fucntions.ReplaceFunction;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import static org.jooq.impl.DSL.field;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDescriptor {

    private static final String KNIGHT_TABLE_NAME = "knight_laundry";
    private static final String BLYNK_DEFAULT_NAME = "reporting_raw_data";

    public static final String SPECIAL_NAME = "Shifts";
    public static final MetaField[] shifts = new MetaField[] {
            new ShiftMetaField(1, SPECIAL_NAME, Role.ADMIN, false, new Shift[] {
                    //order is important. it defines related ids.
                    new Shift("Shift 3", "00:00:00", "08:00:00"),
                    new Shift("Shift 1", "08:00:00", "16:00:00"),
                    new Shift("Shift 2", "16:00:00", "00:00:00")
            })
    };

    //todo move somewhere?
    public static final Field<Integer> DEVICE_ID = field("device_id", Integer.class);
    public static final Field<Integer> PIN = field("pin", Integer.class);
    public static final Field<Integer> PIN_TYPE = field("pin_type", Integer.class);
    public static final Field<Timestamp> CREATED = field("created", Timestamp.class);

    public static final TableDescriptor KNIGHT_INSTANCE = new TableDescriptor(KNIGHT_TABLE_NAME, new Column[] {
            //default blynk columns
            new Column("Device Id", INTEGER),
            new Column("Pin", SMALLINT),
            new Column("Pin Type", SMALLINT),
            new Column("Created", TIMESTAMP),

            //knight specific columns
            new Column("Type Of Record", INTEGER),
            new Column("Washer Id", INTEGER),
            new Column("Start Date", DATE, DateFormatters.MM_DD_YY),
            new Column("Start Time", TIME, DateFormatters.HH_MM_SS, shifts),
            new Column("Finish Time", TIME, DateFormatters.HH_MM_SS),
            new Column("Cycle Time", TIME, DateFormatters.HH_MM_SS),
            new Column("Formula Number", INTEGER),
            new Column("Load Weight", INTEGER, new ReplaceFunction(" KG")),
            new Column("Pump Id", INTEGER),
            new Column("Volume", INTEGER),
            new Column("Run Time", INTEGER),
            new Column("Pulse Count", INTEGER)
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
