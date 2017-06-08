package cc.blynk.integration.https;

import cc.blynk.integration.IntegrationBase;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.application.AppServer;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.CriticalEvent;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.hardware.HardwareServer;
import cc.blynk.utils.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogEventTcpAndHttpAPITest extends APIBaseTest {

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
        holder.dbManager.executeSQL("DELETE FROM reporting_events");
    }

    @After
    public void shutdown() {
        super.shutdown();
        this.appServer.close();
        this.hardwareServer.close();
        this.clientPair.stop();
    }

    @Test
    public void testBasicLogEventFlow() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        long now = System.currentTimeMillis();

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/timeline/1?from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            LogEvent[] logEvents = JsonParser.readAny(responseString, LogEvent[].class);
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.CRITICAL, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
            assertEquals("Temp is super high", logEvents[0].name);
            assertEquals("This is my description", logEvents[0].description);

            System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logEvents));
        }
    }

    @Test
    public void testBasicLogEventWithOverrideDescriptionFlow() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high\0" + "MyNewDescription");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        long now = System.currentTimeMillis();

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/timeline/1?from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            LogEvent[] logEvents = JsonParser.readAny(responseString, LogEvent[].class);
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.CRITICAL, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
            assertEquals("Temp is super high", logEvents[0].name);
            assertEquals("MyNewDescription", logEvents[0].description);
        }
    }

    @Test
    public void testBasicLogEventWithIsResolvedFlow() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high\0" + "MyNewDescription");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        long now = System.currentTimeMillis();

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/timeline/1?from=0&to=" + now + "&limit=10&offset=0&isResolved=true");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            LogEvent[] logEvents = JsonParser.readAny(responseString, LogEvent[].class);
            assertNotNull(logEvents);
            assertEquals(0, logEvents.length);
        }
    }

    @Test
    public void testBasicLogEventFlowWithEventCounters() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            DeviceTest[] devices = JsonParser.readAny(responseString, DeviceTest[].class);
            assertNotNull(devices);
            assertEquals(2, devices.length);
            for (DeviceTest deviceTest : devices) {
                if (deviceTest.id == 1) {
                    assertEquals(Integer.valueOf(1), deviceTest.CRITICAL);
                    assertNull(deviceTest.WARNING);
                }
            }

            System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
        }
    }

    private String createProductAndDevice() throws Exception {
        login(admin.email, admin.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        CriticalEvent event = new CriticalEvent();
        event.name = "Temp is super high";
        event.eventCode = "temp_is_high";
        event.description = "This is my description";
        product.events = new Event[] {
                event
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(product.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertNotNull(fromApi.events);
            assertEquals(1, fromApi.events.length);
        }

        Device device = new Device();
        device.name = "My New Device";
        device.productId = 1;
        device.metaFields = new MetaField[] {
                new NumberMetaField("Jopa", Role.STAFF, 123D)
        };


        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices");
        httpPut.setEntity(new StringEntity(device.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            device = JsonParser.mapper.readValue(responseString, Device.class);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
            assertNotNull(device.token);

        }

        return device.token;
    }

    public static class DeviceTest extends Device {
        Integer CRITICAL;
        Integer WARNING;
    }

}
