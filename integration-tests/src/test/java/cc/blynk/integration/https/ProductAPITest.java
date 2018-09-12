package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.AddressMetaField;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.CoordinatesMetaField;
import cc.blynk.server.core.model.web.product.metafields.CostMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.core.model.web.product.metafields.SwitchMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.web.product.metafields.TimeMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Date;

import static cc.blynk.integration.TestUtil.consumeText;
import static java.time.LocalTime.ofSecondOfDay;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
@Deprecated
public class ProductAPITest extends APIBaseTest {

    @Test
    public void getProductsNotAuthorized() throws Exception {
        HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/product");
        try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getNonExistingProduct() throws Exception {
        login(admin.email, admin.pass);

        HttpGet product = new HttpGet(httpsAdminServerUrl + "/product/123");
        try (CloseableHttpResponse response = httpclient.execute(product)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getProductById() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }

        HttpGet product1 = new HttpGet(httpsAdminServerUrl + "/product/1");
        try (CloseableHttpResponse response = httpclient.execute(product1)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }
    }

    @Test
    public void createProductWithNoName() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void updateProductWithNoName() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        Product fromApi;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
            assertEquals(0, fromApi.version);
        }

        product.id = 1;
        product.name = "";

        HttpPost updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createProduct() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My Farm", Role.ADMIN, false, null, "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", Role.ADMIN, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, null, 0, 100, 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, null, MeasurementUnit.Celsius, 36, 0, 100),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, null, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, null, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", Role.ADMIN, false, null, new Date().getTime()),
                new MeasurementUnitMetaField(10, "None Unit", Role.ADMIN, false, null, MeasurementUnit.None, 36, 0, 100),
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
            assertEquals(product.logoUrl, fromApi.logoUrl);
            assertEquals(0, fromApi.version);
            assertNotEquals(0, fromApi.lastModifiedTs);
            assertNotNull(fromApi.dataStreams);
            assertNotNull(fromApi.metaFields);
            assertEquals(11, fromApi.metaFields.length);
        }
    }

    @Test
    public void createProductWithWidgets() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My Farm", Role.ADMIN, false, null, "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", Role.ADMIN, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, null, 0, 1000, 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, null, MeasurementUnit.Celsius, 36, 0, 100),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, null, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, null, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", Role.ADMIN, false, null, new Date().getTime())
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.x = 1;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
            assertEquals(product.logoUrl, fromApi.logoUrl);
            assertEquals(0, fromApi.version);
            assertNotEquals(0, fromApi.lastModifiedTs);
            assertNotNull(fromApi.dataStreams);
            assertNotNull(fromApi.metaFields);
            assertEquals(10, fromApi.metaFields.length);
            assertNotNull(fromApi.webDashboard);
            assertEquals(1, fromApi.webDashboard.widgets.length);
            assertEquals("123", fromApi.webDashboard.widgets[0].label);
            assertEquals(1, fromApi.webDashboard.widgets[0].x);
            assertEquals(2, fromApi.webDashboard.widgets[0].y);
            assertEquals(10, fromApi.webDashboard.widgets[0].height);
            assertEquals(20, fromApi.webDashboard.widgets[0].width);
        }

        product.id = 1;
        product.description = "Description2";

        webLabel = new WebLabel();
        webLabel.label = "updated";
        webLabel.x = 1;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        HttpPost updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertNotNull(fromApi.webDashboard);
            assertEquals(1, fromApi.webDashboard.widgets.length);
            assertEquals("updated", fromApi.webDashboard.widgets[0].label);
            assertEquals(1, fromApi.webDashboard.widgets[0].x);
            assertEquals(2, fromApi.webDashboard.widgets[0].y);
            assertEquals(10, fromApi.webDashboard.widgets[0].height);
            assertEquals(20, fromApi.webDashboard.widgets[0].width);

            System.out.println(product);
        }


    }

    @Test
    public void create2ProductsWithSameName() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
        }

        Product product2 = new Product();
        product2.name = "My product";
        product2.description = "Description";
        product2.boardType = "ESP8266";
        product2.connectionType = ConnectionType.WI_FI;
        product2.logoUrl = "/static/logo.png";

        req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product2).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createAndUpdateProduct() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        HttpPut createReq = new HttpPut(httpsAdminServerUrl + "/product");
        createReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(createReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
        }

        product.id = 1;
        product.description = "Description2";

        HttpPost updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
        }
    }

    @Test
    public void updateProduct() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        Product fromApi;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
            assertEquals(0, fromApi.version);
        }

        product.id = 1;
        product.name = "Updated Name";

        HttpPost updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals("Updated Name", fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertEquals(product.boardType, fromApi.boardType);
            assertEquals(product.connectionType, fromApi.connectionType);
            assertEquals(1, fromApi.version);
        }
    }

    @Test
    public void getEmptyListOfProducts() throws Exception {
        login(admin.email, admin.pass);

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(0, fromApi.length);
        }
    }

    @Test
    public void getListOfProducts() throws Exception {
        createProduct();

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
        }
    }

    @Test
    public void getListOfProducts2() throws Exception {
        createProduct();

        Product product2 = new Product();
        product2.name = "My product2";
        product2.description = "Description2";
        product2.boardType = "ESP82662";
        product2.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product2).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(2, fromApi.id);
        }

        HttpGet getList = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(getList)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(2, fromApi.length);
        }
    }

    @Test
    public void createProductAndDelete() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }

        HttpGet req2 = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req2)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
        }

        HttpDelete req3 = new HttpDelete(httpsAdminServerUrl + "/product/1");

        try (CloseableHttpResponse response = httpclient.execute(req3)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req4 = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req4)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(0, fromApi.length);
        }
    }

    @Test
    public void cantDeleteProductWithDevices() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = 1;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpDelete req3 = new HttpDelete(httpsAdminServerUrl + "/product/1");

        try (CloseableHttpResponse response = httpclient.execute(req3)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"You are not allowed to remove product with devices.\"}}", consumeText(response));
        }
    }

    @Test
    public void testAddMetaDataFieldInChildDevices() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My test metafield", Role.ADMIN, false, null, "Default Device")
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            product = JsonParser.parseProduct(consumeText(response));
            assertNotNull(product);
            assertEquals(1, product.id);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = 1;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        TextMetaField textMetaField;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            newDevice = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", newDevice.name);
            assertEquals(1, newDevice.id);
            assertNotNull(newDevice.metaFields);
            assertEquals(1, newDevice.metaFields.length);
            textMetaField = (TextMetaField) newDevice.metaFields[0];
            assertEquals(1, textMetaField.id);
            assertEquals("My test metafield", textMetaField.name);
            assertEquals(Role.ADMIN, textMetaField.role);
            assertEquals("Default Device", textMetaField.value);
        }

        newDevice.metaFields[0] = new TextMetaField(textMetaField.id, textMetaField.name, textMetaField.role, false, null, "My updated value");

        HttpPost updateDeviceReq = new HttpPost(httpsAdminServerUrl + "/devices/1");
        updateDeviceReq.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateDeviceReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(1, device.metaFields.length);
            textMetaField = (TextMetaField) device.metaFields[0];
            assertEquals(1, textMetaField.id);
            assertEquals("My test metafield", textMetaField.name);
            assertEquals(Role.ADMIN, textMetaField.role);
            assertEquals("My updated value", textMetaField.value);
        }

        product.metaFields = new MetaField[] {
                product.metaFields[0],
                new NumberMetaField(2, "New metafield", Role.ADMIN, false, null, 0, 1000, 123)
        };

        HttpPost updateProductAndDevicesReq = new HttpPost(httpsAdminServerUrl + "/product/updateDevices");
        updateProductAndDevicesReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateProductAndDevicesReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(2, fromApi.metaFields.length);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(2, device.metaFields.length);
            textMetaField = (TextMetaField) device.metaFields[0];
            assertEquals(1, textMetaField.id);
            assertEquals("My test metafield", textMetaField.name);
            assertEquals(Role.ADMIN, textMetaField.role);
            assertEquals("My updated value", textMetaField.value);

            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[1];
            assertEquals(2, numberMetaField.id);
            assertEquals("New metafield", numberMetaField.name);
            assertEquals(Role.ADMIN, numberMetaField.role);
            assertEquals(123, numberMetaField.value, 0.1);

        }
    }

    @Test
    public void testUpdateMetaDataFieldInChildDevices() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My test metafield", Role.ADMIN, false, null, "Default Device")
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            product = JsonParser.parseProduct(consumeText(response));
            assertNotNull(product);
            assertEquals(1, product.id);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = 1;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        TextMetaField textMetaField;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            newDevice = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", newDevice.name);
            assertEquals(1, newDevice.id);
            assertNotNull(newDevice.metaFields);
            assertEquals(1, newDevice.metaFields.length);
            textMetaField = (TextMetaField) newDevice.metaFields[0];
            assertEquals(1, textMetaField.id);
            assertEquals("My test metafield", textMetaField.name);
            assertEquals(Role.ADMIN, textMetaField.role);
            assertEquals("Default Device", textMetaField.value);
        }

        product.metaFields[0] = new TextMetaField(1, "Me updated test metafield", Role.USER, false, null, "Default Device");

        HttpPost updateProductAndDevicesReq = new HttpPost(httpsAdminServerUrl + "/product/updateDevices");
        updateProductAndDevicesReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateProductAndDevicesReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(1, fromApi.metaFields.length);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(1, device.metaFields.length);
            textMetaField = (TextMetaField) device.metaFields[0];
            assertEquals(1, textMetaField.id);
            assertEquals("Me updated test metafield", textMetaField.name);
            assertEquals(Role.USER, textMetaField.role);
            assertEquals("Default Device", textMetaField.value);

        }
    }

    @Test
    public void testUpdateContactMetaDataFieldInChildDevices() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new ContactMetaField(1, "Farm of Smith", Role.ADMIN, false, null, "Tech Support",
                        "Dmitriy", true, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false, "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false)
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            product = JsonParser.parseProduct(consumeText(response));
            assertNotNull(product);
            assertEquals(1, product.id);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = 1;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        ContactMetaField contactMetaField;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            newDevice = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", newDevice.name);
            assertEquals(1, newDevice.id);
            assertNotNull(newDevice.metaFields);
            assertEquals(1, newDevice.metaFields.length);
            contactMetaField = (ContactMetaField) newDevice.metaFields[0];
            assertEquals(1, contactMetaField.id);
            assertEquals("Farm of Smith", contactMetaField.name);
            assertEquals(Role.ADMIN, contactMetaField.role);
            assertTrue(contactMetaField.isFirstNameEnabled);
            assertFalse(contactMetaField.isLastNameEnabled);
        }

        product.metaFields[0] = new ContactMetaField(1, "Farm of Smith", Role.ADMIN, false, null, "Tech Support",
                "Dmitriy", true, "Dumanskiy", true, "dmitriy@blynk.cc", false,
                "+38063673333",  false, "My street", false, "Ukraine", false,
                "Kyiv", false, "Ukraine", false, "03322", false, false);

        HttpPost updateProductAndDevicesReq = new HttpPost(httpsAdminServerUrl + "/product/updateDevices");
        updateProductAndDevicesReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateProductAndDevicesReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(1, fromApi.metaFields.length);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            assertEquals(1, device.metaFields.length);
            contactMetaField = (ContactMetaField) device.metaFields[0];
            assertEquals(1, contactMetaField.id);
            assertEquals("Farm of Smith", contactMetaField.name);
            assertEquals(Role.ADMIN, contactMetaField.role);
            assertTrue(contactMetaField.isFirstNameEnabled);
            assertTrue(contactMetaField.isLastNameEnabled);
        }
    }

    @Test
    public void createProductAndDeleteRegularUserCantDelete() throws Exception {
        login(regularUser.email, regularUser.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }

        HttpGet req2 = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req2)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
        }

        HttpDelete req3 = new HttpDelete(httpsAdminServerUrl + "/product/1");

        try (CloseableHttpResponse response = httpclient.execute(req3)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
        }

        HttpGet req4 = new HttpGet(httpsAdminServerUrl + "/product");

        try (CloseableHttpResponse response = httpclient.execute(req4)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
        }
    }

    @Test
    public void canDeleteProductRequest() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
        }

        HttpGet canDeleteProductReq = new HttpGet(httpsAdminServerUrl + "/product/canDeleteProduct/1");

        try (CloseableHttpResponse response = httpclient.execute(canDeleteProductReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = 1;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet canDeleteProductReq2 = new HttpGet(httpsAdminServerUrl + "/product/canDeleteProduct/1");

        try (CloseableHttpResponse response = httpclient.execute(canDeleteProductReq2)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
        }

    }

    @Test
    public void checkProductCannotBeCreatedForSubOrg() throws Exception {
        login(admin.email, admin.pass);

        Organization organization = new Organization("My Org", "Some TimeZone", "/static/logo.png", false, 1);

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/organization");
        req.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Organization fromApi = JsonParser.parseOrganization(consumeText(response), 1);
            assertNotNull(fromApi);
            assertEquals(2, fromApi.id);
            assertEquals(organization.name, fromApi.name);
            assertEquals(organization.tzName, fromApi.tzName);
        }

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        HttpPut productCreateReq = new HttpPut(httpsAdminServerUrl + "/product");
        productCreateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(2, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(productCreateReq)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"You can't create products for sub organizations.\"}}", consumeText(response));
        }
    }

}
