package cc.blynk.integration.web;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.processors.rules.Rule;
import cc.blynk.server.core.processors.rules.RuleGroup;
import cc.blynk.server.core.processors.rules.actions.SetNumberPinAction;
import cc.blynk.server.core.processors.rules.conditions.TriggerUpdatedCondition;
import cc.blynk.server.core.processors.rules.triggers.DataStreamTrigger;
import cc.blynk.server.core.processors.rules.value.FormulaValue;
import cc.blynk.server.core.processors.rules.value.params.BackDeviceReferenceFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.DeviceReferenceFormulaParam;
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
import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void CRUDForRuleGroup() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getRuleGroup();
        RuleGroup ruleGroup = client.parseRuleGroup(1);
        assertNull(ruleGroup);

        short floorSourcePin = 1;
        short floorTargetPin = 2;
        short fanSourcePin = 1;

        //if floorProduct.v1 updated setPin floorProduct.v2 = x - avgForReferences(y);
        //x = floorProduct.v1;
        //y = back_refefence_for_floorProduct

        DataStreamTrigger trigger = new DataStreamTrigger(1, floorSourcePin);
        TriggerUpdatedCondition numberUpdatedCondition = new TriggerUpdatedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new SameDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(1, fanSourcePin))
        );
        SetNumberPinAction setNumberPinAction = new SetNumberPinAction(1, floorTargetPin, formulaValue);
        Rule rule = new Rule("Airius rule", trigger, numberUpdatedCondition, setNumberPinAction);
        System.out.println(
                JsonParser.MAPPER
                        .writerWithDefaultPrettyPrinter()
                        .forType(RuleGroup.class)
                .writeValueAsString(new RuleGroup(new Rule[] {
                        rule
                }))
        );
        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule
        }));
        client.verifyResult(ok(2));

        client.getRuleGroup();
        ruleGroup = client.parseRuleGroup(3);
        assertNotNull(ruleGroup);
    }

    @Test
    public void CRUDForRuleGroupFromJson() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getRuleGroup();
        RuleGroup ruleGroup = client.parseRuleGroup(1);
        assertNull(ruleGroup);

        String json =
                "{\n" +
                        "  \"rules\" : [ {\n" +
                        "    \"name\" : \"Airius rule\",\n" +
                        "    \"trigger\" : {\n" +
                        "      \"type\" : \"PIN_TRIGGER\",\n" +
                        "      \"triggerDataStream\" : {\n" +
                        "        \"productId\" : 1,\n" +
                        "        \"pin\" : 1,\n" +
                        "        \"pinType\" : \"VIRTUAL\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"condition\" : {\n" +
                        "      \"type\" : \"UPDATED\"\n" +
                        "    },\n" +
                        "    \"action\" : {\n" +
                        "      \"type\" : \"SET_NUMBER_PIN_ACTION\",\n" +
                        "      \"targetDataStream\" : {\n" +
                        "        \"productId\" : 1,\n" +
                        "        \"pin\" : 2,\n" +
                        "        \"pinType\" : \"VIRTUAL\"\n" +
                        "      },\n" +
                        "      \"pinValue\" : {\n" +
                        "        \"type\" : \"FORMULA_VALUE\",\n" +
                        "        \"formula\" : \"x - avgForReferences(y)\",\n" +
                        "        \"formulaParams\" : {\n" +
                        "          \"y\" : {\n" +
                        "            \"type\" : \"BACK_DEVICE_REFERENCE_PARAM\",\n" +
                        "            \"targetDataStream\" : {\n" +
                        "              \"productId\" : 1,\n" +
                        "              \"pin\" : 1,\n" +
                        "              \"pinType\" : \"VIRTUAL\"\n" +
                        "            }\n" +
                        "          },\n" +
                        "          \"x\" : {\n" +
                        "            \"type\" : \"SAME_STREAM_PARAM\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  } ]\n" +
                        "}";

        client.editRuleGroup(json);
        client.verifyResult(ok(2));

        client.getRuleGroup();
        ruleGroup = client.parseRuleGroup(3);
        assertNotNull(ruleGroup);
    }

    @Test
    public void testRuleEngineForAiriusCaseWith0ReferencedDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product floorProduct = new Product();
        floorProduct.name = "Airius Floor Sensor";
        floorProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Floor Sensor", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(floorProduct);
        ProductDTO floorProductFromApi = client.parseProductDTO(1);
        assertNotNull(floorProductFromApi);

        Product fanProduct = new Product();
        fanProduct.name = "Airius Fan Product";
        fanProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Fan Product", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, -1)
        };

        client.createProduct(fanProduct);
        ProductDTO fanProductFromApi = client.parseProductDTO(2);
        assertNotNull(fanProductFromApi);

        short floorSourcePin = 1;
        short floorTargetPin = 2;

        short fanSourcePin = 1;

        //if floorProduct.v1 updated setPin floorProduct.v2 = x - y;
        //x = floorProduct.v1;
        //y = back_refefence_for_floorProduct

        DataStreamTrigger trigger = new DataStreamTrigger(floorProductFromApi.id, floorSourcePin);
        TriggerUpdatedCondition numberUpdatedCondition = new TriggerUpdatedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new SameDataStreamFormulaParam(),
                        "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        SetNumberPinAction setNumberPinAction = new SetNumberPinAction(floorProductFromApi.id, floorTargetPin, formulaValue);

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

        TestHardClient fanHardClient = new TestHardClient("localhost", properties.getHttpPort());
        fanHardClient.start();
        fanHardClient.login(createdFanDevice.token);
        fanHardClient.verifyResult(ok(1));
        fanHardClient.hardware(fanSourcePin, "40");

        TestHardClient floorHardClient = new TestHardClient("localhost", properties.getHttpPort());
        floorHardClient.start();
        floorHardClient.login(createdFloorDevice.token);
        floorHardClient.verifyResult(ok(1));
        floorHardClient.hardware(floorSourcePin, "42");

        floorHardClient.sync(PinType.VIRTUAL, floorTargetPin);
        floorHardClient.never(any());
    }

    @Test
    public void testRuleEngineForAiriusCaseWith1ReferencedDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product floorProduct = new Product();
        floorProduct.name = "Airius Floor Sensor";
        floorProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Floor Sensor", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(floorProduct);
        ProductDTO floorProductFromApi = client.parseProductDTO(1);
        assertNotNull(floorProductFromApi);

        Product fanProduct = new Product();
        fanProduct.name = "Airius Fan Product";
        fanProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Fan Product", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, -1)
        };

        client.createProduct(fanProduct);
        ProductDTO fanProductFromApi = client.parseProductDTO(2);
        assertNotNull(fanProductFromApi);

        short floorSourcePin = 1;
        short floorTargetPin = 2;

        short fanSourcePin = 1;

        //if floorProduct.v1 updated setPin floorProduct.v2 = x - y;
        //x = floorProduct.v1;
        //y = back_refefence_for_floorProduct

        DataStreamTrigger trigger = new DataStreamTrigger(floorProductFromApi.id, floorSourcePin);
        TriggerUpdatedCondition numberUpdatedCondition = new TriggerUpdatedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new SameDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        SetNumberPinAction setNumberPinAction = new SetNumberPinAction(floorProductFromApi.id, floorTargetPin, formulaValue);

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
        floorHardClient.hardware(floorSourcePin, "42");

        floorHardClient.sync(PinType.VIRTUAL, floorTargetPin);
        floorHardClient.verifyResult(hardware(3, "vw " + floorTargetPin + " 2.0"));
    }

    @Test
    public void testRuleEngineForAiriusCaseWith2ReferencedDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product floorProduct = new Product();
        floorProduct.name = "Airius Floor Sensor";
        floorProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Floor Sensor", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(floorProduct);
        ProductDTO floorProductFromApi = client.parseProductDTO(1);
        assertNotNull(floorProductFromApi);

        Product fanProduct = new Product();
        fanProduct.name = "Airius Fan Product";
        fanProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Fan Product", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, -1)
        };

        client.createProduct(fanProduct);
        ProductDTO fanProductFromApi = client.parseProductDTO(2);
        assertNotNull(fanProductFromApi);

        short floorSourcePin = 1;
        short floorTargetPin = 2;

        short fanSourcePin = 1;

        //if floorProduct.v1 updated setPin floorProduct.v2 = x - y;
        //x = floorProduct.v1;
        //y = back_refefence_for_floorProduct

        DataStreamTrigger trigger = new DataStreamTrigger(floorProductFromApi.id, floorSourcePin);
        TriggerUpdatedCondition numberUpdatedCondition = new TriggerUpdatedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new SameDataStreamFormulaParam(),
                        "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        SetNumberPinAction setNumberPinAction = new SetNumberPinAction(floorProductFromApi.id, floorTargetPin, formulaValue);

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

        Device fanDevice2 = new Device();
        fanDevice2.name = "Fan Device 2";
        fanDevice2.productId = fanProductFromApi.id;
        client.createDevice(fanDevice2);
        Device createdFanDevice2 = client.parseDevice(8);
        assertNotNull(createdFanDevice2);

        client.getDevicesByReferenceMetafield(createdFanDevice2.id, 3);
        DeviceDTO[] deviceDTOS2 = client.parseDevicesDTO(9);
        assertNotNull(deviceDTOS2);
        assertEquals(1, deviceDTOS2.length);
        assertEquals(floorDevice.name, deviceDTOS2[0].name);
        client.updateDeviceMetafield(createdFanDevice2.id,
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, deviceDTOS2[0].id));
        client.verifyResult(ok(10));


        TestHardClient fanHardClient = new TestHardClient("localhost", properties.getHttpPort());
        fanHardClient.start();
        fanHardClient.login(createdFanDevice.token);
        fanHardClient.verifyResult(ok(1));
        fanHardClient.hardware(fanSourcePin, "40");

        TestHardClient fanHardClient2 = new TestHardClient("localhost", properties.getHttpPort());
        fanHardClient2.start();
        fanHardClient2.login(createdFanDevice2.token);
        fanHardClient2.verifyResult(ok(1));
        fanHardClient2.hardware(fanSourcePin, "44");

        TestHardClient floorHardClient = new TestHardClient("localhost", properties.getHttpPort());
        floorHardClient.start();
        floorHardClient.login(createdFloorDevice.token);
        floorHardClient.verifyResult(ok(1));
        floorHardClient.hardware(floorSourcePin, "42");

        //expecting 42 - avg(40 + 44)
        floorHardClient.sync(PinType.VIRTUAL, floorTargetPin);
        floorHardClient.verifyResult(hardware(3, "vw " + floorTargetPin + " 0.0"));
    }

    @Test
    public void testRuleEngineForAiriusCaseWith2ReferencedAndBothSides() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product floorProduct = new Product();
        floorProduct.name = "Airius Floor Sensor";
        floorProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Floor Sensor", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(floorProduct);
        ProductDTO floorProductFromApi = client.parseProductDTO(1);
        assertNotNull(floorProductFromApi);

        Product fanProduct = new Product();
        fanProduct.name = "Airius Fan Product";
        fanProduct.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "Airius Fan Product", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, -1)
        };

        client.createProduct(fanProduct);
        ProductDTO fanProductFromApi = client.parseProductDTO(2);
        assertNotNull(fanProductFromApi);

        short floorSourcePin = 1;
        short floorTargetPin = 2;

        short fanSourcePin = 1;
        short fanTargetPin = 2;

        //if floorProduct.v1 updated setPin floorProduct.v2 = x - y;
        //x = floorProduct.v1;
        //y = back_refefence_for_floorProduct
        DataStreamTrigger trigger = new DataStreamTrigger(floorProductFromApi.id, floorSourcePin);
        TriggerUpdatedCondition numberUpdatedCondition = new TriggerUpdatedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new SameDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        SetNumberPinAction setNumberPinAction = new SetNumberPinAction(floorProductFromApi.id, floorTargetPin, formulaValue);
        Rule rule1 = new Rule("Airius rule for floor", trigger, numberUpdatedCondition, setNumberPinAction);


        DataStreamTrigger trigger2 = new DataStreamTrigger(fanProductFromApi.id, fanSourcePin);
        TriggerUpdatedCondition numberUpdatedCondition2 = new TriggerUpdatedCondition();
        FormulaValue formulaValue2 = new FormulaValue(
                "x - y",
                Map.of("x", new DeviceReferenceFormulaParam(floorProductFromApi.id, floorSourcePin),
                       "y", new SameDataStreamFormulaParam())
        );
        SetNumberPinAction setNumberPinAction2 = new SetNumberPinAction(fanProductFromApi.id, fanTargetPin, formulaValue2);
        Rule rule2 = new Rule("Airius rule for fan", trigger2, numberUpdatedCondition2, setNumberPinAction2);

        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule1,
                rule2
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

        Device fanDevice2 = new Device();
        fanDevice2.name = "Fan Device 2";
        fanDevice2.productId = fanProductFromApi.id;
        client.createDevice(fanDevice2);
        Device createdFanDevice2 = client.parseDevice(8);
        assertNotNull(createdFanDevice2);

        client.getDevicesByReferenceMetafield(createdFanDevice2.id, 3);
        DeviceDTO[] deviceDTOS2 = client.parseDevicesDTO(9);
        assertNotNull(deviceDTOS2);
        assertEquals(1, deviceDTOS2.length);
        assertEquals(floorDevice.name, deviceDTOS2[0].name);
        client.updateDeviceMetafield(createdFanDevice2.id,
                new DeviceReferenceMetaField(3, "Floor reference", null, true, false, false, null, new int[] {floorProductFromApi.id}, deviceDTOS2[0].id));
        client.verifyResult(ok(10));


        TestHardClient fanHardClient = new TestHardClient("localhost", properties.getHttpPort());
        fanHardClient.start();
        fanHardClient.login(createdFanDevice.token);
        fanHardClient.verifyResult(ok(1));
        fanHardClient.hardware(fanSourcePin, "40");

        TestHardClient fanHardClient2 = new TestHardClient("localhost", properties.getHttpPort());
        fanHardClient2.start();
        fanHardClient2.login(createdFanDevice2.token);
        fanHardClient2.verifyResult(ok(1));
        fanHardClient2.hardware(fanSourcePin, "44");

        TestHardClient floorHardClient = new TestHardClient("localhost", properties.getHttpPort());
        floorHardClient.start();
        floorHardClient.login(createdFloorDevice.token);
        floorHardClient.verifyResult(ok(1));
        floorHardClient.hardware(floorSourcePin, "42");

        //expecting 42 - avg(40 + 44)
        floorHardClient.sync(PinType.VIRTUAL, floorTargetPin);
        floorHardClient.verifyResult(hardware(3, "vw " + floorTargetPin + " 0.0"));

        //expecting 42 - 47
        fanHardClient2.hardware(fanSourcePin, "47");
        fanHardClient2.sync(PinType.VIRTUAL, fanTargetPin);
        fanHardClient2.verifyResult(hardware(4, "vw " + fanTargetPin + " -5.0"));
    }

}

