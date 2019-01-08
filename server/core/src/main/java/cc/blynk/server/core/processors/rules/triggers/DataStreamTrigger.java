package cc.blynk.server.core.processors.rules.triggers;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.rules.RuleDataStream;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class DataStreamTrigger extends BaseTrigger {

    public final RuleDataStream triggerDataStream;

    @JsonCreator
    public DataStreamTrigger(@JsonProperty("triggerDataStream") RuleDataStream triggerDataStream) {
        this.triggerDataStream = triggerDataStream;
    }

    public DataStreamTrigger(int productId, short sourcePin) {
        this(new RuleDataStream(productId, sourcePin, PinType.VIRTUAL));
    }

    @Override
    public boolean isSame(int productId, short pin, PinType pinType) {
        return triggerDataStream != null && triggerDataStream.isSame(productId, pin, pinType);
    }
}
