package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.db.dao.descriptor.MetaDataFormatters;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
                          @JsonProperty("roleIds") int[] roleIds,
                          @JsonProperty("includeInProvision") boolean includeInProvision,
                          @JsonProperty("isMandatory") boolean isMandatory,
                          @JsonProperty("isDefault") boolean isDefault,
                          @JsonProperty("icon") String icon,
                          @JsonProperty("shifts") Shift[] shifts) {
        super(id, name, roleIds, includeInProvision, isMandatory, isDefault, icon);
        this.shifts = shifts == null ? EMPTY : shifts;
    }

    public static LocalTime parse(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    @Override
    public MetaField copySpecificFieldsOnly(MetaField metaField) {
        return new ShiftMetaField(id, metaField.name, metaField.roleIds,
                metaField.includeInProvision, metaField.isMandatory, metaField.isDefault,
                metaField.icon,
                copyShifts(shifts));
    }

    @Override
    public MetaField copy() {
        return new ShiftMetaField(id, name, roleIds,
                includeInProvision, isMandatory, isDefault,
                icon, copyShifts(shifts));
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
