package cc.blynk.integration.https.reporting;

import cc.blynk.integration.IntegrationBase;
import cc.blynk.integration.https.APIBaseTest;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.api.http.dashboard.dto.DataDTO;
import cc.blynk.server.api.http.dashboard.dto.DataQueryRequestGroupDTO;
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
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.server.core.reporting.raw.RawDataProcessor;
import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataAPITest extends APIBaseTest {

    private BaseServer hardwareServer;
    private ClientPair clientPair;

    @Before
    public void init() throws Exception {
        super.init();
        this.hardwareServer = new HardwareAndHttpAPIServer(holder).start();

        this.clientPair = IntegrationBase.initAppAndHardPair();
        //clean everything just in case
        holder.dbManager.executeSQL("DELETE FROM " + TableDescriptor.BLYNK_DEFAULT_INSTANCE.tableName);
    }

    @After
    public void shutdown() {
        super.shutdown();
        this.hardwareServer.close();
        this.clientPair.stop();
    }

    @Test
    public void testInvalidRequestNoDataStream() throws Exception {
        login(regularUser.email, regularUser.pass);

        DataQueryRequestGroupDTO dataQueryRequestGroup = makeReq(null, 0, 0, System.currentTimeMillis());
        HttpPost getData = new HttpPost(httpsAdminServerUrl + "/data/1/history");
        getData.setEntity(new StringEntity(JsonParser.toJson(dataQueryRequestGroup), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"No data stream provided for request.\"}}", consumeText(response));
        }
    }

    @Test
    public void testInvalidRequestNoDevice() throws Exception {
        login(regularUser.email, regularUser.pass);

        DataQueryRequestGroupDTO dataQueryRequestGroup = makeReq(PinType.VIRTUAL, 1, 0, System.currentTimeMillis());
        HttpPost getData = new HttpPost(httpsAdminServerUrl + "/data/1/history");
        getData.setEntity(new StringEntity(JsonParser.toJson(dataQueryRequestGroup), APPLICATION_JSON));
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
        httpPut.setEntity(new StringEntity(newDevice.toString(), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(1, PinType.VIRTUAL, (byte) 1, "123");
        rawDataProcessor.collect(1, PinType.VIRTUAL, (byte) 1, "124");

        //invoking directly dao to avoid separate thread execution
        holder.dbManager.reportingDBDao.insertDataPoint(rawDataProcessor.rawStorage);

        DataQueryRequestGroupDTO dataQueryRequestGroup = makeReq(PinType.VIRTUAL, 1, 0, System.currentTimeMillis());
        HttpPost getData = new HttpPost(httpsAdminServerUrl + "/data/1/history");
        getData.setEntity(new StringEntity(JsonParser.toJson(dataQueryRequestGroup), APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            //expected string is
            //{"V1":{"data":[{"1504015510046":124.0},{"1504015510045":123.0}]}}

            String responseString = consumeText(response);
            DataDTO<ArrayList<LinkedHashMap>>[] obj = JsonParser.MAPPER.readValue(responseString, DataDTO[].class);
            assertNotNull(obj);
            assertEquals(1, obj.length);
            List dataField = obj[0].data;
            assertNotNull(dataField);
            assertEquals(2, dataField.size());

            LinkedHashMap point0 = (LinkedHashMap) dataField.get(0);
            LinkedHashMap point1 = (LinkedHashMap) dataField.get(1);

            assertTrue(point0.containsValue(123D));
            assertTrue(point1.containsValue(124D));

            System.out.println(responseString);
        }
    }

    @Test
    public void printDataStreamWithPinV100() throws Exception {
        DataStream dataStream = new DataStream(0, (byte) 100, false, false, PinType.VIRTUAL, null, 0, 255, null, null);
        System.out.println(JsonParser.init().writerWithDefaultPrettyPrinter().writeValueAsString(dataStream));
    }

    @Test
    public void testMultiPinRequest() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        RawDataProcessor rawDataProcessor = new RawDataProcessor(true);
        rawDataProcessor.collect(1, PinType.VIRTUAL, (byte) 1, "123");
        rawDataProcessor.collect(1, PinType.VIRTUAL, (byte) 2, "124");

        //invoking directly dao to avoid separate thread execution
        holder.dbManager.reportingDBDao.insertDataPoint(rawDataProcessor.rawStorage);

        DataQueryRequestGroupDTO dataQueryRequestGroup = new DataQueryRequestGroupDTO(new DataQueryRequestDTO[] {
                new DataQueryRequestDTO(RAW_DATA, PinType.VIRTUAL, (byte) 1,
                        null, null, null, null, 0, 1000, 0, System.currentTimeMillis()),
                new DataQueryRequestDTO(RAW_DATA, PinType.VIRTUAL, (byte) 2,
                        null, null, null, null, 0, 1000, 0, System.currentTimeMillis())
        });

        HttpPost getData = new HttpPost(httpsAdminServerUrl + "/data/1/history");
        getData.setEntity(new StringEntity(JsonParser.toJson(dataQueryRequestGroup), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            //expected string is
            //[
            //   {
            //      "data":[{"1507809234902":123.0}]
            //   },
            //   {
            //      "data":[{"1507809234902":124.0}]
            //   }
            //]
            System.out.println(responseString);

            DataDTO<ArrayList<LinkedHashMap>>[] obj = JsonParser.MAPPER.readValue(responseString, DataDTO[].class);
            assertNotNull(obj);
            assertEquals(2, obj.length);
            List dataField = obj[0].data;
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            LinkedHashMap point0 = (LinkedHashMap) dataField.get(0);
            assertTrue(point0.containsValue(123.0D));

            dataField = obj[1].data;
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            point0 = (LinkedHashMap) dataField.get(0);
            assertTrue(point0.containsValue(124.0D));
        }
    }

    @Test
    public void testInsertAPIWorks() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet insertPoint = new HttpGet(httpsAdminServerUrl + "/data/1/insert?dataStream=V1&value=123");
        try (CloseableHttpResponse response = httpclient.execute(insertPoint)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        long now = System.currentTimeMillis();

        DataQueryRequestGroupDTO dataQueryRequestGroup = makeReq(PinType.VIRTUAL, 1, 0, now);

        HttpPost getData = new HttpPost(httpsAdminServerUrl + "/data/1/history");
        getData.setEntity(new StringEntity(JsonParser.toJson(dataQueryRequestGroup), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(getData)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            //expected string is
            //[
            //   {
            //      "data":[{"1507809234902":123.0}]
            //   }
            //]

            String responseString = consumeText(response);
            DataDTO<ArrayList<LinkedHashMap>>[] obj = JsonParser.MAPPER.readValue(responseString, DataDTO[].class);
            assertNotNull(obj);
            assertEquals(1, obj.length);
            List dataField = obj[0].data;
            assertNotNull(dataField);
            assertEquals(1, dataField.size());

            LinkedHashMap point0 = (LinkedHashMap) dataField.get(0);
            assertTrue(point0.containsValue(123.0D));

            System.out.println(responseString);
        }
    }

    private static DataQueryRequestGroupDTO makeReq(PinType pinType, int pin, long from, long to) {
        return new DataQueryRequestGroupDTO(new DataQueryRequestDTO[] {
                new DataQueryRequestDTO(RAW_DATA, pinType, (byte) pin,
                        null, null, null, null, 0, 1000, from, to)
        });
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
                new WebSource("some Label", "#334455", false,
                        RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL), null, null, null, SortOrder.ASC, 10)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi.id;
        }
    }

}
