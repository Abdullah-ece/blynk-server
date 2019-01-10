package cc.blynk.server.core.processors.rules.datastream;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class ProductDataStream extends DeviceDataStream {

    public final int productId;

    @JsonCreator
    public ProductDataStream(@JsonProperty("productId") int productId,
                             @JsonProperty("pin") short pin,
                             @JsonProperty("pinType") PinType pinType) {
        super(pin, pinType);
        this.productId = productId;
    }

    @Override
    public boolean isSame(int productId, short pin, PinType pinType) {
        return this.productId == productId && super.isSame(productId, pin, pinType);
    }

    @Override
    public String toString() {
        return "ProductDataStream{"
                + "productId=" + productId
                + ", pin=" + pin
                + ", pinType=" + pinType
                + '}';
    }
}
