package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 13.10.17.
 */
public class Shift implements CopyObject<Shift> {

    public static final DateTimeFormatter timeFormatter = ofPattern("HH:mm:ss");

    public final String name;

    @JsonSerialize(using = LocalTimeToIntSerializer.class)
    @JsonDeserialize(using = IntToLocalTimeSerializer.class)
    public final LocalTime from;

    @JsonSerialize(using = LocalTimeToIntSerializer.class)
    @JsonDeserialize(using = IntToLocalTimeSerializer.class)
    public final LocalTime to;

    //todo auto calculate this field?
    public final int timeId;

    @JsonCreator
    public Shift(@JsonProperty("name") String name,
                 @JsonProperty("from") LocalTime from,
                 @JsonProperty("to") LocalTime to,
                 @JsonProperty("timeId") int timeId) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.timeId = timeId;
    }

    public static LocalTime parse(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    public Shift(String name,
                 String from,
                 String to,
                 int timeId) {
        this(name, parse(from), parse(to), timeId);
    }

    @Override
    public Shift copy() {
        return new Shift(name, from, to, timeId);
    }
}
