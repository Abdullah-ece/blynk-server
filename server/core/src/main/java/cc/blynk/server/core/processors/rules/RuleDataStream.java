package cc.blynk.server.core.processors.rules;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class RuleDataStream {

    public final int productId;

    public final short pin;

    public final PinType pinType;

    @JsonCreator
    public RuleDataStream(@JsonProperty("productId") int productId,
                          @JsonProperty("pin") short pin,
                          @JsonProperty("pinType") PinType pinType) {
        this.productId = productId;
        this.pin = pin;
        this.pinType = pinType;
    }

    public boolean isSame(int productId, short pin, PinType pinType) {
        return this.productId ==productId &&this.pin ==pin &&this.pinType == pinType;
    }

    @Override
    public String toString() {
        return "RuleDataStream{" +
                "productId=" + productId +
                ", pin=" + pin +
                ", pinType=" + pinType +
                '}';
    }
}
