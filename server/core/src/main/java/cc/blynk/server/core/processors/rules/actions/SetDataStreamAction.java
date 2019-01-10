package cc.blynk.server.core.processors.rules.actions;

import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.processors.rules.datastream.DeviceRuleDataStream;
import cc.blynk.server.core.processors.rules.datastream.ProductRuleDataStream;
import cc.blynk.server.core.processors.rules.value.ValueBase;
import cc.blynk.utils.NumberUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.18.
 */
public class SetDataStreamAction extends BaseAction {

    private static final DeviceRuleDataStream[] EMPTY_STREAMS = {};

    public final DeviceRuleDataStream[] targetDataStreams;

    public final ValueBase pinValue;

    @JsonCreator
    public SetDataStreamAction(@JsonProperty("targetDataStream") DeviceRuleDataStream[] targetDataStreams,
                               @JsonProperty("pinValue") ValueBase pinValue) {
        this.targetDataStreams = targetDataStreams == null ? EMPTY_STREAMS : targetDataStreams;
        this.pinValue = pinValue;
    }

    @Override
    public void execute(SessionDao sessionDao, Organization org, Device triggerDevice, String triggerValue) {
        double resolvedValue = pinValue.resolve(org, triggerDevice, triggerValue);
        if (resolvedValue != NumberUtil.NO_RESULT) {
            String result = String.valueOf(resolvedValue);
            for (DeviceRuleDataStream targetDataStream : targetDataStreams) {
                if (targetDataStream instanceof ProductRuleDataStream) {
                    ProductRuleDataStream productDataStream = (ProductRuleDataStream) targetDataStream;
                    Product product = org.getProductByIdOrParentId(productDataStream.productId);
                    if (product != null) {
                        int deviceId = triggerDevice.id;
                        for (Device backReferenceDevice : product.devices) {
                            if (backReferenceDevice.hasReferenceDevice(deviceId)) {
                                backReferenceDevice.updateValue(targetDataStream, result);
                                sendToUI(sessionDao, org.id, targetDataStream, result, backReferenceDevice.id);
                            }
                        }
                    }
                } else {
                    triggerDevice.updateValue(targetDataStream, result);
                    sendToUI(sessionDao, org.id, targetDataStream, result, triggerDevice.id);
                }
            }
        }
    }

    private void sendToUI(SessionDao sessionDao, int orgId,
                          DeviceRuleDataStream targetDataStream, String result, int deviceId) {
        Session session = sessionDao.getOrgSession(orgId);
        String hardwareBody = DataStream.makeHardwareBody(
                targetDataStream.pinType, targetDataStream.pin, result);
        session.sendToSelectedDeviceOnWeb(HARDWARE, 777, hardwareBody, deviceId);
        session.sendToApps(HARDWARE, 777, hardwareBody, deviceId);
    }

    @Override
    public boolean isValid() {
        return targetDataStreams.length > 0 && pinValue != null && pinValue.isValid();
    }
}
