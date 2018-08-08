package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
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
                new NumberMetaField(1, "Jopa", Role.STAFF, false, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, "My Default device Name")
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
        assertEquals(Role.STAFF, numberMetaField.role);
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
        assertEquals(Role.STAFF, numberMetaField.role);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(getUserName(), createdDevice.activatedBy);

        client.getDevices(orgId);
        Device[] devices = client.parseDevices(4);
        assertNotNull(devices);
        assertEquals(2, devices.length);
    }

    @Test
    public void createAndDeleteDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, "My Default device Name")
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

        Organization organization = new Organization("My Org", "Some TimeZone", "/static/logo.png", false, orgId);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        Organization fromApi = client.parseOrganization(2);
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
        Device[] devices = client.parseDevices(4);
        assertNotNull(devices);
        assertEquals(1, devices.length);

        client.getDevice(fromApi.id, devices[0].id);
        Device device = client.parseDevice(5);
        assertNotNull(device);
    }

    @Test
    public void createDeviceForAnotherOrganizationAndIsVisibleForParentOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("My Org ffff", "Some TimeZone", "/static/logo.png", false, orgId);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        Organization fromApi = client.parseOrganization(2);
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
        Device[] devices = client.parseDevices(4);
        assertNotNull(devices);
        assertEquals(1, devices.length);

        client.getDevice(orgId, devices[0].id);
        Device device = client.parseDevice(5);
        assertNotNull(device);
    }

    @Test
    public void createDeviceAndUpdateMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, "My Default device Name")
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
        assertEquals(Role.STAFF, numberMetaField.role);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
        assertEquals(getUserName(), device.activatedBy);
        assertEquals(0, device.metadataUpdatedAt);
        assertNull(device.metadataUpdatedBy);

        MetaField updatedMetaField = new NumberMetaField(1, "Jopa2", Role.STAFF, false, 123D);
        client.updateDeviceMetafield(device.id, updatedMetaField);
        client.verifyResult(ok(3));

        client.getDevice(orgId, device.id);
        device = client.parseDevice(4);
        assertNotNull(device);
        assertEquals(System.currentTimeMillis(), device.metadataUpdatedAt, 5000);
        assertEquals(getUserName(), device.metadataUpdatedBy);

        MetaField updatedMetaField2 = new NumberMetaField(3, "Jopa2", Role.STAFF, false, 123D);
        client.updateDeviceMetafield(device.id, updatedMetaField2);
        client.verifyResult(webJson(5, "Couldn't find metafield with passed id."));
    }

    @Test
    public void getAllDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getDevices(orgId);
        Device[] devices = client.parseDevices(1);
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
    @Ignore
    //todo finish
    public void getDevicesWithSortingByMultiFields2() throws Exception {

    }

    @Test
    @Ignore
    //todo finish
    public void getDevicesWithSorting() throws Exception {

    }
    public static class TestDevice extends Device {

        String orgName;

        String productName;

        String productLogoUrl;

    }
}
