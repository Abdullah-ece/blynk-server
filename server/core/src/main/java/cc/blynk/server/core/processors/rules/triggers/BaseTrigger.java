package cc.blynk.server.core.processors.rules.triggers;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataStreamTrigger.class, name = "PIN_TRIGGER")
})
public abstract class BaseTrigger {

    public abstract boolean isSame(int productId, short pin, PinType pinType);

}
