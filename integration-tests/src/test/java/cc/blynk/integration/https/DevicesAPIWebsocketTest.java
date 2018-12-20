package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.DeviceNameMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.WebLineGraph;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.server.core.protocol.enums.Command;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.utils.TokenGeneratorUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.APIBaseTest.createMeasurementMeta;
import static cc.blynk.integration.APIBaseTest.createNumberMeta;
import static cc.blynk.integration.APIBaseTest.createTextMeta;
import static cc.blynk.integration.TestUtil.createWebLabelWidget;
import static cc.blynk.integration.TestUtil.createWebLineGraph;
import static cc.blynk.integration.TestUtil.invalidToken;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.updateProductWebDash;
import static cc.blynk.integration.TestUtil.webJson;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                createNumberMeta(3, "Jopa", 123D)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;


        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(3, createdDevice.metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) createdDevice.metaFields[2];
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
        assertEquals(3, createdDevice.metaFields.length);
        numberMetaField = (NumberMetaField) createdDevice.metaFields[2];
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(4);
        assertNotNull(devices);
        assertEquals(3, devices.length);
    }

    @Test
    public void createAndDeleteDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true),
                createNumberMeta(3, "Jopa", 123D)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("My SubOrg", "Some TimeZone", "/static/logo.png", false, -1);
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

        client.trackOrg(fromApi.id);
        client.verifyResult(ok(3));
        client.createDevice(-1, newDevice);
        Device createdDevice = client.parseDevice(4);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals("super@blynk.cc", createdDevice.activatedBy);

        client.getDevices(-1);
        DeviceDTO[] devices = client.parseDevicesDTO(5);
        assertNotNull(devices);
        assertEquals(1, devices.length);
        assertEquals(product.name, devices[0].productName);
        assertEquals("My SubOrg", devices[0].orgName);

        client.getDevice(-1, devices[0].id);
        Device device = client.parseDevice(6);
        assertNotNull(device);
    }

    @Test
    public void createDeviceForAnotherOrganizationAndIsNotVisibleForParentOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("My Org ffff", "Some TimeZone", "/static/logo.png", false, -1);
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

        client.trackOrg(fromApi.id);
        client.verifyResult(ok(3));
        client.createDevice(-1, newDevice);
        Device createdDevice = client.parseDevice(4);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals("super@blynk.cc", createdDevice.activatedBy);

        client.trackOrg(orgId);
        client.verifyResult(ok(5));
        client.getDevices(-1);
        DeviceDTO[] devices = client.parseDevicesDTO(6);
        assertNotNull(devices);
        assertEquals(1, devices.length);

        client.trackOrg(fromApi.id);
        client.verifyResult(ok(7));
        client.getDevices(-1);
        devices = client.parseDevicesDTO(8);
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
                createTextMeta(2, "Device Name", "My Default device Name"),
                createDeviceNameMeta(3, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(4, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device device = client.parseDevice(2);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(4, device.metaFields.length);

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

        MetaField updatedMetaField2 = createNumberMeta(5, "Jopa2", 123D);
        client.updateDeviceMetafield(device.id, updatedMetaField2);
        client.verifyResult(webJson(5, "Metafield with passed id not found."));
    }

    @Test
    public void createDeviceAndUpdateWithWrongMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                createTextMeta(2, "Device Name", "My Default device Name"),
                createDeviceNameMeta(3, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(4, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device device = client.parseDevice(2);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(4, device.metaFields.length);

        MetaField updatedMetaField = createDeviceOwnerMeta(4, "Device Owner", "aaaa", true);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(webJson(3, "Device owner metafield value is not correct email."));

        updatedMetaField = createDeviceOwnerMeta(4, "Device Owner", "", true);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(webJson(4, "Device owner metafield value is empty."));

        updatedMetaField = createDeviceOwnerMeta(4, "Device Owner", "a@a.com", true);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(ok(5));
    }

    @Test
    public void createDeviceAndUpdateUnitMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createMeasurementMeta(1, "Jopa", 213, MeasurementUnit.Celsius),
                createTextMeta(2, "Device Name", "My Default device Name"),
                createDeviceNameMeta(3, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(4, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device device = client.parseDevice(2);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(4, device.metaFields.length);

        MetaField updatedMetaField = new MeasurementUnitMetaField(1, "Jopa", new int[] {2}, false, false, false, null, null, 0, 1000, 123, 1);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(webJson(3, "Metafield is not valid. Units field is empty."));

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.updateDeviceMetafield(device.id, updatedMetaField);
        appClient.verifyResult(webJson(2, "Metafield is not valid. Units field is empty."));
    }

    @Test
    public void getAllDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(1);
        assertNotNull(devices);
        assertEquals(1, devices.length);
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
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        assertEquals(3, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals("My Default device Name", deviceNameMetaField.value);
    }

    @Test
    public void mobileListShouldNotReturnMetaAndWebDash() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.logoUrl = "MyLogo.png";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertNotNull(fromApiProduct.webDashboard);

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

        appClient.getDevices(orgId);
        DeviceDTO[] deviceDTOs = appClient.parseDevicesDTO(2);

        assertNotNull(deviceDTOs);
        assertEquals(2, deviceDTOs.length);
        DeviceDTO newDeviceDTO = deviceDTOs[1];
        assertEquals("My product", newDeviceDTO.productName);
        assertEquals("MyLogo.png", newDeviceDTO.productLogoUrl);
        assertNull(newDeviceDTO.metaFields);
        assertNull(newDeviceDTO.webDashboard);
    }

    @Test
    public void getDeviceFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals("My Default device Name", deviceNameMetaField.value);
    }

    @Test
    public void updateDeviceMetafieldFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        DeviceNameMetaField textMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        NumberMetaField newMeta = createNumberMeta(1, "Jopa", 10000D, true);
        appClient.updateDeviceMetafield(createdDevice.id, newMeta);
        appClient.verifyResult(ok(3));

        appClient.getDevice(createdDevice.id, true);
        device = appClient.parseDevice(4);
        metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
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
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals("My Default device Name", deviceNameMetaField.value);

        client.trackDevice(createdDevice.id);
        client.verifyResult(ok(3));
        NumberMetaField newMeta = createNumberMeta(1, "Jopa", 10000D, true);
        appClient.updateDeviceMetafield(createdDevice.id, newMeta);
        appClient.verifyResult(ok(3));
        client.verifyResult(new StringMessage(3, Command.WEB_UPDATE_DEVICE_METAFIELD,
                String.valueOf(createdDevice.id) + BODY_SEPARATOR + newMeta.toString()));

        appClient.getDevice(createdDevice.id, true);
        device = appClient.parseDevice(4);
        metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(10000D, numberMetaField.value, 0.1);

        AppWebSocketClient client2 = loggedDefaultClient(getUserName(), "1");
        client2.trackDevice(createdDevice.id);
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
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);

        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);

        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals("My Default device Name", deviceNameMetaField.value);

        metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 1000D, true),
                createDeviceNameMeta(2, "Device Name", "New Name", true),
        };
        appClient.updateDeviceMetafields(createdDevice.id, metaFields);
        appClient.verifyResult(ok(3));

        appClient.getDevice(createdDevice.id, true);
        device = appClient.parseDevice(4);
        metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);

        numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1000D, numberMetaField.value, 0.1);

        deviceNameMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals("New Name", deviceNameMetaField.value);
    }

    @Test
    public void updateDeviceMetafieldFromMobileviaBatchFails() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);

        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);

        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals("My Default device Name", deviceNameMetaField.value);

        metaFields = new MetaField[] {
                createNumberMeta(10, "Jopa", 123D, true),
                createTextMeta(2, "Device Name", "My Default device Name", true)
        };
        appClient.updateDeviceMetafields(createdDevice.id, metaFields);
        appClient.verifyResult(webJson(3, "Metafield with passed id not found."));
    }

    @Test
    public void updateDeviceNameMetafieldFromMobile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) metaFields[0];
        assertEquals(1, numberMetaField.id);
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
        assertEquals(123D, numberMetaField.value, 0.1);
        DeviceNameMetaField textMetaField = (DeviceNameMetaField) metaFields[1];
        assertEquals(2, textMetaField.id);
        assertEquals("Device Name", textMetaField.name);
        assertEquals(1, textMetaField.roleIds[0]);
        assertEquals("My Default device Name", textMetaField.value);

        MetaField updatedMeta = createDeviceNameMeta(2, "Device Name", "Updated Name", true);
        appClient.updateDeviceMetafield(createdDevice.id, updatedMeta);
        appClient.verifyResult(ok(3));

        appClient.getDevice(createdDevice.id, true);
        device = appClient.parseDevice(4);
        metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        assertEquals("Updated Name", ((DeviceNameMetaField) metaFields[1]).value);

        client.getDevice(orgId, createdDevice.id);
        device = client.parseDevice(3);
        assertEquals("Updated Name", device.name);
    }

    @Test
    public void createDeviceWithWidgets() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };
        product.webDashboard = new WebDashboard(new Widget[] {
                createWebLabelWidget(1, "123"),
                createWebLineGraph(2, "graph"),
        });

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.metaFields);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);
        assertNotNull(createdDevice.webDashboard);
        assertEquals(2, createdDevice.webDashboard.widgets.length);
        assertEquals("123", createdDevice.webDashboard.widgets[0].label);
        assertEquals("graph", createdDevice.webDashboard.widgets[1].label);
    }

    @Test
    public void createDeviceWithWidgetsAndValueUpdated() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };
        product.webDashboard = new WebDashboard(new Widget[] {
                createWebLabelWidget(1, "123")
        });

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        assertNotNull(createdDevice.token);
        String token = createdDevice.token;
        assertEquals("My New Device", createdDevice.name);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);
        assertNotNull(createdDevice.webDashboard);
        assertEquals(1, createdDevice.webDashboard.widgets.length);
        assertTrue(createdDevice.webDashboard.widgets[0] instanceof WebLabel);
        WebLabel webLabel = (WebLabel) createdDevice.webDashboard.widgets[0];
        assertEquals("123", webLabel.label);
        assertEquals(1, webLabel.sources[0].dataStream.pin);
        assertEquals(PinType.VIRTUAL, webLabel.sources[0].dataStream.pinType);
        assertNull(webLabel.sources[0].dataStream.value);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + token);
        newHardClient.verifyResult(ok(1));
        newHardClient.send("hardware vw 1 121");
        client.verifyResult(TestUtil.deviceConnected(1, createdDevice.id));

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(4);

        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.webDashboard);
        assertEquals(1, createdDevice.webDashboard.widgets.length);
        assertTrue(createdDevice.webDashboard.widgets[0] instanceof WebLabel);
        webLabel = (WebLabel) createdDevice.webDashboard.widgets[0];
        assertEquals("123", webLabel.label);
        assertEquals(1, webLabel.sources[0].dataStream.pin);
        assertEquals(PinType.VIRTUAL, webLabel.sources[0].dataStream.pinType);
        assertEquals("121", webLabel.sources[0].dataStream.value);
    }

    @Test
    public void testSetAuthTokenWorks() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };
        product.webDashboard = new WebDashboard(new Widget[] {
                createWebLabelWidget(1, "123")
        });

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        assertNotNull(createdDevice.token);
        String token = createdDevice.token;
        assertEquals("My New Device", createdDevice.name);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);
        assertNotNull(createdDevice.webDashboard);
        assertEquals(1, createdDevice.webDashboard.widgets.length);
        assertTrue(createdDevice.webDashboard.widgets[0] instanceof WebLabel);
        WebLabel webLabel = (WebLabel) createdDevice.webDashboard.widgets[0];
        assertEquals("123", webLabel.label);
        assertEquals(1, webLabel.sources[0].dataStream.pin);
        assertEquals(PinType.VIRTUAL, webLabel.sources[0].dataStream.pinType);
        assertNull(webLabel.sources[0].dataStream.value);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + token);
        newHardClient.verifyResult(ok(1));
        client.verifyResult(TestUtil.deviceConnected(1, createdDevice.id));

        client.trackDevice(createdDevice.id);
        client.verifyResult(ok(3));
        String newToken = TokenGeneratorUtil.generateNewToken();
        client.setAuthToken(createdDevice.id, newToken);
        client.verifyResult(ok(4));

        //after token was changed, expecting existing hard connection to be closed
        assertTrue(newHardClient.isClosed());

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + token);
        newHardClient.verifyResult(invalidToken(1));

        newHardClient.send("login " + newToken);
        newHardClient.verifyResult(ok(2));
        client.verifyResult(TestUtil.deviceConnected(2, createdDevice.id));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        Device updatedDevice = client.parseDevice(1);
        assertNotNull(updatedDevice);
        assertEquals(createdDevice.id, updatedDevice.id);
        assertEquals(newToken, updatedDevice.token);
    }

    @Test
    public void testSetInvalidAuthToken() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.setAuthToken(1, "123");
        client.verifyResult(webJson(1, "Set auth token is not valid. Token is empty or length is not 32 chars."));
    }

    @Test
    public void testDashboardIsInheritedByAllDevicesNotUpdated() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", null, true)
        };
        product.webDashboard = new WebDashboard(new Widget[] {
                createWebLabelWidget(1, "123"),
                createWebLineGraph(2, "graph")
        });

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        WebLabel webLabel = createWebLabelWidget(1, "1234");
        webLabel.x = 111;
        webLabel.y = 111;
        webLabel.width = 111;
        webLabel.height = 111;
        WebLineGraph webLineGraph = createWebLineGraph(2, "graph4");

        fromApiProduct = updateProductWebDash(fromApiProduct, webLabel, webLineGraph);

        client.updateProduct(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(3);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertNotNull(fromApiProduct.webDashboard);
        assertEquals(2, fromApiProduct.webDashboard.widgets.length);
        webLabel = (WebLabel) fromApiProduct.webDashboard.widgets[0];
        assertEquals("1234", webLabel.label);
        assertEquals(111, webLabel.x);
        assertEquals(111, webLabel.y);
        assertEquals(111, webLabel.height);
        assertEquals(111, webLabel.width);

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(4);

        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertNotNull(createdDevice.webDashboard);
        assertEquals(2, createdDevice.webDashboard.widgets.length);
        assertTrue(createdDevice.webDashboard.widgets[0] instanceof WebLabel);
        webLabel = (WebLabel) createdDevice.webDashboard.widgets[0];
        assertEquals("123", webLabel.label);
        assertEquals(1, webLabel.x);
        assertEquals(2, webLabel.y);
        assertEquals(10, webLabel.height);
        assertEquals(20, webLabel.width);

        WebSource webSource = webLabel.sources[0];
        assertEquals("Web Source Label", webSource.label);
        assertEquals(1, webSource.dataStream.pin);
        assertEquals(PinType.VIRTUAL, webSource.dataStream.pinType);
    }

    @Test
    public void trackNonExistingDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.trackDevice(11111);
        client.verifyResult(webJson(1, "Requested device not exists."));
    }

    public static class TestDevice extends Device {

        String orgName;

        String productName;

        String productLogoUrl;

    }
}
