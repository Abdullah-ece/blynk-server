package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.server.core.model.web.Organization.SUPER_ORG_PARENT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
@Deprecated
@Ignore
public class DevicesAPITest extends APIBaseTest {

    @Test
    public void createDevice() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(2, device.metaFields.length);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
            assertEquals(regularUser.email, device.activatedBy);
        }

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(2, device.id);
            assertNotNull(device.metaFields);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
            assertEquals(regularUser.email, device.activatedBy);
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(3, devices.length);

            System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
        }
    }

    @Test
    public void createDeviceForAnotherOrganization() throws Exception {
        login(regularAdmin.email, regularAdmin.pass);

        int productId = createProduct();

        Organization organization = new Organization("My Org", "Some TimeZone", "/static/logo.png", false, SUPER_ORG_PARENT_ID);
        organization.selectedProducts = new int[]{productId};

        HttpPut createOrgReq = new HttpPut(httpsAdminServerUrl + "/organization");
        createOrgReq.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        int orgId = 2;
        int newProductId = 2;

        try (CloseableHttpResponse response = httpclient.execute(createOrgReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Organization fromApi = JsonParser.parseOrganization(consumeText(response), 1);
            assertNotNull(fromApi);
            assertEquals(orgId, fromApi.id);
            assertEquals(1, fromApi.parentId);
            assertEquals(organization.name, fromApi.name);
            assertEquals(organization.tzName, fromApi.tzName);
            assertNotNull(fromApi.products);
            assertEquals(1, fromApi.products.length);
            assertEquals(newProductId, fromApi.products[0].id);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = newProductId;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/" + orgId);
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(2, device.metaFields.length);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
            assertEquals(regularAdmin.email, device.activatedBy);
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/" + orgId);
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(1, devices.length);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/" + orgId + "/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.readAny(responseString, Device.class);
            assertNotNull(device);
            assertEquals(1, device.id);
        }
    }

    @Test
    public void createDeviceForAnotherOrganizationAndIsVisibleForParentOrg() throws Exception {
        login(regularAdmin.email, regularAdmin.pass);

        int productId = createProduct();

        Organization organization = new Organization("My Org", "Some TimeZone", "/static/logo.png", false, SUPER_ORG_PARENT_ID);
        organization.selectedProducts = new int[]{productId};

        HttpPut createOrgReq = new HttpPut(httpsAdminServerUrl + "/organization");
        createOrgReq.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        int orgId = 2;
        int newProductId = 2;

        try (CloseableHttpResponse response = httpclient.execute(createOrgReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Organization fromApi = JsonParser.parseOrganization(consumeText(response), 1);
            assertNotNull(fromApi);
            assertEquals(orgId, fromApi.id);
            assertEquals(1, fromApi.parentId);
            assertNotNull(fromApi.products);
            assertEquals(1, fromApi.products.length);
            assertEquals(newProductId, fromApi.products[0].id);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = newProductId;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/" + orgId);
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/" + 1);
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(2, devices.length);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/" + 1 + "/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.readAny(responseString, Device.class);
            assertNotNull(device);
            assertEquals(1, device.id);
        }
    }

    @Test
    public void createDeviceAndUpdateMetafield() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        NumberMetaField numberMetaField;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(2, device.metaFields.length);

            numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals(1, numberMetaField.id);
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
            assertEquals(regularUser.email, device.activatedBy);
            assertEquals(0, device.metadataUpdatedAt);
            assertNull(device.metadataUpdatedBy);
        }

        MetaField updatedMetaField = new NumberMetaField(1, "Jopa2", Role.STAFF, false, null, 0, 1000, 123D);

        HttpPost update = new HttpPost(httpsAdminServerUrl + "/devices/1/1/updateMetaField");
        update.setEntity(new StringEntity(JsonParser.toJson(updatedMetaField), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(update)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.readAny(responseString, Device.class);
            assertNotNull(device);
            assertEquals(System.currentTimeMillis(), device.metadataUpdatedAt, 5000);
            assertEquals(regularUser.email, device.metadataUpdatedBy);
        }

        MetaField updatedMetaField2 = new NumberMetaField(3, "Jopa2", Role.STAFF, false, null, 0, 1000, 123D);

        HttpPost update2 = new HttpPost(httpsAdminServerUrl + "/devices/1/1/updateMetaField");
        update2.setEntity(new StringEntity(JsonParser.toJson(updatedMetaField2), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(update2)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getAllDevices() throws Exception {
        login(admin.email, admin.pass);

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(1, devices.length);

            System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
        }
    }

    @Test
    public void getDevicesWithSorting() throws Exception {
        login(admin.email, admin.pass);

        int productId = createProduct();

        Device newDevice = new Device();
        newDevice.name = "B";
        newDevice.productId = productId;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newDevice = new Device();
        newDevice.name = "C";
        newDevice.productId = productId;

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newDevice = new Device();
        newDevice.name = "A";
        newDevice.productId = productId;

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1?orderField=name&order=ASC");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(4, devices.length);
            assertNull(devices[0].name);
            assertEquals("A", devices[1].name);
            assertEquals("B", devices[2].name);
            assertEquals("C", devices[3].name);
        }

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1?orderField=name&order=DESC");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(4, devices.length);
            assertEquals("C", devices[0].name);
            assertEquals("B", devices[1].name);
            assertEquals("A", devices[2].name);
            assertNull(devices[3].name);
        }
    }

    @Test
    public void getDevicesWithSortingByMultiFields1() throws Exception {
        login(admin.email, admin.pass);

        int productId = createProduct();

        Device newDevice = new Device();
        newDevice.name = "B";
        newDevice.productId = productId;
        newDevice.dataReceivedAt = 1;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newDevice = new Device();
        newDevice.name = "C";
        newDevice.productId = productId;
        newDevice.dataReceivedAt = 2;

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newDevice = new Device();
        newDevice.name = "A";
        newDevice.productId = productId;
        newDevice.dataReceivedAt = 3;

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1?orderField=dataReceivedAt&orderField=name&order=ASC");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(4, devices.length);
            assertNull(devices[0].name);
            assertEquals("B", devices[1].name);
            assertEquals("C", devices[2].name);
            assertEquals("A", devices[3].name);
        }

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1?orderField=dataReceivedAt&orderField=name&order=DESC");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(4, devices.length);
            assertEquals("A", devices[0].name);
            assertEquals("C", devices[1].name);
            assertEquals("B", devices[2].name);
            assertNull(devices[3].name);
        }
    }

    @Test
    public void getDevicesWithSortingByMultiFields2() throws Exception {
        login(admin.email, admin.pass);

        int productId = createProduct();

        Device newDevice = new Device();
        newDevice.name = "B";
        newDevice.productId = productId;
        newDevice.dataReceivedAt = 2;
        newDevice.disconnectTime = 0;


        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newDevice = new Device();
        newDevice.name = "C";
        newDevice.productId = productId;
        newDevice.dataReceivedAt = 1;
        newDevice.disconnectTime = 2;

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newDevice = new Device();
        newDevice.name = "A";
        newDevice.productId = productId;
        newDevice.dataReceivedAt = 1;
        newDevice.disconnectTime = 1;

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1?orderField=dataReceivedAt&orderField=disconnectTime&orderField=name&order=ASC");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(4, devices.length);
            assertNull(devices[0].name);
            assertEquals("A", devices[1].name);
            assertEquals("C", devices[2].name);
            assertEquals("B", devices[3].name);
        }

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1?orderField=dataReceivedAt&orderField=disconnectTime&orderField=name&order=DESC");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(4, devices.length);
            assertEquals("B", devices[0].name);
            assertEquals("C", devices[1].name);
            assertEquals("A", devices[2].name);
            assertNull(devices[3].name);
        }
    }

    @Test
    public void getDeviceById() throws Exception {
        login(admin.email, admin.pass);

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/0");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.readAny(responseString, Device.class);
            assertNotNull(device);;
        }
    }

    @Test
    public void getDeviceByIdNotFound() throws Exception {
        login(regularUser.email, regularUser.pass);

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/11111");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }


    @Test
    public void checkDeviceOrgName() throws Exception {
        login(admin.email, admin.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            TestDevice device = JsonParser.MAPPER.readValue(responseString, TestDevice.class);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertEquals(1, device.productId);
            assertNotNull(device.metaFields);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals("Blynk Inc.", device.orgName);
            assertEquals("My product", device.productName);
            assertEquals("/logoUrl", device.productLogoUrl);
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TestDevice[] devices = JsonParser.readAny(responseString, TestDevice[].class);
            assertNotNull(devices);
            for (TestDevice testDevice : devices) {
                if (testDevice.id == 1) {
                    assertEquals("My product", testDevice.productName);
                }
            }
        }

    }

    private int createProduct() throws Exception {
        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.logoUrl = "/logoUrl";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, null, 0, 1000, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, null, "My Default device Name")
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi.id;
        }
    }

    public static class TestDevice extends Device {

        public String orgName;

        public String productName;

        public String productLogoUrl;

    }


}
