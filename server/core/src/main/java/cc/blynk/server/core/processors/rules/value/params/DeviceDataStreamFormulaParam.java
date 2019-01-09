package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.processors.rules.RuleDataStream;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class DeviceDataStreamFormulaParam extends FormulaParamBase {

    private static final Logger log = LogManager.getLogger(DeviceDataStreamFormulaParam.class);

    public final RuleDataStream targetDataStream;

    @JsonCreator
    public DeviceDataStreamFormulaParam(@JsonProperty("targetDataStream") RuleDataStream targetDataStream) {
        this.targetDataStream = targetDataStream;
    }

    public DeviceDataStreamFormulaParam(int productId, short pin) {
        this(new RuleDataStream(productId, pin, PinType.VIRTUAL));
    }

    @Override
    public boolean isValid() {
        return targetDataStream != null;
    }

    @Override
    public String resolve(Organization org, Device device, String triggerValue) {
        PinStorageValue pinStorageValue = device.getValue(targetDataStream);
        if (pinStorageValue == null) {
            log.trace("Error processing DeviceDataStreamFormulaParam. No value for {}.", targetDataStream);
            return null;
        }
        return pinStorageValue.lastValue();
    }

    @Override
    public String toString() {
        return "DeviceDataStreamFormulaParam{"
                + "targetDataStream=" + targetDataStream
                + '}';
    }
}
