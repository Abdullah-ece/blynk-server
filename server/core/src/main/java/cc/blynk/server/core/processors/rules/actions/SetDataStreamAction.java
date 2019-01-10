package cc.blynk.server.core.processors.rules.actions;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.processors.rules.datastream.DeviceDataStream;
import cc.blynk.server.core.processors.rules.datastream.ProductDataStream;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import cc.blynk.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class SetDataStreamAction extends BaseAction {

    private static final DeviceDataStream[] EMPTY_STREAMS = {};

    public final DeviceDataStream[] targetDataStreams;

    public final ValueBase pinValue;

    @JsonCreator
    public SetDataStreamAction(@JsonProperty("targetDataStream") DeviceDataStream[] targetDataStreams,
                               @JsonProperty("pinValue") ValueBase pinValue) {
        this.targetDataStreams = targetDataStreams == null ? EMPTY_STREAMS : targetDataStreams;
        this.pinValue = pinValue;
    }

    @Override
    public void execute(Organization org, Device device, String triggerValue) {
        double resolvedValue = pinValue.resolve(org, device, triggerValue);
        if (resolvedValue != NumberUtil.NO_RESULT) {
            String result = String.valueOf(resolvedValue);
            for (DeviceDataStream targetDataStream : targetDataStreams) {
                if (targetDataStream instanceof ProductDataStream) {
                    ProductDataStream productDataStream = (ProductDataStream) targetDataStream;
                    Product product = org.findProductByIdOrParentId(productDataStream.productId);
                    if (product != null) {
                        int deviceId = device.id;
                        for (Device backReferenceDevice : product.devices) {
                            if (backReferenceDevice.hasReferenceDevice(deviceId)) {
                                backReferenceDevice.updateValue(targetDataStream, result);
                            }
                        }
                    }
                } else {
                    device.updateValue(targetDataStream, result);
                }
            }
        }
    }

    @Override
    public boolean isValid() {
        return targetDataStreams.length > 0 && pinValue != null && pinValue.isValid();
    }
}
