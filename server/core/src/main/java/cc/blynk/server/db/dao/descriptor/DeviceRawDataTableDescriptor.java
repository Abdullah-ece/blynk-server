package cc.blynk.server.db.dao.descriptor;

import org.jooq.Field;

import java.sql.Timestamp;
import java.util.List;

import static org.jooq.impl.DSL.field;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public final class DeviceRawDataTableDescriptor {

    public static final String NAME = "reporting_device_raw_data";

    public static final Field<Integer>   DEVICE_ID = field("device_id", Integer.class);
    public static final Field<Short>     PIN       = field("pin", Short.class);
    public static final Field<Integer>   PIN_TYPE  = field("pin_type", Integer.class);
    public static final Field<Timestamp> TS        = field("ts", Timestamp.class);
    public static final Field<Double>    VALUE     = field("value", Double.class);

    private static final Field[] fields = new Field[] {
            DEVICE_ID,
            PIN,
            PIN_TYPE,
            TS,
            VALUE
    };

    private static final List<String> values = List.of("?", "?", "?", "?", "?");

    private DeviceRawDataTableDescriptor() {
    }

    public static Field[] fields() {
        return fields;
    }

    public static List<String> values() {
        return values;
    }

}
