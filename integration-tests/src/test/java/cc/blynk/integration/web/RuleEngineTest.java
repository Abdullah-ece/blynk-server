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
import cc.blynk.server.core.processors.rules.actions.BaseAction;
import cc.blynk.server.core.processors.rules.actions.SetDataStreamAction;
import cc.blynk.server.core.processors.rules.conditions.TriggerChangedCondition;
import cc.blynk.server.core.processors.rules.datastream.DeviceRuleDataStream;
import cc.blynk.server.core.processors.rules.datastream.ProductRuleDataStream;
import cc.blynk.server.core.processors.rules.triggers.ProductDataStreamTrigger;
import cc.blynk.server.core.processors.rules.value.FormulaValue;
import cc.blynk.server.core.processors.rules.value.params.BackDeviceReferenceFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.DeviceReferenceFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.TriggerDataStreamFormulaParam;
import cc.blynk.server.core.processors.rules.value.params.TriggerDeviceDataStreamFormulaParam;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.TestUtil.consumeJsonPinValues;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
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

        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(1, floorSourcePin)};
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(1, fanSourcePin))
        );

        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
            new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };
        Rule rule = new Rule("Airius rule", triggers, numberUpdatedCondition, setDeviceDataStreamAction);
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

        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin)};
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDataStreamFormulaParam(),
                        "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };

        Rule rule = new Rule("Airius rule", triggers, numberUpdatedCondition, setDeviceDataStreamAction);
        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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

        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin)};
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };

        Rule rule = new Rule("Airius rule", triggers, numberUpdatedCondition, setDeviceDataStreamAction);
        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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

        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin)};
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDataStreamFormulaParam(),
                        "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };

        Rule rule = new Rule("Airius rule", triggers, numberUpdatedCondition, setDeviceDataStreamAction);
        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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
        client.createDevice(orgId, fanDevice2);
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
        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin)};
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };
        Rule rule1 = new Rule("Airius rule for floor", triggers, numberUpdatedCondition, setDeviceDataStreamAction);


        ProductDataStreamTrigger[] triggers2 = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(fanProductFromApi.id, fanSourcePin)};
        TriggerChangedCondition numberUpdatedCondition2 = new TriggerChangedCondition();
        FormulaValue formulaValue2 = new FormulaValue(
                "x - y",
                Map.of("x", new DeviceReferenceFormulaParam(floorProductFromApi.id, floorSourcePin),
                       "y", new TriggerDataStreamFormulaParam())
        );
        DeviceRuleDataStream[] facActionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(fanTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction2 = new BaseAction[] {
                new SetDataStreamAction(facActionDataStreams, formulaValue2)
        };
        Rule rule2 = new Rule("Airius rule for fan", triggers2, numberUpdatedCondition2, setDeviceDataStreamAction2);

        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule1,
                rule2
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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
        client.createDevice(orgId, fanDevice2);
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

    @Test
    public void testRuleEngineForAiriusCaseWith2ReferencedAndBothSidesAndMultiTrigger() throws Exception {
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
        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {
                new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin),
                new ProductDataStreamTrigger(fanProductFromApi.id, fanSourcePin)
        };
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDeviceDataStreamFormulaParam(floorProductFromApi.id, floorSourcePin),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[]{
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };
        Rule rule1 = new Rule("Airius rule for floor", triggers, numberUpdatedCondition, setDeviceDataStreamAction);


        ProductDataStreamTrigger[] triggers2 = new ProductDataStreamTrigger[] {
                new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin),
                new ProductDataStreamTrigger(fanProductFromApi.id, fanSourcePin)
        };
        TriggerChangedCondition numberUpdatedCondition2 = new TriggerChangedCondition();
        FormulaValue formulaValue2 = new FormulaValue(
                "x - y",
                Map.of("x", new DeviceReferenceFormulaParam(floorProductFromApi.id, floorSourcePin),
                       "y", new TriggerDeviceDataStreamFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] facActionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(fanTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction2 = new BaseAction[]{
                new SetDataStreamAction(facActionDataStreams, formulaValue2)
        };
        Rule rule2 = new Rule("Airius rule for fan", triggers2, numberUpdatedCondition2, setDeviceDataStreamAction2);

        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule1,
                rule2
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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
        client.createDevice(orgId, fanDevice2);
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

    @Test
    public void testRuleEngineForAiriusCaseWith2ReferencedAndBothSidesAndMultiTriggerViaHttps() throws Exception {
        String httpsServerUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());
        CloseableHttpClient httpclient = getDefaultHttpsClient();

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

        //if floorProduct.v1 or fanProduct.v1 changed setPin trigger.v2 = x - y;
        //x = floorProduct.v1;
        //y = back_refefence_for_floorProduct
        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {
                new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin),
                new ProductDataStreamTrigger(fanProductFromApi.id, fanSourcePin)
        };
        TriggerChangedCondition valueChangedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDeviceDataStreamFormulaParam(floorProductFromApi.id, floorSourcePin),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );

        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };
        Rule rule1 = new Rule("Airius rule for floor", triggers, valueChangedCondition, setDeviceDataStreamAction);


        ProductDataStreamTrigger[] triggers2 = new ProductDataStreamTrigger[] {
                new ProductDataStreamTrigger(fanProductFromApi.id, fanSourcePin)
        };
        TriggerChangedCondition numberUpdatedCondition2 = new TriggerChangedCondition();
        FormulaValue formulaValue2 = new FormulaValue(
                "x - y",
                Map.of("x", new DeviceReferenceFormulaParam(floorProductFromApi.id, floorSourcePin),
                        "y", new TriggerDeviceDataStreamFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] facActionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(fanTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction2 = new BaseAction[]{
                new SetDataStreamAction(facActionDataStreams, formulaValue2)
        };
        Rule rule2 = new Rule("Airius rule for fan", triggers2, numberUpdatedCondition2, setDeviceDataStreamAction2);
        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule1,
                rule2
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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
        client.createDevice(orgId, fanDevice2);
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


        HttpPut put = new HttpPut(httpsServerUrl + createdFanDevice.token + "/update/v" + fanSourcePin);
        put.setEntity(new StringEntity("[\"40\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        put = new HttpPut(httpsServerUrl + createdFanDevice2.token + "/update/v" + fanSourcePin);
        put.setEntity(new StringEntity("[\"44\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        put = new HttpPut(httpsServerUrl + createdFloorDevice.token + "/update/v" + floorSourcePin);
        put.setEntity(new StringEntity("[\"42\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        //expecting 42 - avg(40 + 44)
        HttpGet get = new HttpGet(httpsServerUrl + createdFloorDevice.token + "/get/v" + floorTargetPin);
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("0.0", values.get(0));
        }

        put = new HttpPut(httpsServerUrl + createdFanDevice2.token + "/update/v" + fanSourcePin);
        put.setEntity(new StringEntity("[\"47\"]", ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        //expecting 42 - 47
        get = new HttpGet(httpsServerUrl + createdFanDevice2.token + "/get/v" + fanTargetPin);
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("-5.0", values.get(0));
        }

        //todo this should work
        get = new HttpGet(httpsServerUrl + createdFloorDevice.token + "/get/v" + floorTargetPin);
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<String> values = consumeJsonPinValues(response);
            assertEquals(1, values.size());
            assertEquals("-5.0", values.get(0));
        }

    }

    @Test
    public void testRuleEngineForAiriusCaseWith2ReferencedAndValuesUpdatedAtTheSameTime() throws Exception {
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
        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {new ProductDataStreamTrigger(floorProductFromApi.id, floorSourcePin)};
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDataStreamFormulaParam(),
                       "y", new BackDeviceReferenceFormulaParam(fanProductFromApi.id, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL),
                new ProductRuleDataStream(fanProductFromApi.id, fanTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };
        Rule rule1 = new Rule("Airius rule for floor", triggers, numberUpdatedCondition, setDeviceDataStreamAction);

        client.editRuleGroup(new RuleGroup(new Rule[] {
                rule1
        }));
        client.verifyResult(ok(3));

        Device floorDevice = new Device();
        floorDevice.name = "Floor Device";
        floorDevice.productId = floorProductFromApi.id;
        client.createDevice(orgId, floorDevice);
        Device createdFloorDevice = client.parseDevice(4);
        assertNotNull(createdFloorDevice);

        Device fanDevice = new Device();
        fanDevice.name = "Fan Device";
        fanDevice.productId = fanProductFromApi.id;
        client.createDevice(orgId, fanDevice);
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
        client.createDevice(orgId, fanDevice2);
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

        //here we expect that values that comes to floor sensor also triggers fan value update
        fanHardClient.sync(PinType.VIRTUAL, fanTargetPin);
        fanHardClient.verifyResult(hardware(3, "vw " + fanTargetPin + " 0.0"));

        fanHardClient2.sync(PinType.VIRTUAL, fanTargetPin);
        fanHardClient2.verifyResult(hardware(3, "vw " + fanTargetPin + " 0.0"));
    }

    @Test
    public void generateRequiredJson() throws Exception {
        int floorProductId = 7;
        short floorSourcePin = 1;
        short floorTargetPin = 31;

        int fanProductId = 2;
        short fanSourcePin = 1;
        short fanTargetPin = 31;

        ProductDataStreamTrigger[] triggers = new ProductDataStreamTrigger[] {
                new ProductDataStreamTrigger(floorProductId, floorSourcePin),
                new ProductDataStreamTrigger(fanProductId, fanSourcePin)
        };
        TriggerChangedCondition numberUpdatedCondition = new TriggerChangedCondition();
        FormulaValue formulaValue = new FormulaValue(
                "x - avgForReferences(y)",
                Map.of("x", new TriggerDeviceDataStreamFormulaParam(floorProductId, floorSourcePin),
                       "y", new BackDeviceReferenceFormulaParam(fanProductId, fanSourcePin))
        );
        DeviceRuleDataStream[] actionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(floorTargetPin, PinType.VIRTUAL),
                new ProductRuleDataStream(fanProductId, fanTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction = new BaseAction[] {
                new SetDataStreamAction(actionDataStreams, formulaValue)
        };
        Rule rule1 = new Rule("Airius rule for floor", triggers, numberUpdatedCondition, setDeviceDataStreamAction);


        ProductDataStreamTrigger[] triggers2 = new ProductDataStreamTrigger[] {
                new ProductDataStreamTrigger(floorProductId, floorSourcePin),
                new ProductDataStreamTrigger(fanProductId, fanSourcePin)
        };
        TriggerChangedCondition numberUpdatedCondition2 = new TriggerChangedCondition();
        FormulaValue formulaValue2 = new FormulaValue(
                "x - y",
                Map.of("x", new DeviceReferenceFormulaParam(floorProductId, floorSourcePin),
                        "y", new TriggerDeviceDataStreamFormulaParam(fanProductId, fanSourcePin))
        );
        DeviceRuleDataStream[] facActionDataStreams = new DeviceRuleDataStream[] {
                new DeviceRuleDataStream(fanTargetPin, PinType.VIRTUAL)
        };
        BaseAction[] setDeviceDataStreamAction2 = new BaseAction[] {
                new SetDataStreamAction(facActionDataStreams, formulaValue2)
        };
        Rule rule2 = new Rule("Airius rule for fan", triggers2, numberUpdatedCondition2, setDeviceDataStreamAction2);

        System.out.println(
                JsonParser.MAPPER
                        .writerWithDefaultPrettyPrinter()
                        .forType(RuleGroup.class)
                        .writeValueAsString(new RuleGroup(new Rule[] {
                                rule1,
                                rule2
                        }))
        );
    }

}

