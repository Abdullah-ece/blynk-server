package cc.blynk.server.core.processors.rules.value.params;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
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
public class DeviceReferenceFormulaParam extends FormulaParamBase {

    private static final Logger log = LogManager.getLogger(DeviceReferenceFormulaParam.class);

    public final ProductRuleDataStream targetDataStream;

    @JsonCreator
    public DeviceReferenceFormulaParam(@JsonProperty("targetDataStream") ProductRuleDataStream targetDataStream) {
        this.targetDataStream = targetDataStream;
    }

    public DeviceReferenceFormulaParam(int productId, short pin) {
        this(new ProductRuleDataStream(productId, pin, PinType.VIRTUAL));
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
    @Override
    public String resolve(Organization org, Device device, String triggerValue) {
        DeviceReferenceMetaField deviceReferenceMetaField = device.getDeviceReferenceMetafield();
        if (deviceReferenceMetaField == null) {
            log.trace("DeviceReferenceFormulaParam. No device ref metafield for deviceId = {}.", device.id);
            return null;
        }
        if (deviceReferenceMetaField.selectedDeviceId <= 0) {
            log.trace("DeviceReferenceFormulaParam. Metafield {} for deviceId = {} has no device.",
                    deviceReferenceMetaField.id, device.id);
            return null;
        }

        Product product = org.findProductByIdOrParentId(this.targetDataStream.productId);
        if (product == null) {
            log.trace("DeviceReferenceFormulaParam. No reference product for {} and orgId = {}.",
                    targetDataStream, org.id);
            return null;
        }

        int deviceId = (int) deviceReferenceMetaField.selectedDeviceId;

        for (Device iterDevice : product.devices) {
            if (iterDevice.id == deviceId) {
                PinStorageValue pinStorageValue = iterDevice.getValue(targetDataStream);
                if (pinStorageValue == null) {
                    log.trace("Error processing DeviceReferenceFormulaParam. No value for {}.", targetDataStream);
                    return null;
                }
                return pinStorageValue.lastValue();
            }
        }

        log.trace("Error processing DeviceReferenceFormulaParam. No value in all devices for {}.", targetDataStream);
        return null;
    }

    @Override
    public boolean isValid() {
        return this.targetDataStream != null;
    }

}
