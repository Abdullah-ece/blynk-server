package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.10.17.
 */
public final class RawEntryWithPin {

    public final long ts;

    public final double value;

    public final short pin;

    public final PinType pinType;

    @JsonCreator
    public RawEntryWithPin(@JsonProperty("ts") long ts,
                           @JsonProperty("value") double value,
                           @JsonProperty("pin") short pin,
                           @JsonProperty("pinType") PinType pinType) {
        this.ts = ts;
        this.value = value;
        this.pin = pin;
        this.pinType = pinType;
    }

}
