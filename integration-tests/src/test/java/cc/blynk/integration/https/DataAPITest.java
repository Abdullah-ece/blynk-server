package cc.blynk.integration.https;

import cc.blynk.integration.IntegrationBase;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.application.AppServer;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphType;
import cc.blynk.server.core.model.widgets.web.SourceType;
import cc.blynk.server.core.model.widgets.web.WebLabel;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.reporting.raw.BaseReportingKey;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.hardware.HardwareServer;
import cc.blynk.server.http.web.model.WebProductAndOrgId;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataAPITest extends APIBaseTest {

    private BaseServer appServer;
    private BaseServer hardwareServer;
    private ClientPair clientPair;

    @Before
    public void init() throws Exception {
        super.init();
        this.hardwareServer = new HardwareServer(holder).start();
        this.appServer = new AppServer(holder).start();

        this.clientPair = IntegrationBase.initAppAndHardPair();
        //clean everything just in case
        holder.dbManager.executeSQL("DELETE FROM reporting_raw_data");
    }

    @After
    public void shutdown() {
        super.shutdown();
        this.appServer.close();
        this.hardwareServer.close();
        this.clientPair.stop();
    }

    @Test
    public void testInvalidRequestNoDataStream() throws Exception {
        login(regularUser.email, regularUser.pass);

        HttpGet getData = new HttpGet(httpsAdminServerUrl
                + "/data/1/history?from=0&to="
                + System.currentTimeMillis()
                + "&offset=0&limit=1000");
        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"No data stream provided for request.\"}}", consumeText(response));
        }
    }

    @Test
    public void testInvalidRequestNoDevice() throws Exception {
        login(regularUser.email, regularUser.pass);

        HttpGet getData = new HttpGet(httpsAdminServerUrl
                + "/data/1/history?dataStream=V1&from=0&to="
                + System.currentTimeMillis()
                + "&offset=0&limit=1000");
        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Requested device not exists.\"}}", consumeText(response));
        }
    }

    @Test
    public void testSinglePinRequest() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        long now = System.currentTimeMillis();
        rawDataProcessor.collect(new BaseReportingKey("any@gmail.com", "Knight", 1, 1, PinType.VIRTUAL, (byte) 1), now, 123);
        rawDataProcessor.collect(new BaseReportingKey("any@gmail.com", "Knight", 1, 1, PinType.VIRTUAL, (byte) 1), now + 1, 124);

        //invoking directly dao to avoid separate thread execution
        holder.dbManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);

        HttpGet getData = new HttpGet(httpsAdminServerUrl + "/data/1/history?dataStream=V1&from=0&to=" + now+1 + "&offset=0&limit=1000");
        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            //expected string is
            //{"V1":{"data":[{"1504015510046":124.0},{"1504015510045":123.0}]}}

            String responseString = consumeText(response);
            Map map = JsonParser.MAPPER.readValue(responseString, Map.class);
            assertNotNull(map);
            assertEquals(1, map.size());
            LinkedHashMap dataField = (LinkedHashMap) map.get("V1");
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            List data = (List) dataField.get("data");
            assertEquals(2, data.size());
            LinkedHashMap point0 = (LinkedHashMap) data.get(0);
            LinkedHashMap point1 = (LinkedHashMap) data.get(1);
            assertEquals(123, (Double) point0.get(String.valueOf(now)), 0.0001);
            assertEquals(124, (Double) point1.get(String.valueOf(now + 1)), 0.0001);

            System.out.println(responseString);
        }
    }

    @Test
    public void testMultiPinRequest() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        long now = System.currentTimeMillis();
        rawDataProcessor.collect(new BaseReportingKey("any@gmail.com", "Knight", 1, 1, PinType.VIRTUAL, (byte) 1), now, 123);
        rawDataProcessor.collect(new BaseReportingKey("any@gmail.com", "Knight", 1, 1, PinType.VIRTUAL, (byte) 2), now, 124);

        //invoking directly dao to avoid separate thread execution
        holder.dbManager.reportingDBDao.insertRawData(rawDataProcessor.rawStorage);

        HttpGet getData = new HttpGet(httpsAdminServerUrl + "/data/1/history?dataStream=V1&dataStream=V2&from=0&to=" + now + "&offset=0&limit=1000");
        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            //expected string is
            //{
            // "V1":{"data":[{"1504102495921":123.0}]},
            // "V2":{"data":[{"1504102495921":124.0}]}
            // }
            System.out.println(responseString);

            Map map = JsonParser.MAPPER.readValue(responseString, Map.class);
            assertNotNull(map);
            assertEquals(2, map.size());
            LinkedHashMap dataField = (LinkedHashMap) map.get("V1");
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            List data = (List) dataField.get("data");
            assertEquals(1, data.size());
            LinkedHashMap point0 = (LinkedHashMap) data.get(0);
            assertEquals(123, (Double) point0.get(String.valueOf(now)), 0.0001);

            dataField = (LinkedHashMap) map.get("V2");
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            data = (List) dataField.get("data");
            assertEquals(1, data.size());
            point0 = (LinkedHashMap) data.get(0);
            assertEquals(124, (Double) point0.get(String.valueOf(now)), 0.0001);


        }
    }

    @Test
    public void testInsertAPIWorks() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet insertPoint = new HttpGet(httpsAdminServerUrl + "/data/1/insert?dataStream=V1&value=123");
        try (CloseableHttpResponse response = httpclient.execute(insertPoint)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        long now = System.currentTimeMillis();

        HttpGet getData = new HttpGet(httpsAdminServerUrl + "/data/1/history?dataStream=V1&from=0&to=" + now + "&offset=0&limit=1000");
        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            //expected string is
            //[{"V1":{"data":[{"1504015510046":123.0}]}}]

            String responseString = consumeText(response);
            Map map = JsonParser.MAPPER.readValue(responseString, Map.class);
            assertNotNull(map);
            assertEquals(1, map.size());
            LinkedHashMap dataField = (LinkedHashMap) map.get("V1");
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            List data = (List) dataField.get("data");
            assertEquals(1, data.size());
            LinkedHashMap point0 = (LinkedHashMap) data.get(0);
            assertEquals(123, (Double) point0.values().iterator().next(), 0.0001);

            System.out.println(responseString);
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
                new WebSource("some Label", SourceType.RAW_DATA, "#334455", GraphType.LINE, false, new DataStream((byte) 1, PinType.VIRTUAL))
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new WebProductAndOrgId(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi.id;
        }
    }

}
