package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.MultiTextMetaField;
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

    private static final String KNIGHT_SCOPETECH_TABLE_NAME = "knight_scopetech";
    private static final String KNIGHT_TABLE_NAME = "knight_laundry";
    private static final String BLYNK_DEFAULT_NAME = "blynk_default";

    public static final String SHIFTS_METAINFO_NAME = "Shifts";
    public static final String PUMP_METAINFO_NAME = "Pump Names";
    public static final String FORMULA_METAINFO_NAME = "Formula Names";

    public static final MetaField[] shifts = new MetaField[] {
            new ShiftMetaField(1, SHIFTS_METAINFO_NAME, Role.ADMIN, false, false, false, null, new Shift[] {
                    //order is important. it defines related ids.
                    new Shift("Shift 3", "00:00:00", "08:00:00"),
                    new Shift("Shift 1", "08:00:00", "16:00:00"),
                    new Shift("Shift 2", "16:00:00", "00:00:00")
            })
    };

    public static final MetaField[] pumpNames = new MetaField[] {
            new MultiTextMetaField(2, PUMP_METAINFO_NAME, Role.ADMIN, false, false, false, null, new String[]{
                    "",
                    "Saphire",
                    "Boost",
                    "Emulsifier",
                    "Destain",
                    "Bleach",
                    "Sour",
                    "Supreme",
                    "Jasmine"
            })
    };

    public static final MetaField[] formulaNames = new MetaField[] {
            new MultiTextMetaField(3, FORMULA_METAINFO_NAME, Role.ADMIN, false, false, false, null, new String[]{
                    "",
                    "Towel White",
                    "Bed sheet White",
                    "Pillow Case",
                    "Pool Towel",
                    "Dark",
                    "",
                    "F&B Light",
                    "F&B Dark",
                    "",
                    "Light Guest/Uniform",
                    "Dark Guest/Uniform",
                    "Nil1",
                    "Nil2",
                    "F&B White",
                    "White Guest/Uniform",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "Towel White 2",
                    "Bedsheet White 2",
                    "Pillow Case 2",
                    "Pool Towel 2",
                    "Dark2",
                    "",
                    "F&B Light 2",
                    "F&B Dark 2",
                    "",
                    "Light Guest/Uniform",
                    "Dark Guest/Uniform",
                    "Nil 2",
                    "Nil2 2",
                    "F&B White",
                    "White Guest/Uniform"
            })
    };


    //todo move somewhere?
    public static final Field<Integer> DEVICE_ID = field("device_id", Integer.class);
    public static final Field<Integer> PIN = field("pin", Integer.class);
    public static final Field<Integer> PIN_TYPE = field("pin_type", Integer.class);
    public static final Field<Timestamp> CREATED = field("created", Timestamp.class);
    public static final Field<Timestamp> TS = field("ts", Timestamp.class);
    public static final Field<Double> VALUE = field("value", Double.class);

    public static final TableDescriptor KNIGHT_LAUNDRY = new TableDescriptor(KNIGHT_TABLE_NAME, new Column[] {
            //default blynk columns
            new Column("Device Id", INTEGER),
            new Column("Pin", SMALLINT),
            new Column("Pin Type", SMALLINT),
            new Column("Created", TIMESTAMP),

            //knight specific columns
            new Column("Type Of Record", INTEGER),
            new Column("Washer Id", INTEGER),
            new Column("Start Date", DATE, MetaDataFormatters.MM_DD_YY),
            new Column("Start Time", TIME, MetaDataFormatters.HH_MM_SS, shifts),
            new Column("Finish Time", TIME, MetaDataFormatters.HH_MM_SS),
            new Column("Cycle Time", TIME, MetaDataFormatters.HH_MM_SS),
            new Column("Formula Number", INTEGER, formulaNames),
            new Column("Load Weight", INTEGER, new ReplaceFunction(" KG")),
            new Column("Pump Id", INTEGER, pumpNames),
            new Column("Volume", INTEGER),
            new Column("Run Time", INTEGER),
            new Column("Pulse Count", INTEGER)
    });

    public static final TableDescriptor KNIGHT_SCOPETECH = new TableDescriptor(KNIGHT_SCOPETECH_TABLE_NAME,
            new Column[] {
            //default blynk columns
            new Column("Device Id", INTEGER),
            new Column("Pin", SMALLINT),
            new Column("Pin Type", SMALLINT),
            new Column("Created", TIMESTAMP),

            //knight specific columns
            new Column("Time", TIMESTAMP, MetaDataFormatters.M_DD_YYYY_HH_MM_SS),
            new Column("Scope User", VARCHAR),
            new Column("Serial", INTEGER),
            new Column("Dose Volume", INTEGER),
            new Column("Flush Volume", INTEGER),
            new Column("Rinse Volume", INTEGER),
            new Column("Leak Test", INTEGER),
            new Column("Pressure", INTEGER),
            new Column("Temperature", INTEGER),
            new Column("Error", INTEGER)
    });

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

    public static TableDescriptor getTableByPin(byte pin, PinType pinType) {
        if (pin == 100 && pinType == PinType.VIRTUAL) {
            return TableDescriptor.KNIGHT_LAUNDRY;
        } else if (pin == 101 && pinType == PinType.VIRTUAL) {
            return TableDescriptor.KNIGHT_SCOPETECH;
        }

        return TableDescriptor.BLYNK_DEFAULT_INSTANCE;
    }

}
