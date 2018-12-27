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

    public final RuleDataStream ruleDataStream;

    @JsonCreator
    public DataStreamTrigger(@JsonProperty("dataStream") RuleDataStream dataStream) {
        this.ruleDataStream = dataStream;
    }

    @Override
    public boolean isSame(int productId, short pin, PinType pinType) {
        return ruleDataStream != null && ruleDataStream.isSame(productId, pin, pinType);
    }
}
