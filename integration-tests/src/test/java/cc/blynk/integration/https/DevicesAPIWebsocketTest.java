package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.OrganizationDTO;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.protocol.enums.Command;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.APIBaseTest.createMeasurementMeta;
import static cc.blynk.integration.APIBaseTest.createNumberMeta;
import static cc.blynk.integration.APIBaseTest.createTextMeta;
import static cc.blynk.integration.TestUtil.illegalCommand;
import static cc.blynk.integration.TestUtil.illegalCommandBody;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DevicesAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void createDevice() throws Exception {
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
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) createdDevice.metaFields[0];
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);

        newDevice.name = "My New Device2";
        client.createDevice(orgId, newDevice);
        createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        assertEquals("My New Device2", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        numberMetaField = (NumberMetaField) createdDevice.metaFields[0];
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(4);
        assertNotNull(devices);
        assertEquals(2, devices.length);
    }

    @Test
    public void createAndDeleteDevice() throws Exception {
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
        assertEquals("My New Device", createdDevice.name);

        client.deleteDevice(orgId, createdDevice.id);
        client.verifyResult(ok(3));
    }

    @Test
    public void createDeviceForAnotherOrganization() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("My SubOrg", "Some TimeZone", "/static/logo.png", false, orgId);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApi = client.parseOrganizationDTO(2);
        assertNotNull(fromApi);
        assertEquals(orgId, fromApi.parentId);
        assertEquals(organization.name, fromApi.name);
        assertEquals(organization.tzName, fromApi.tzName);
        assertNotNull(fromApi.products);
        assertEquals(1, fromApi.products.length);
        assertEquals(fromApiProduct.id + 1, fromApi.products[0].id);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApi.products[0].id;

        client.createDevice(fromApi.id, newDevice);
        Device createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(0, createdDevice.metaFields.length);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals("super@blynk.cc", createdDevice.activatedBy);

        client.getDevices(fromApi.id);
        DeviceDTO[] devices = client.parseDevicesDTO(4);
        assertNotNull(devices);
        assertEquals(1, devices.length);
        assertEquals(product.name, devices[0].productName);
        assertEquals("My SubOrg", devices[0].orgName);

        client.getDevice(fromApi.id, devices[0].id);
        Device device = client.parseDevice(5);
        assertNotNull(device);
    }

    @Test
    public void createDeviceForAnotherOrganizationAndIsNotVisibleForParentOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("My Org ffff", "Some TimeZone", "/static/logo.png", false, orgId);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApi = client.parseOrganizationDTO(2);
        assertNotNull(fromApi);
        assertEquals(orgId, fromApi.parentId);
        assertEquals(organization.name, fromApi.name);
        assertEquals(organization.tzName, fromApi.tzName);
        assertNotNull(fromApi.products);
        assertEquals(1, fromApi.products.length);
        assertEquals(fromApiProduct.id + 1, fromApi.products[0].id);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApi.products[0].id;

        client.createDevice(fromApi.id, newDevice);
        Device createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(0, createdDevice.metaFields.length);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals("super@blynk.cc", createdDevice.activatedBy);

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(4);
        assertNotNull(devices);
        assertEquals(0, devices.length);

        client.getDevices(fromApi.id);
        devices = client.parseDevicesDTO(5);
        assertNotNull(devices);
        assertNotNull(devices);
        assertEquals(1, devices.length);
    }

    @Test
    public void createDeviceAndUpdateMetafield() throws Exception {
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
        Device device = client.parseDevice(2);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(2, device.metaFields.length);

        NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
        assertEquals(getUserName(), device.activatedBy);
        assertEquals(0, device.metadataUpdatedAt);
        assertNull(device.metadataUpdatedBy);

        MetaField updatedMetaField = createNumberMeta(1, "Jopa2", 123D);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(ok(3));

        client.getDevice(orgId, device.id);
        device = client.parseDevice(4);
        assertNotNull(device);
        assertEquals(System.currentTimeMillis(), device.metadataUpdatedAt, 5000);
        assertEquals(getUserName(), device.metadataUpdatedBy);

        MetaField updatedMetaField2 = createNumberMeta(3, "Jopa2", 123D);
        client.updateDeviceMetafield(device.id, updatedMetaField2);
        client.verifyResult(webJson(5, "Metafield with passed id not found."));
    }

    @Test
    public void createDeviceAndUpdateUnitMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createMeasurementMeta(1, "Jopa", 213, MeasurementUnit.Celsius),
                createTextMeta(2, "Device Name", "My Default device Name")
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device device = client.parseDevice(2);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(2, device.metaFields.length);

        MetaField updatedMetaField = new MeasurementUnitMetaField(1, "Jopa", new int[] {2}, false, false, false, null, null, 0, 1000, 123, 1);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(webJson(3, "Metafield is not valid. Units field is empty."));

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.updateDeviceMetafield(device.id, updatedMetaField);
        appClient.verifyResult(illegalCommandBody(2));
    }

    @Test
    public void getAllDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(1);
        assertNotNull(devices);
        assertEquals(0, devices.length);
    }

    @Test
    public void getDeviceByIdNotFound() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getDevice(orgId, 1111);
        client.verifyResult(webJson(1, "Device not found."));
    }

    @Test
    public void checkDeviceOrgName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.logoUrl = "/logoUrl";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        String responseString = client.getBody(2);
        TestDevice device = JsonParser.MAPPER.readValue(responseString, TestDevice.class);
        assertNotNull(device);
        assertEquals("My New Device", device.name);
        assertEquals("Blynk Inc.", device.orgName);
        assertEquals("My product", device.productName);
        assertEquals("/logoUrl", device.productLogoUrl);
    }

    @Test
    public void getDeviceMetafieldFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.logoUrl = "MyLogo.png";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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

        appClient.getDevice(createdDevice.id);
        DeviceDTO deviceDTO = appClient.parseDeviceDTO(2);

        assertNotNull(deviceDTO);
        assertEquals("My product", deviceDTO.productName);
        assertEquals("MyLogo.png", deviceDTO.productLogoUrl);

        MetaField[] metaFields = deviceDTO.metaFields;
        assertNotNull(metaFields);
        assertEquals(2, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);
    }

    @Test
    public void getDeviceFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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
        assertEquals(2, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);
    }

    @Test
    public void updateDeviceMetafieldFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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
        assertEquals(2, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        NumberMetaField newMeta = createNumberMeta(1, "Jopa", 10000D, true);
        appClient.updateDeviceMetafield(createdDevice.id, newMeta);
        appClient.verifyResult(ok(3));

        appClient.getDeviceMetafield(createdDevice.id);
        metaFields = appClient.parseMetafields(4);
        assertNotNull(metaFields);
        assertEquals(2, metaFields.length);
        numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(10000D, numberMetaField.value, 0.1);
    }

    @Test
    public void updateDeviceMetafieldFromMobileAndWebGetsUpdate() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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
        assertEquals(2, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        client.track(createdDevice.id);
        client.verifyResult(ok(3));
        NumberMetaField newMeta = createNumberMeta(1, "Jopa", 10000D, true);
        appClient.updateDeviceMetafield(createdDevice.id, newMeta);
        appClient.verifyResult(ok(3));
        client.verifyResult(new StringMessage(3, Command.WEB_UPDATE_DEVICE_METAFIELD,
                String.valueOf(createdDevice.id) + BODY_SEPARATOR + newMeta.toString()));

        appClient.getDeviceMetafield(createdDevice.id);
        metaFields = appClient.parseMetafields(4);
        assertNotNull(metaFields);
        assertEquals(2, metaFields.length);
        numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(10000D, numberMetaField.value, 0.1);

        AppWebSocketClient client2 = loggedDefaultClient(getUserName(), "1");
        client2.track(createdDevice.id);
        client2.verifyResult(ok(1));

        newMeta = createNumberMeta(1, "Jopa", 10001D, true);
        client.updateDeviceMetafield(createdDevice.id, newMeta);
        client.verifyResult(ok(4));

        client2.verifyResult(new StringMessage(4, Command.WEB_UPDATE_DEVICE_METAFIELD,
                String.valueOf(createdDevice.id) + BODY_SEPARATOR + newMeta.toString()));
    }

    @Test
    public void updateDeviceMetafieldFromMobileviaBatch() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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
        assertEquals(2, metaFields.length);

        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);

        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 1000D, true),
                createTextMeta(2, "Device Name", "New Name", true)
        };
        appClient.updateDeviceMetafields(createdDevice.id, metaFields);
        appClient.verifyResult(ok(3));

        appClient.getDeviceMetafield(createdDevice.id);
        metaFields = appClient.parseMetafields(4);
        assertNotNull(metaFields);
        assertEquals(2, metaFields.length);

        numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1000D, numberMetaField.value, 0.1);

        textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals("New Name", textMetaField.value);
    }

    @Test
    public void updateDeviceMetafieldFromMobileviaBatchFails() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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
        assertEquals(2, metaFields.length);

        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);

        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        metaFields = new MetaField[] {
                createNumberMeta(10, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
        };
        appClient.updateDeviceMetafields(createdDevice.id, metaFields);
        appClient.verifyResult(illegalCommand(3));
    }

    @Test
    public void updateDeviceNameMetafieldFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
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
        assertEquals(2, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        TextMetaField textMetaField = (TextMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        MetaField updatedMeta = createTextMeta(2, "Device Name", "Updated Name", true);
        appClient.updateDeviceMetafield(createdDevice.id, updatedMeta);
        appClient.verifyResult(ok(3));

        appClient.getDeviceMetafield(createdDevice.id);
        metaFields = appClient.parseMetafields(4);
        assertNotNull(metaFields);
        assertEquals(2, metaFields.length);
        assertEquals("Updated Name", ((TextMetaField) metaFields[1]).value);

        client.getDevice(orgId, createdDevice.id);
        Device device = client.parseDevice(3);
        assertEquals("Updated Name", device.name);
    }

    @Test
    @Ignore
    //todo finish
    public void getDevicesWithSortingByMultiFields2() {
    }

    @Test
    @Ignore
    //todo finish
    public void getDevicesWithSorting() {
    }

    public static class TestDevice extends Device {

        String orgName;

        String productName;

        String productLogoUrl;

    }
}
