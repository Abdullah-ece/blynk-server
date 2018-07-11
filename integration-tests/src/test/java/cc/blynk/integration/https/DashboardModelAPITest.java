package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import cc.blynk.server.core.model.widgets.web.WebLineGraph;
import cc.blynk.server.core.model.widgets.web.WebSlider;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.WebSwitch;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static cc.blynk.integration.https.reporting.ReportingTestUtils.columnFrom;
import static cc.blynk.integration.https.reporting.ReportingTestUtils.metaDataFrom;
import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DashboardModelAPITest extends APIBaseTest {

    private BaseServer hardwareServer;
    private ClientPair clientPair;

    @Before
    public void init() throws Exception {
        super.init();
        this.hardwareServer = new HardwareAndHttpAPIServer(holder).start();

        this.clientPair = initAppAndHardPair();
    }

    @After
    public void shutdownHardwareServer() {
        this.hardwareServer.close();
        this.clientPair.stop();
    }

    @Test
    public void createDeviceWithWidgets() throws Exception {
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
            assertNotNull(device.webDashboard);
            assertEquals(2, device.webDashboard.widgets.length);
            assertEquals("123", device.webDashboard.widgets[0].label);
        }
    }

    @Test
    public void createDeviceWithWidgetsAndValueUpdated() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        String token;

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertNotNull(device.token);
            token = device.token;
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
            assertNotNull(device.webDashboard);
            assertEquals(2, device.webDashboard.widgets.length);
            assertTrue(device.webDashboard.widgets[0] instanceof WebLabel);
            WebLabel webLabel = (WebLabel) device.webDashboard.widgets[0];
            assertEquals("123", webLabel.label);
            assertEquals(1, webLabel.sources[0].dataStream.pin);
            assertEquals(PinType.VIRTUAL, webLabel.sources[0].dataStream.pinType);
            assertEquals(null, webLabel.sources[0].dataStream.value);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("hardware vw 1 121");
        //todo ok for now. but could be better.
        sleep(500);

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertNotNull(device.webDashboard);
            assertEquals(2, device.webDashboard.widgets.length);
            assertTrue(device.webDashboard.widgets[0] instanceof WebLabel);
            WebLabel webLabel = (WebLabel) device.webDashboard.widgets[0];
            assertEquals("123", webLabel.label);
            assertEquals(1, webLabel.sources[0].dataStream.pin);
            assertEquals(PinType.VIRTUAL, webLabel.sources[0].dataStream.pinType);
            assertEquals("121", webLabel.sources[0].dataStream.value);

        }
    }

    @Test
    public void testDashboardIsInheritedByAllDevicesNotUpdated() throws Exception {
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
            assertNotNull(device.webDashboard);
            assertEquals(2, device.webDashboard.widgets.length);
            assertEquals("123", device.webDashboard.widgets[0].label);
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/product");

        Product product;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
            product = fromApi[0];
        }

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
            assertEquals(2, fromApi.webDashboard.widgets.length);
            assertEquals("123", fromApi.webDashboard.widgets[0].label);
            assertEquals(1, fromApi.webDashboard.widgets[0].x);
            assertEquals(2, fromApi.webDashboard.widgets[0].y);
            assertEquals(10, fromApi.webDashboard.widgets[0].height);
            assertEquals(20, fromApi.webDashboard.widgets[0].width);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertNotNull(device.webDashboard);
            assertEquals(2, device.webDashboard.widgets.length);
            assertEquals("123", device.webDashboard.widgets[0].label);
            assertEquals(1, device.webDashboard.widgets[0].x);
            assertEquals(2, device.webDashboard.widgets[0].y);
            assertEquals(10, device.webDashboard.widgets[0].height);
            assertEquals(20, device.webDashboard.widgets[0].width);
        }
    }

    @Test
    public void testDashboardUpdateIsInheritedByAllDevices() throws Exception {
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
            assertNotNull(device.webDashboard);
            assertEquals(2, device.webDashboard.widgets.length);
            assertEquals("123", device.webDashboard.widgets[0].label);
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/product");

        Product product;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product[] fromApi = JsonParser.readAny(consumeText(response), Product[].class);
            assertNotNull(fromApi);
            assertEquals(1, fromApi.length);
            product = fromApi[0];
        }

        WebSwitch webSwitch = new WebSwitch();
        webSwitch.id = 100;
        webSwitch.onLabel = "updated";
        webSwitch.x = 3;
        webSwitch.y = 4;
        webSwitch.height = 50;
        webSwitch.width = 60;

        WebLabel webLabel = new WebLabel();
        webLabel.id = 101;
        webLabel.label = "updated";
        webLabel.x = 3;
        webLabel.y = 4;
        webLabel.height = 50;
        webLabel.width = 60;

        WebSlider webSlider = new WebSlider();
        webSlider.id = 102;
        webSlider.label = "updated";
        webSlider.x = 3;
        webSlider.y = 4;
        webSlider.height = 50;
        webSlider.width = 60;
        webSlider.minValue = 0;
        webSlider.maxValue = 100;

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel, webSwitch, webSlider
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
            assertEquals(3, fromApi.webDashboard.widgets.length);

            webLabel = (WebLabel) fromApi.webDashboard.widgets[0];
            assertEquals("updated", webLabel.label);
            assertEquals(3, webLabel.x);
            assertEquals(4, webLabel.y);
            assertEquals(50, webLabel.height);
            assertEquals(60, webLabel.width);

            webSwitch = (WebSwitch) fromApi.webDashboard.widgets[1];
            assertEquals("updated", webSwitch.onLabel);
            assertEquals(3, webSwitch.x);
            assertEquals(4, webSwitch.y);
            assertEquals(50, webSwitch.height);
            assertEquals(60, webSwitch.width);

            webSlider = (WebSlider) fromApi.webDashboard.widgets[2];
            assertEquals("updated", webSlider.label);
            assertEquals(3, webSlider.x);
            assertEquals(4, webSlider.y);
            assertEquals(50, webSlider.height);
            assertEquals(60, webSlider.width);
            assertEquals(0, webSlider.minValue, 0.001);
            assertEquals(100, webSlider.maxValue, 0.001);
        }

        webSlider.minValue = 1;
        webSlider.maxValue = 101;

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel, webSwitch, webSlider
        });

        updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(product.name, fromApi.name);
            assertEquals(product.description, fromApi.description);
            assertNotNull(fromApi.webDashboard);
            assertEquals(3, fromApi.webDashboard.widgets.length);

            webLabel = (WebLabel) fromApi.webDashboard.widgets[0];
            assertEquals("updated", webLabel.label);
            assertEquals(3, webLabel.x);
            assertEquals(4, webLabel.y);
            assertEquals(50, webLabel.height);
            assertEquals(60, webLabel.width);

            webSwitch = (WebSwitch) fromApi.webDashboard.widgets[1];
            assertEquals("updated", webSwitch.onLabel);
            assertEquals(3, webSwitch.x);
            assertEquals(4, webSwitch.y);
            assertEquals(50, webSwitch.height);
            assertEquals(60, webSwitch.width);

            webSlider = (WebSlider) fromApi.webDashboard.widgets[2];
            assertEquals("updated", webSlider.label);
            assertEquals(3, webSlider.x);
            assertEquals(4, webSlider.y);
            assertEquals(50, webSlider.height);
            assertEquals(60, webSlider.width);
            assertEquals(1, webSlider.minValue, 0.001);
            assertEquals(101, webSlider.maxValue, 0.001);
        }

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertNotNull(device.webDashboard);
            assertEquals(3, device.webDashboard.widgets.length);

            webLabel = (WebLabel) device.webDashboard.widgets[0];
            assertEquals("updated", webLabel.label);
            assertEquals(3, webLabel.x);
            assertEquals(4, webLabel.y);
            assertEquals(50, webLabel.height);
            assertEquals(60, webLabel.width);

            webSwitch = (WebSwitch) device.webDashboard.widgets[1];
            assertEquals("updated", webSwitch.onLabel);
            assertEquals(3, webSwitch.x);
            assertEquals(4, webSwitch.y);
            assertEquals(50, webSwitch.height);
            assertEquals(60, webSwitch.width);

            webSlider = (WebSlider) device.webDashboard.widgets[2];
            assertEquals("updated", webSlider.label);
            assertEquals(3, webSlider.x);
            assertEquals(4, webSlider.y);
            assertEquals(50, webSlider.height);
            assertEquals(60, webSlider.width);
            assertEquals(1, webSlider.minValue, 0.001);
            assertEquals(101, webSlider.maxValue, 0.001);
        }
    }

    private int createProduct() throws Exception {
        Product product = new Product();
        product.name = "My Product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.logoUrl = "/logoUrl";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, "My Default device Name")
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.x = 1;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        WebLineGraph webGraph = new WebLineGraph();
        webGraph.label = "graph";
        webGraph.x = 3;
        webGraph.y = 4;
        webGraph.height = 10;
        webGraph.width = 20;
        webGraph.sources = new WebSource[] {
                new WebSource("Some Label", "#334455", false,
                        RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL),
                        new SelectedColumn[] {columnFrom("Load Weight")},
                        new SelectedColumn[] {metaDataFrom("Shift 1"), metaDataFrom("Shift 2"), metaDataFrom("Shift 3")},
                        new SelectedColumn[] {columnFrom("Load Weight")},
                        SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel,
                webGraph
        });

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fromApi));
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
