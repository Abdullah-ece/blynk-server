package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.jooq.impl.DSL.field;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ShiftMetaField extends MetaField {

    public static final DateTimeFormatter timeFormatter = ofPattern("HH:mm:ss");
    private static final Shift[] EMPTY = {};

    private final Shift[] shifts;

    @JsonCreator
    public ShiftMetaField(@JsonProperty("id") int id,
                          @JsonProperty("name") String name,
                          @JsonProperty("role") Role role,
                          @JsonProperty("isDefault") boolean isDefault,
                          @JsonProperty("shifts") Shift[] shifts) {
        super(id, name, role, isDefault);
        this.shifts = shifts == null ? EMPTY : shifts;
    }

    public static LocalTime parse(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    @Override
    public Field<Integer> prepareField(SelectSelectStep<Record> query, Field<Object> field) {
        //todo do not hardcode 28800
        return field("ceil(EXTRACT(EPOCH FROM {0}) / 28800)", Integer.class, field);
    }

    /*
    @Override
    public Field<Integer> prepareField(SelectSelectStep<Record> query, String columnName) {
        return count().filterWhere(DSL.field(columnName).between(from, to)).as(name);
    }
    */

    @Override
    public MetaField copy() {
        Shift[] copy = new Shift[shifts.length];
        int i = 0;
        for (Shift shift : shifts) {
            copy[i++] = shift.copy();
        }

        return new ShiftMetaField(id, name, role, isDefault, copy);
    }
}
