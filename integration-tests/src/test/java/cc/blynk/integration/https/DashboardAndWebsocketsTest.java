package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod;
import cc.blynk.server.core.model.widgets.web.WebLineGraph;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.WebSwitch;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.server.core.protocol.enums.Response;
import cc.blynk.server.core.protocol.model.messages.BinaryMessage;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
import cc.blynk.server.core.reporting.average.AggregationKey;
import cc.blynk.server.core.reporting.average.AggregationValue;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static cc.blynk.integration.TestUtil.appSync;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.AVG;
import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphPeriod.LIVE;
import static cc.blynk.server.core.reporting.average.AverageAggregatorProcessor.MINUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
public class DashboardAndWebsocketsTest extends APIBaseTest {

    private BaseServer hardwareServer;
    private String tempDir;

    @Before
    public void init() throws Exception {
        super.init();
        this.hardwareServer = new HardwareAndHttpAPIServer(holder).start();
        this.tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", "1");
        org.apache.commons.io.FileUtils.deleteDirectory(userReportFolder.toFile());
        Files.createDirectories(userReportFolder);
        //clean everything just in case
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_average_minute");
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_average_hourly");
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_average_daily");
    }

    @After
    public void shutdownHardwareServer() {
        this.hardwareServer.close();
    }

    @Test
    public void createDeviceWithWidgets() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct().id;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        Device device;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.token);
            assertNotNull(device.metaFields);
            assertEquals(3, device.metaFields.length);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(1, numberMetaField.roleIds[0]);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
            assertEquals(regularUser.email, device.activatedBy);
            assertNotNull(device.webDashboard);
            assertEquals(3, device.webDashboard.widgets.length);
            assertTrue(device.webDashboard.widgets[0] instanceof WebSwitch);
            WebSwitch webSwitch = (WebSwitch) device.webDashboard.widgets[0];
            assertEquals(1, webSwitch.sources[0].dataStream.pin);
            assertEquals(PinType.VIRTUAL, webSwitch.sources[0].dataStream.pinType);
            assertEquals("123", webSwitch.label);
        }

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(device.id);
        appWebSocketClient.verifyResult(ok(2));

        TestHardClient testHardClient = new TestHardClient("localhost", properties.getHttpPort());
        testHardClient.start();
        testHardClient.login(device.token);
        testHardClient.verifyResult(ok(1));

        appWebSocketClient.send("hardware " + device.id + " vw 1 222");
        testHardClient.verifyResult(new HardwareMessage(3, b("vw 1 222")));

        appWebSocketClient.reset();
        appWebSocketClient.getDevice(regularUser.orgId, device.id);
        Device deviceDTO = appWebSocketClient.parseDevice(1);
        assertNotNull(deviceDTO);
        assertEquals("My New Device", deviceDTO.name);
        assertNotNull(deviceDTO.webDashboard);
        assertEquals(3, deviceDTO.webDashboard.widgets.length);
        WebSwitch webSwitch = (WebSwitch) deviceDTO.webDashboard.widgets[0];
        assertEquals("222", webSwitch.sources[0].dataStream.value);
    }

    @Test
    public void webSocketRetrievesCommandFromExternalApi() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct().id;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        Device device;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.token);
            assertNotNull(device.metaFields);
            assertEquals(3, device.metaFields.length);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(1, numberMetaField.roleIds[0]);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertEquals(System.currentTimeMillis(), device.activatedAt, 5000);
            assertEquals(regularUser.email, device.activatedBy);
            assertNotNull(device.webDashboard);
            assertEquals(3, device.webDashboard.widgets.length);
            assertTrue(device.webDashboard.widgets[0] instanceof WebSwitch);
            WebSwitch webSwitch = (WebSwitch) device.webDashboard.widgets[0];
            assertEquals(1, webSwitch.sources[0].dataStream.pin);
            assertEquals(PinType.VIRTUAL, webSwitch.sources[0].dataStream.pinType);
            assertEquals("123", webSwitch.label);
        }

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(device.id);
        appWebSocketClient.verifyResult(ok(2));

        String apiUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());

        HttpGet putValueViaGet = new HttpGet(apiUrl + device.token + "/update/v10?value=1");

        try (CloseableHttpResponse response = httpclient.execute(putValueViaGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        appWebSocketClient.verifyResult(new HardwareMessage(111, b("1 vw 10 1")));
    }

    @Test
    public void webSocketWrongTrackCommand() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));

        AppWebSocketClient appWebSocketClient2 = TestUtil.defaultClient();
        appWebSocketClient2.start();
        appWebSocketClient2.login(regularUser);
        appWebSocketClient2.verifyResult(ok(1));
        appWebSocketClient2.send("trackDevice null");
        appWebSocketClient2.verifyResult(webJson(2, "Error parsing number. For input string: \"null\""));
    }

    @Test
    public void webSocketDoesNotRetrieveCommandForItself() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(0);
        appWebSocketClient.verifyResult(ok(2));

        AppWebSocketClient appWebSocketClient2 = TestUtil.defaultClient();
        appWebSocketClient2.start();
        appWebSocketClient2.login(regularUser);
        appWebSocketClient2.verifyResult(ok(1));
        appWebSocketClient2.trackDevice(0);
        appWebSocketClient2.verifyResult(ok(2));

        appWebSocketClient.send("hardware 0 vw 10 100");
        appWebSocketClient2.verifyResult(appSync(3, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(3, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(1111, b("0 vw 10 100")));
    }

    @Test
    public void mobileGetHardwarecommandFromWebSocket() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(0);
        appWebSocketClient.verifyResult(ok(2));

        TestAppClient appClient = new TestAppClient("localhost", properties);
        appClient.start();
        appClient.loginNoHash(regularUser.email, regularUser.pass);
        appClient.verifyResult(ok(1));

        appWebSocketClient.send("hardware 0 vw 10 100");
        appClient.verifyResult(appSync(3, "0 vw 10 100"));

        appClient.sync(0);
        appClient.verifyResult(ok(2));
        appClient.verifyResult(appSync(1111, b("0 vw 10 100")));

    }

    @Test
    public void trackCommandWorks() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));

        AppWebSocketClient appWebSocketClient2 = TestUtil.defaultClient();
        appWebSocketClient2.start();
        appWebSocketClient2.login(regularUser);
        appWebSocketClient2.verifyResult(ok(1));

        //do not trackDevice any device by purpose
        //appWebSocketClient2.trackDevice(0);
        appWebSocketClient.send("hardware 0 vw 10 100");
        appWebSocketClient2.never(appSync(2, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(2, b("0 vw 10 100")));
    }

    @Test
    public void commandFromAppIsRetrievedByWeb() throws Exception {
        login(regularUser.email, regularUser.pass);

        Product product = createProduct();

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = product.id;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals(1, device.id);
        }

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(1);
        appWebSocketClient.verifyResult(ok(2));

        TestAppClient appClient = new TestAppClient("localhost", properties);
        appClient.start();
        appClient.loginNoHash(regularUser.email, regularUser.pass);
        appClient.verifyResult(ok(1));

        appClient.activate(0);
        appClient.verifyResult(webJson(2, "Device not in the network.", Response.DEVICE_NOT_IN_NETWORK));

        appClient.send("hardware 1 vw 2 222");
        appWebSocketClient.verifyResult(appSync(3, b("1 vw 2 222")));

        appWebSocketClient.send("hardware 1 vw 10 100");
        appClient.verifyResult(appSync(3, b("1 vw 10 100")));
        appClient.verifyResult(webJson(3, "Device not in the network.", Response.DEVICE_NOT_IN_NETWORK));
    }

    @Test
    public void senderShouldNotGetCommandHeSent() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(0);
        appWebSocketClient.verifyResult(ok(2));

        AppWebSocketClient appWebSocketClient2 = TestUtil.defaultClient();
        appWebSocketClient2.start();
        appWebSocketClient2.login(regularUser);
        appWebSocketClient2.verifyResult(ok(1));
        appWebSocketClient2.trackDevice(0);
        appWebSocketClient2.verifyResult(ok(2));

        appWebSocketClient.send("hardware 0 vw 10 100");
        appWebSocketClient2.verifyResult(appSync(3, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(3, b("0 vw 10 100")));
    }

    @Test
    public void trackCommandWorks2() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));

        AppWebSocketClient appWebSocketClient2 = TestUtil.defaultClient();
        appWebSocketClient2.start();
        appWebSocketClient2.login(regularUser);
        appWebSocketClient2.verifyResult(ok(1));

        //do not trackDevice any device by purpose
        //appWebSocketClient2.trackDevice(0);
        appWebSocketClient.send("hardware 0 vw 10 100");
        appWebSocketClient2.never(appSync(2, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(2, b("0 vw 10 100")));

        appWebSocketClient2.trackDevice(0);
        appWebSocketClient2.verifyResult(ok(2));

        appWebSocketClient.trackDevice(0);
        appWebSocketClient.verifyResult(ok(3));
        appWebSocketClient.send("hardware 0 vw 10 100");
        appWebSocketClient2.verifyResult(appSync(4, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(4, b("0 vw 10 100")));

        appWebSocketClient.send("hardware 1 vw 10 100");
        appWebSocketClient2.never(appSync(5, b("1 vw 10 100")));
        appWebSocketClient.never(appSync(5, b("1 vw 10 100")));

        appWebSocketClient2.trackDevice(-1);
        appWebSocketClient2.verifyResult(webJson(3, "Requested device not exists."));

        appWebSocketClient.send("hardware 0 vw 10 100");
        appWebSocketClient2.never(appSync(6, b("0 vw 10 100")));
        appWebSocketClient.never(appSync(6, b("0 vw 10 100")));
    }

    @Test
    public void getWebGraphData() throws Exception {
        login(regularUser.email, regularUser.pass);
        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct().id;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.reset();

        long now = System.currentTimeMillis();
        AggregationValue aggregationValue = new AggregationValue();
        aggregationValue.update(1.11D);
        AggregationKey aggregationKey1 = new AggregationKey(regularUser.orgId, 1, PinType.VIRTUAL, (byte) 3, (now - 180_000) / MINUTE);
        Map<AggregationKey, AggregationValue> data = new HashMap<>();
        data.put(aggregationKey1, aggregationValue);

        AggregationValue aggregationValue2 = new AggregationValue();
        aggregationValue2.update(1.22D);
        AggregationKey aggregationKey2 = new AggregationKey(regularUser.orgId, 1, PinType.VIRTUAL, (byte) 3, (now - 120_000) / MINUTE);
        data.put(aggregationKey2, aggregationValue2);

        AggregationValue aggregationValue3 = new AggregationValue();
        aggregationValue3.update(1.33D);
        AggregationKey aggregationKey3 = new AggregationKey(regularUser.orgId, 1, PinType.VIRTUAL, (byte) 4, (now - 60_000) / MINUTE);
        data.put(aggregationKey3, aggregationValue3);
        holder.reportingDBManager.reportingDBDao.insert(data, GraphGranularityType.MINUTE);

        appWebSocketClient.getGraphData(1, 432, GraphPeriod.DAY);

        BinaryMessage graphDataResponse = appWebSocketClient.getBinaryBody();
        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = graphDataResponse.getBytes();
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(2, bb.getInt());
        assertEquals(1.11D, bb.getDouble(), 0.1);
        assertEquals(aggregationKey1.getTs(GraphGranularityType.MINUTE), bb.getLong());
        assertEquals(1.22D, bb.getDouble(), 0.1);
        assertEquals(aggregationKey2.getTs(GraphGranularityType.MINUTE), bb.getLong());
        assertEquals(1, bb.getInt());
        assertEquals(1.33D, bb.getDouble(), 0.1);
        assertEquals(aggregationKey3.getTs(GraphGranularityType.MINUTE), bb.getLong());

    }

    @Test
    public void getLiveWebGraphData() throws Exception {
        login(regularUser.email, regularUser.pass);
        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct().id;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        Device device;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + device.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.reset();

        newHardClient.send("hardware vw 3 123");
        appWebSocketClient.getGraphData(1, 432, LIVE);

        BinaryMessage graphDataResponse = appWebSocketClient.getBinaryBody();
        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = graphDataResponse.getBytes();
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(1, bb.getInt());
        assertEquals(123D, bb.getDouble(), 0.1);
        assertEquals(System.currentTimeMillis(), bb.getLong(), 5000);
    }

    @Test
    public void getCustomWebGraphData() throws Exception {
        login(regularUser.email, regularUser.pass);
        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct().id;

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        Device device;
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            device = JsonParser.parseDevice(responseString, 0);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
        }

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.reset();

        String tempDir = holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", regularUser.email);
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }

        var now = System.currentTimeMillis();
        var aggregationValue = new AggregationValue();
        aggregationValue.update(1.11D);
        Map<AggregationKey, AggregationValue> data = new HashMap<>();
        data.put(
                new AggregationKey(1, 1, PinType.VIRTUAL, (byte) 3, (now - 60_000) / MINUTE),
                aggregationValue
        );

        aggregationValue = new AggregationValue();
        aggregationValue.update(1.22D);
        data.put(
                new AggregationKey(1, 1, PinType.VIRTUAL, (byte) 3, now / MINUTE),
                aggregationValue
        );
        holder.reportingDBManager.reportingDBDao.insert(data, GraphGranularityType.MINUTE);

        appWebSocketClient.send("getenhanceddata 1" + b(" 432 CUSTOM " + (now - 120_000) + " " + now));

        BinaryMessage graphDataResponse = appWebSocketClient.getBinaryBody();
        assertNotNull(graphDataResponse);
        byte[] decompressedGraphData = graphDataResponse.getBytes();
        ByteBuffer bb = ByteBuffer.wrap(decompressedGraphData);

        assertEquals(1, bb.getInt());
        assertEquals(2, bb.getInt());
        assertEquals(1.11D, bb.getDouble(), 0.1);
        assertEquals(((now - 60_000) / MINUTE) * MINUTE, bb.getLong());
        assertEquals(1.22D, bb.getDouble(), 0.1);
        assertEquals((now / MINUTE) * MINUTE, bb.getLong());
    }

    @Test
    @Ignore
    //todo fix later
    public void webSocketRetrievesCommandFromExternalApiForOneDeviceOnly() throws Exception {
        login(regularUser.email, regularUser.pass);

        Product product = createProduct();

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = product.id;

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.productId = newDevice.productId;

        String token;
        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            token = device.token;
            assertEquals(1, device.id);
        }

        HttpPut httpPut2 = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut2.setEntity(new StringEntity(newDevice2.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(httpPut2)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device device = JsonParser.parseDevice(responseString, 0);
            assertEquals(2, device.id);
        }

        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.login(regularUser);
        appWebSocketClient.verifyResult(ok(1));
        appWebSocketClient.trackDevice(1);

        String apiUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());

        HttpGet putValueViaGet = new HttpGet(apiUrl + token + "/update/v2?value=666");
        try (CloseableHttpResponse response = httpclient.execute(putValueViaGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
        appWebSocketClient.verifyResult(new HardwareMessage(111, b("1 vw 2 666")));

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(3, devices.length);

            Device device1 = getDeviceById(devices, 1);
            Device device2 = getDeviceById(devices, 2);
            assertEquals("666", ((WebLabel) device1.webDashboard.widgets[1]).sources[0].dataStream.value);
            assertNull(((WebLabel) device2.webDashboard.widgets[1]).sources[0].dataStream.value);
        }

        WebSwitch webSwitch = new WebSwitch();
        webSwitch.label = "123";
        webSwitch.id = 1;
        webSwitch.x = 1;
        webSwitch.y = 2;
        webSwitch.height = 10;
        webSwitch.width = 20;
        webSwitch.sources = new WebSource[] {
                new WebSource("some switch", "#334455",
                        false, RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        WebLabel webLabelNew = new WebLabel();
        webLabelNew.label = "123";
        webLabelNew.id = 3;
        webLabelNew.x = 4;
        webLabelNew.y = 2;
        webLabelNew.height = 10;
        webLabelNew.width = 20;
        webLabelNew.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 3, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard.widgets = new Widget[] {
                webSwitch,
                webLabel,
                webLabelNew
        };

        HttpPost updateReq = new HttpPost(httpsAdminServerUrl + "/product");
        updateReq.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, new ProductDTO(product)).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(updateReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(3, devices.length);

            Device device1 = getDeviceById(devices, 1);
            Device device2 = getDeviceById(devices, 2);
            assertEquals("666", ((WebLabel) device1.webDashboard.widgets[1]).sources[0].dataStream.value);
            assertNull(((WebLabel) device2.webDashboard.widgets[1]).sources[0].dataStream.value);
        }

        putValueViaGet = new HttpGet(apiUrl + token + "/update/v2?value=777");
        try (CloseableHttpResponse response = httpclient.execute(putValueViaGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
        appWebSocketClient.verifyResult(new HardwareMessage(111, b("1 vw 2 777")));

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(3, devices.length);

            Device device1 = getDeviceById(devices, 1);
            Device device2 = getDeviceById(devices, 2);
            assertEquals("777", ((WebLabel) device1.webDashboard.widgets[1]).sources[0].dataStream.value);
            assertNull(((WebLabel) device2.webDashboard.widgets[1]).sources[0].dataStream.value);
        }
    }

    private Product createProduct() throws Exception {
        Product product = new Product();
        product.name = "My Product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.logoUrl = "/logoUrl";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                createNumberMeta(1, "Jopa", 123D),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(3, "Device Owner", null, true)
        };

        WebSwitch webSwitch = new WebSwitch();
        webSwitch.label = "123";
        webSwitch.id = 1;
        webSwitch.x = 1;
        webSwitch.y = 2;
        webSwitch.height = 10;
        webSwitch.width = 20;
        webSwitch.sources = new WebSource[] {
                new WebSource("some switch", "#334455",
                        false, RAW_DATA, new DataStream((byte) 1, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        WebLineGraph webLineGraph = new WebLineGraph();
        webLineGraph.id = 432;
        webLineGraph.sources = new WebSource[] {
                new WebSource("Temperature", "#334455",
                        false, AVG, new DataStream((byte) 3, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false),
                new WebSource("Temperature", "#334455",
                        false, AVG, new DataStream((byte) 4, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webSwitch,
                webLabel,
                webLineGraph
        });

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, new ProductDTO(product)).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fromApi));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi;
        }
    }

    private static Device getDeviceById(Device[] devices, int id) {
        for (Device device : devices) {
            if (device.id == id) {
                return device;
            }
        }
        throw new RuntimeException("No device with id " + id);
    }

    public static class TestDevice extends Device {

        public String orgName;

        public String productName;

        public String productLogoUrl;

    }


}
