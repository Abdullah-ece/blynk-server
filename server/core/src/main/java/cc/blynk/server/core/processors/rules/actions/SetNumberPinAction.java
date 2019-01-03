package cc.blynk.server.core.processors.rules.actions;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.processors.rules.RuleDataStream;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class SetNumberPinAction extends BaseAction {

    public final RuleDataStream targetDataStream;

    public final ValueBase pinValue;

    @JsonCreator
    public SetNumberPinAction(@JsonProperty("targetDataStream") RuleDataStream targetDataStream,
                              @JsonProperty("pinValue") ValueBase pinValue) {
        this.targetDataStream = targetDataStream;
        this.pinValue = pinValue;
    }

    public SetNumberPinAction(int productId, short pin, ValueBase pinValue) {
        this(new RuleDataStream(productId, pin, PinType.VIRTUAL), pinValue);
    }

    @Override
    public boolean isValid() {
        return targetDataStream != null && pinValue != null && pinValue.isValid();
    }
}
