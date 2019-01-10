package cc.blynk.server.core.processors.rules.triggers;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.rules.DeviceDataStream;
import cc.blynk.server.core.processors.rules.ProductDataStream;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class ProductDataStreamTrigger extends BaseTrigger {

    public final DeviceDataStream triggerDataStream;

    @JsonCreator
    public ProductDataStreamTrigger(@JsonProperty("triggerDataStream") DeviceDataStream triggerDataStream) {
        this.triggerDataStream = triggerDataStream;
    }

    public ProductDataStreamTrigger(int productId, short sourcePin) {
        this(new ProductDataStream(productId, sourcePin, PinType.VIRTUAL));
    }

    @Override
    public boolean isSame(int productId, short pin, PinType pinType) {
        return triggerDataStream != null && triggerDataStream.isSame(productId, pin, pinType);
    }
}
