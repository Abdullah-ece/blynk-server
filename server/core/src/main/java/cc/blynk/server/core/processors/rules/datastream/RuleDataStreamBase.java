package cc.blynk.server.core.processors.rules.datastream;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceRuleDataStream.class, name = "DEVICE_DATA_STREAM"),
        @JsonSubTypes.Type(value = ProductRuleDataStream.class, name = "PRODUCT_DATA_STREAM")
})
public abstract class RuleDataStreamBase {

    public abstract boolean isSame(int productId, short pin, PinType pinType);

}
