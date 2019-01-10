package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.datastream.DeviceRuleDataStream;
import cc.blynk.server.core.processors.rules.datastream.ProductRuleDataStream;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class TriggerDeviceDataStreamFormulaParam extends FormulaParamBase {

    private static final Logger log = LogManager.getLogger(TriggerDeviceDataStreamFormulaParam.class);

    public final DeviceRuleDataStream targetDataStream;

    @JsonCreator
    public TriggerDeviceDataStreamFormulaParam(@JsonProperty("targetDataStream")
                                                           DeviceRuleDataStream targetDataStream) {
        this.targetDataStream = targetDataStream;
    }

    public TriggerDeviceDataStreamFormulaParam(int productId, short pin) {
        this(new ProductRuleDataStream(productId, pin, PinType.VIRTUAL));
    }

    @Override
    public boolean isValid() {
        return targetDataStream != null;
    }

    @Override
    public String resolve(Organization org, Device device, String triggerValue) {
        PinStorageValue pinStorageValue = device.getValue(targetDataStream);
        if (pinStorageValue == null) {
            log.trace("Error processing TriggerDeviceDataStreamFormulaParam. No value for {}.", targetDataStream);
            return null;
        }
        return pinStorageValue.lastValue();
    }

    @Override
    public String toString() {
        return "TriggerDeviceDataStreamFormulaParam{"
                + "targetDataStream=" + targetDataStream
                + '}';
    }
}
