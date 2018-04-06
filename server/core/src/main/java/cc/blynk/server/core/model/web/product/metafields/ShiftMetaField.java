package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.db.dao.descriptor.MetaDataFormatters;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jooq.CaseConditionStep;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.jooq.impl.DSL.field;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ShiftMetaField extends MetaField {

    public static final DateTimeFormatter timeFormatter = MetaDataFormatters.HH_MM_SS.formatter;
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
        //for now assume all shifts are equal
        int divider = 86400 / (shifts.length == 0 ? 1 : shifts.length);
        return field("floor(EXTRACT(EPOCH FROM {0}) / " + divider + ")", Integer.class, field);
    }

    @Override
    public Field<?> applyMapping(SelectSelectStep<Record> query, Field<Object> field) {
        CaseConditionStep<String> caseConditionStep = null;
        for (int i = 0; i < shifts.length; i++) {
            caseConditionStep = caseConditionStep == null
                    ? DSL.when(field.eq(i), shifts[i].name)
                    : caseConditionStep.when(field.eq(i), shifts[i].name);
        }
        return caseConditionStep;
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new ShiftMetaField(id, metaField.name, metaField.role, metaField.isDefault,
                copyShifts(shifts));
    }

    @Override
    public MetaField copy() {
        return new ShiftMetaField(id, name, role, isDefault, copyShifts(shifts));
    }

    private static Shift[] copyShifts(Shift[] shifts) {
        Shift[] copy = new Shift[shifts.length];
        int i = 0;
        for (Shift shift : shifts) {
            copy[i++] = shift.copy();
        }
        return copy;
    }
}
