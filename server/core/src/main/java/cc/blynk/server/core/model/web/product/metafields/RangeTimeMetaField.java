package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.jooq.impl.DSL.count;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class RangeTimeMetaField extends MetaField {

    public static final DateTimeFormatter timeFormatter = ofPattern("HH:mm:ss");

    @JsonSerialize(using = LocalTimeToIntSerializer.class)
    @JsonDeserialize(using = IntToLocalTimeSerializer.class)
    public final LocalTime from;

    @JsonSerialize(using = LocalTimeToIntSerializer.class)
    @JsonDeserialize(using = IntToLocalTimeSerializer.class)
    public final LocalTime to;

    @JsonCreator
    public RangeTimeMetaField(@JsonProperty("id") int id,
                              @JsonProperty("name") String name,
                              @JsonProperty("role") Role role,
                              @JsonProperty("isDefault") boolean isDefault,
                              @JsonProperty("from") LocalTime from,
                              @JsonProperty("to") LocalTime to) {
        super(id, name, role, isDefault);
        this.from = from;
        this.to = to;
    }

    public RangeTimeMetaField(int id,
                              String name,
                              Role role,
                              boolean isDefault,
                              String from,
                              String to) {
        this(id, name, role, isDefault, parse(from), parse(to));
    }

    public static LocalTime parse(String time) {
        return LocalTime.parse(time, timeFormatter);
    }


    @Override
    public Field<Integer> attachQuery(SelectSelectStep<Record> query, String columnName) {
        return count().filterWhere(DSL.field(columnName).between(from, to)).as(name);
    }

    @Override
    public MetaField copy() {
        return new RangeTimeMetaField(id, name, role, isDefault, from, to);
    }
}
