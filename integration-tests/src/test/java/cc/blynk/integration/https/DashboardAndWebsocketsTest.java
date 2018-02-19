package cc.blynk.integration.https;

import cc.blynk.integration.IntegrationBase;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
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
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.WebSwitch;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.server.core.model.widgets.web.SourceType.RAW_DATA;
import static cc.blynk.utils.StringUtils.WEBSOCKET_WEB_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DashboardAndWebsocketsTest extends APIBaseTest {

    private BaseServer hardwareServer;
    private ClientPair clientPair;

    @Before
    public void init() throws Exception {
        super.init();
        this.hardwareServer = new HardwareAndHttpAPIServer(holder).start();

        this.clientPair = IntegrationBase.initAppAndHardPair();
    }

    @After
    public void shutdown() {
        this.hardwareServer.close();
        this.clientPair.stop();
        super.shutdown();
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
            assertEquals(1, device.webDashboard.widgets.length);
            assertTrue(device.webDashboard.widgets[0] instanceof WebSwitch);
            WebSwitch webSwitch = (WebSwitch) device.webDashboard.widgets[0];
            assertEquals(1, webSwitch.sources[0].dataStream.pin);
            assertEquals(PinType.VIRTUAL, webSwitch.sources[0].dataStream.pinType);
            assertEquals("123", webSwitch.label);
        }

        AppWebSocketClient appWebSocketClient = new AppWebSocketClient("localhost", httpsPort, WEBSOCKET_WEB_PATH);
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.send("hardware 1 vw 1 222");

        HttpGet getDevice = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevice)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertNotNull(device.webDashboard);
            assertEquals(1, device.webDashboard.widgets.length);
            WebSwitch webSwitch = (WebSwitch) device.webDashboard.widgets[0];
            assertEquals("222", webSwitch.sources[0].dataStream.value);
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

        WebSwitch webSwitch = new WebSwitch();
        webSwitch.label = "123";
        webSwitch.x = 1;
        webSwitch.y = 2;
        webSwitch.height = 10;
        webSwitch.width = 20;
        webSwitch.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webSwitch
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
