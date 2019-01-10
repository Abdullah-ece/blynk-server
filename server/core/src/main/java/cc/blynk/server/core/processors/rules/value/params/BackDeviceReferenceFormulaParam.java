package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.processors.rules.datastream.ProductRuleDataStream;
import cc.blynk.utils.DoubleArray;
import cc.blynk.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class BackDeviceReferenceFormulaParam extends FormulaParamBase {

    private static final Logger log = LogManager.getLogger(BackDeviceReferenceFormulaParam.class);

    public final ProductRuleDataStream targetDataStream;

    @JsonCreator
    public BackDeviceReferenceFormulaParam(@JsonProperty("targetDataStream") ProductRuleDataStream targetDataStream) {
        this.targetDataStream = targetDataStream;
    }

    public BackDeviceReferenceFormulaParam(int productId, short pin) {
        this(new ProductRuleDataStream(productId, pin, PinType.VIRTUAL));
    }

    //todo this method is real bottleneck
    //we need to refactor it and use cache for referenced devices
    //otherwise we do iteration over all org devices
    @Override
    public double[] resolve(Organization org, Device device, String triggerValue) {
        Product product = org.getProductByIdOrParentId(this.targetDataStream.productId);
        if (product == null) {
            log.trace("BackDeviceReferenceFormulaParam. No back reference product for {} and orgId = {}.",
                    targetDataStream, org.id);
            return null;
        }

        int deviceId = device.id;
        DoubleArray doubleArray = new DoubleArray();
        for (Device iterDevice : product.devices) {
            if (iterDevice.hasReferenceDevice(deviceId)) {
                PinStorageValue pinStorageValue = iterDevice.getValue(targetDataStream);
                if (pinStorageValue != null) {
                    String value = pinStorageValue.lastValue();
                    if (value != null) {
                        doubleArray.add(NumberUtil.parseDouble(value));
                    }
                }
            }
        }

        if (doubleArray.size() > 0) {
            return doubleArray.toArray();
        }

        log.trace("Error processing BackDeviceReferenceFormulaParam. No value in all devices for {}.",
                targetDataStream);
        return null;
    }

    @Override
    public boolean isValid() {
        return this.targetDataStream != null;
    }

}
