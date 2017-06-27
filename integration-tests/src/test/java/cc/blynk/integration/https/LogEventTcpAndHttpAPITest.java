package cc.blynk.integration.https;

import cc.blynk.integration.IntegrationBase;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.application.AppServer;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.*;
import cc.blynk.server.core.model.web.product.events.CriticalEvent;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.hardware.HardwareServer;
import cc.blynk.server.http.web.Comment;
import cc.blynk.utils.JsonParser;
import org.apache.http.client.methods.*;
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

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.CRITICAL, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
            assertEquals("Temp is super high", logEvents[0].name);
            assertEquals("This is my description", logEvents[0].description);

            System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(timeLineResponse));
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

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
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
    public void testTimneLineIsFetchedEventProductWasRemoved() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high\0" + "MyNewDescription");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        long now = System.currentTimeMillis();

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.CRITICAL, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
            assertEquals("Temp is super high", logEvents[0].name);
            assertEquals("MyNewDescription", logEvents[0].description);
        }

        HttpDelete deleteReq = new HttpDelete(httpsAdminServerUrl + "/product/1");
        try (CloseableHttpResponse response = httpclient.execute(deleteReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.CRITICAL, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
            assertNull(logEvents[0].name);
            assertEquals("MyNewDescription", logEvents[0].description);
        }
    }

    @Test
    public void testEmailNotificationWorkWithEvent() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high\0" + "MyNewDescription");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        long now = System.currentTimeMillis();

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.CRITICAL, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
            assertEquals("Temp is super high", logEvents[0].name);
            assertEquals("MyNewDescription", logEvents[0].description);
        }

        verify(mailWrapper, timeout(1000)).sendHtml(eq("dmitriy@blynk.cc"), eq("You received event."), eq("Temp is super high"));
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

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?from=0&to=" + now + "&limit=10&offset=0&isResolved=true");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertNotNull(logEvents);
            assertEquals(0, logEvents.length);
        }
    }

    @Test
    public void testDeviceIsOnlineIndicatorCorrect() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(2, devices.length);
            Device device = devices[1];
            assertNotNull(device);
            assertEquals(Status.ONLINE, device.status);
        }

        newHardClient.stop();

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(2, devices.length);
            Device device = devices[1];
            assertNotNull(device);
            assertEquals(Status.OFFLINE, device.status);
        }
    }

    @Test
    public void testResolveLogEventFlow() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("logEvent temp_is_high");
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        long now = System.currentTimeMillis();

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0");
        int logEventId;
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(1, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            logEventId = logEvents[0].id;
            assertTrue(logEventId > 1);
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/devices/1/1/resolveEvent/" + logEventId);
        post.setEntity(new StringEntity(new Comment("123").toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0&isResolved=true");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(0, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(1, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertNotNull(logEvents);
            assertEquals(1, logEvents.length);
            int oldLogEventId = logEvents[0].id;
            assertEquals(logEventId, oldLogEventId);
            assertTrue(logEvents[0].isResolved);
            assertEquals(System.currentTimeMillis(), logEvents[0].resolvedAt, 5000);
            assertEquals("123", logEvents[0].resolvedComment);
        }

        getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?eventType=CRITICAL&from=0&to=" + now + "&limit=10&offset=0&isResolved=false");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(0, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(1, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
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

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            DeviceTest[] devices = JsonParser.readAny(responseString, DeviceTest[].class);
            assertNotNull(devices);
            assertEquals(2, devices.length);
            for (DeviceTest deviceTest : devices) {
                if (deviceTest.id == 1) {
                    assertEquals(Integer.valueOf(1), deviceTest.criticalSinceLastView);
                    assertNull(deviceTest.warningSinceLastView);
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
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, 123D),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false)
        };
        CriticalEvent event = new CriticalEvent();
        event.name = "Temp is super high";
        event.eventCode = "temp_is_high";
        event.description = "This is my description";
        event.emailNotifications = new EventReceiver[] {
                new EventReceiver(6, MetadataType.Contact, "Farm of Smith")
        };
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


        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
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

    @Test
    public void testSystemEventsCreated() throws Exception {
        String token = createProductAndDevice();

        TestHardClient newHardClient = new TestHardClient("localhost", tcpHardPort);
        newHardClient.start();
        newHardClient.send("login " + token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        HttpGet getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?from=0&to=" + System.currentTimeMillis() + "&limit=10&offset=0");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(0, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.ONLINE, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
        }

        newHardClient.stop();
        //we have to wait until DB query will be executed and ignore period will pass. it is 1000 millis.
        sleep(1100);

        getEvents = new HttpGet(httpsAdminServerUrl + "/devices/1/1/timeline?from=0&to=" +
                System.currentTimeMillis() + "&limit=10&offset=0" + "&eventType=OFFLINE");
        try (CloseableHttpResponse response = httpclient.execute(getEvents)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            TimeLineResponse timeLineResponse = JsonParser.readAny(responseString, TimeLineResponse.class);
            assertNotNull(timeLineResponse);
            assertEquals(0, timeLineResponse.totalCritical);
            assertEquals(0, timeLineResponse.totalWarning);
            assertEquals(0, timeLineResponse.totalResolved);
            LogEvent[] logEvents = timeLineResponse.logEvents;
            assertNotNull(timeLineResponse.logEvents);
            assertEquals(1, logEvents.length);
            assertEquals(1, logEvents[0].deviceId);
            assertEquals(EventType.OFFLINE, logEvents[0].eventType);
            assertFalse(logEvents[0].isResolved);
        }
    }

    public static class TimeLineResponse {
        private int totalCritical;
        private int totalWarning;
        private int totalResolved;
        private LogEvent[] logEvents;
    }

    public static class DeviceTest extends Device {
        Integer criticalSinceLastView;
        Integer warningSinceLastView;
    }

}
