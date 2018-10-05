package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.model.web.product.metafields.ListMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.APIBaseTest.createMeasurementMeta;
import static cc.blynk.integration.APIBaseTest.createNumberMeta;
import static cc.blynk.integration.APIBaseTest.createTextMeta;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.hardwareConnected;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.10.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DevicesProvisionFlowTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void metafieldsForMobileFilteredByIncludeInProvision() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                createTextMeta(2, "Device Name", "My Default device Name")
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getDeviceMetafield(createdDevice.id);
        MetaField[] metaFields = appClient.parseMetafields(2);
        assertNotNull(metaFields);
        assertEquals(0, metaFields.length);
    }

    @Test
    public void testGetDevicesByReferenceMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                new DeviceReferenceMetaField(2, "Device Ref", new int[] {1}, true, true, true, null, null, -1L)
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        fromApiProduct.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                new DeviceReferenceMetaField(2, "Device Ref", new int[] {1}, true, true, true, null, new int[] {fromApiProduct.id}, -1L)
        };

        client.updateProduct(orgId, fromApiProduct);
        fromApiProduct = client.parseProduct(2);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getDeviceMetafield(createdDevice.id);
        MetaField[] metaFields = appClient.parseMetafields(2);
        assertNotNull(metaFields);
        assertEquals(1, metaFields.length);
        MetaField metaField = metaFields[0];
        assertTrue(metaField instanceof DeviceReferenceMetaField);

        appClient.getDevicesByReferenceMetafield(createdDevice.id, metaField.id);
        Device[] deviceDTOS = appClient.parseDevices(3);
        assertNotNull(deviceDTOS);
        assertEquals(1, deviceDTOS.length);
        assertEquals("My New Device", deviceDTOS[0].name);
    }

    @Test
    public void testProvisionFlow() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createMeasurementMeta(1, "Jopa", 1, MeasurementUnit.Celsius),
                createTextMeta(2, "Device Name", "My Default device Name")
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                new MeasurementUnitMetaField(1, "Jopa", new int[] {2}, true, false, false, null, MeasurementUnit.Celsius, 0, 1000, 123, 1),
                createTextMeta(2, "Device Name", "My Default device Name"),
                new ListMetaField(3, "Template Id", new int[] {1}, false, false, true, null, new String[] {"TMPL0001"}, null)
        };
        client.createProduct(orgId, product2);
        Product fromApiProduct2 = client.parseProduct(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(1, newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(hardwareConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(hardwareConnected(2, "1-" + deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(1, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct2.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(hardwareConnected(1, "1-" + deviceFromApi.id));
    }

    @Test
    public void testProvisionFlowNoTemplateIdMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                new MeasurementUnitMetaField(1, "Jopa", new int[] {2}, true, false, false, null, MeasurementUnit.Celsius, 0, 1000, 123, 1),
                new TextMetaField(2, "Device Name", new int[] {1}, false, false, true, null, "My Default device Name"),
                new ListMetaField(3, "Template Id", new int[] {1}, false, false, true, null, new String[] {"TMPL0002"}, null)
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        client.createProduct(orgId, product2);
        Product fromApiProduct2 = client.parseProduct(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;
        newDevice.productId = fromApiProduct.id;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(1, newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(hardwareConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(hardwareConnected(2, "1-" + deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(1, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(hardwareConnected(1, "1-" + deviceFromApi.id));
    }

    @Test
    public void testProvisionFlowNoTemplateIdInDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                new MeasurementUnitMetaField(1, "Jopa", new int[] {2}, true, false, false, null, MeasurementUnit.Celsius, 0, 1000, 123, 1),
                new TextMetaField(2, "Device Name", new int[] {1}, false, false, true, null, "My Default device Name")
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                new MeasurementUnitMetaField(1, "Jopa", new int[] {2}, true, false, false, null, MeasurementUnit.Celsius, 0, 1000, 123, 1),
                new TextMetaField(2, "Device Name", new int[] {1}, false, false, true, null, "My Default device Name"),
                new ListMetaField(3, "Template Id", new int[] {1}, false, false, true, null, new String[] {"TMPL0001"}, null)
        };
        client.createProduct(orgId, product2);
        Product fromApiProduct2 = client.parseProduct(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(1, newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(hardwareConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(hardwareConnected(2, "1-" + deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(1, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertNull(provisionedDevice.hardwareInfo.templateId);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(hardwareConnected(1, "1-" + deviceFromApi.id));
    }

}
