package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
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
public class BackDeviceReferenceFormulaParam extends FormulaParamBase {

    private static final Logger log = LogManager.getLogger(BackDeviceReferenceFormulaParam.class);

    public final RuleDataStream targetDataStream;

    @JsonCreator
    public BackDeviceReferenceFormulaParam(@JsonProperty("targetDataStream") RuleDataStream targetDataStream) {
        this.targetDataStream = targetDataStream;
    }

    private static Product findProductById(Product[] products, int productId) {
        for (Product product : products) {
            if (product.id == productId || product.parentId == productId) {
                return product;
            }
        }
        return null;
    }

    //todo this method is real bottleneck
    //we need to refactor it and use cache for referenced devices
    //otherwise we do iteration over all org devices
    public String resolve(Organization org, Device device) {
        Product product = findProductById(org.products, this.targetDataStream.productId);
        if (product == null) {
            log.trace("BackDeviceReferenceFormulaParam. No back reference product for {} and orgId = {}.",
                    targetDataStream, org.id);
            return null;
        }
        int deviceId = device.id;
        for (Device iterDevice : product.devices) {
            if (iterDevice.hasReferenceDevice(deviceId)) {
                PinStorageValue pinStorageValue = iterDevice.getValue(targetDataStream);
                if (pinStorageValue == null) {
                    log.trace("Error processing BackDeviceReferenceFormulaParam. No value for {}.", targetDataStream);
                    return null;
                }
                return pinStorageValue.lastValue();
            }
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
