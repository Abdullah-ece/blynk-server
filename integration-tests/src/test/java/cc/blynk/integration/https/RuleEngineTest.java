package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.processors.rules.Rule;
import cc.blynk.server.core.processors.rules.RuleDataStream;
import cc.blynk.server.core.processors.rules.RuleGroup;
import cc.blynk.server.core.processors.rules.actions.SetNumberPinAction;
import cc.blynk.server.core.processors.rules.conditions.NumberUpdatedCondition;
import cc.blynk.server.core.processors.rules.triggers.DataStreamTrigger;
import cc.blynk.server.core.processors.rules.value.FormulaValue;
import cc.blynk.server.core.processors.rules.value.params.BackDeviceReferenceFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.SameDataStreamFormulaParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void testRuleEngineForAiriusCase() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product floorProduct = new Product();
        floorProduct.name = "Airius Floor Sensor";
        floorProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Floor Sensor", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(orgId, floorProduct);
        ProductDTO floorProductFromApi = client.parseProductDTO(1);
        assertNotNull(floorProductFromApi);

        Product fanProduct = new Product();
        fanProduct.name = "Airius Fan Product";
        fanProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Fan Product", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, -1)
        };

        client.createProduct(orgId, fanProduct);
        ProductDTO fanProductFromApi = client.parseProductDTO(2);
        assertNotNull(fanProductFromApi);

        short floor1SourcePin = 1;
        short floor1TargetPin = 2;

        short fanSourcePin = 1;

        RuleDataStream triggerDataStream = new RuleDataStream(floorProductFromApi.id, floor1SourcePin, PinType.VIRTUAL);
        DataStreamTrigger trigger = new DataStreamTrigger(triggerDataStream);

        NumberUpdatedCondition numberUpdatedCondition = new NumberUpdatedCondition();

        RuleDataStream setValueDataStream = new RuleDataStream(floorProductFromApi.id, floor1TargetPin, PinType.VIRTUAL);
        RuleDataStream sourceDataStream = new RuleDataStream(fanProductFromApi.id, fanSourcePin, PinType.VIRTUAL);
        FormulaValue formulaValue = new FormulaValue(
                "x - y",
                Map.of("x", new SameDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(sourceDataStream))
        );
        SetNumberPinAction setNumberPinAction = new SetNumberPinAction(setValueDataStream, formulaValue);

        Rule rule = new Rule("Airius rule", trigger, numberUpdatedCondition, setNumberPinAction);
        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(fanDevice);
        Device createdFanDevice = client.parseDevice(5);
        assertNotNull(createdFanDevice);

        client.getDevicesByReferenceMetafield(createdFanDevice.id, 3);
        DeviceDTO[] deviceDTOS = client.parseDevicesDTO(6);
        assertNotNull(deviceDTOS);
        assertEquals(1, deviceDTOS.length);
        assertEquals(floorDevice.name, deviceDTOS[0].name);
        client.updateDeviceMetafield(createdFanDevice.id,
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, deviceDTOS[0].id));
        client.verifyResult(ok(7));


        TestHardClient fanHardClient = new TestHardClient("localhost", properties.getHttpPort());
        fanHardClient.start();
        fanHardClient.login(createdFanDevice.token);
        fanHardClient.verifyResult(ok(1));
        fanHardClient.hardware(fanSourcePin, "40");

        TestHardClient floorHardClient = new TestHardClient("localhost", properties.getHttpPort());
        floorHardClient.start();
        floorHardClient.login(createdFloorDevice.token);
        floorHardClient.verifyResult(ok(1));
        floorHardClient.hardware(floor1SourcePin, "42");

        floorHardClient.sync(PinType.VIRTUAL, floor1TargetPin);
        floorHardClient.verifyResult(hardware(3, "vw " + floor1TargetPin + " 2.0"));
    }

}

