package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.MetadataType;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.CriticalEvent;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.web.product.events.OfflineEvent;
import cc.blynk.server.core.model.web.product.events.OnlineEvent;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.web.handlers.logic.device.timeline.TimelineResponseDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.integration.TestUtil.hardwareConnected;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogEventTcpAndHttpAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Before
    public void clearDB() throws Exception {
        //clean everything just in case
        holder.reportingDBManager.executeSQL("DELETE FROM reporting_events");
    }

    @Test
    public void testBasicLogEventFlow() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        Device device = createProductAndDevice(client, orgId);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + device.token);
        newHardClient.verifyResult(ok(1));
        newHardClient.send("logEvent temp_is_high");
        newHardClient.verifyResult(ok(2));
        client.verifyResult(hardwareConnected(1, "0-1"));
        client.reset();

        long now = System.currentTimeMillis();
        client.getTimeline(orgId, device.id, EventType.CRITICAL, null, 0, now, 0, 10);
        TimelineResponseDTO timeLineResponse = client.parseTimelineResponse(1);
        assertNotNull(timeLineResponse);
        assertEquals(1, timeLineResponse.totalCritical);
        assertEquals(0, timeLineResponse.totalWarning);
        assertEquals(0, timeLineResponse.totalResolved);
        List<LogEvent> logEvents = timeLineResponse.eventList;
        assertNotNull(timeLineResponse.eventList);
        assertEquals(1, logEvents.size());
        assertEquals(1, logEvents.get(0).deviceId);
        assertEquals(EventType.CRITICAL, logEvents.get(0).eventType);
        assertFalse(logEvents.get(0).isResolved);
        assertEquals("Temp is super high", logEvents.get(0).name);
        assertEquals("This is my description", logEvents.get(0).description);
    }

    @Test
    //https://github.com/blynkkk/knight/issues/1276
    public void testDeviceQuicklyReconnects() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        Device device = createProductAndDevice(client, orgId);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + device.token);
        newHardClient.verifyResult(ok(1));
        client.verifyResult(hardwareConnected(1, "0-" + device.id));
        newHardClient.stop();
        client.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.send("login " + device.token);
        newHardClient.verifyResult(ok(1));
        client.verifyResult(hardwareConnected(1, "0-" + + device.id));
        newHardClient.stop();
        client.reset();

        //we have to wait for DB.
        sleep(500);

        long now = System.currentTimeMillis();
        client.getTimeline(orgId, device.id, null, null, 0, now, 0, 10);
        TimelineResponseDTO timeLineResponse = client.parseTimelineResponse(1);
        assertNotNull(timeLineResponse);
        assertEquals(0, timeLineResponse.totalCritical);
        assertEquals(0, timeLineResponse.totalWarning);
        assertEquals(0, timeLineResponse.totalResolved);
        List<LogEvent> logEvents = timeLineResponse.eventList;
        assertNotNull(timeLineResponse.eventList);
        assertEquals(4, logEvents.size());
        assertEquals(EventType.OFFLINE, logEvents.get(0).eventType);
        assertEquals(EventType.ONLINE, logEvents.get(1).eventType);
        assertEquals(EventType.OFFLINE, logEvents.get(2).eventType);
        assertEquals(EventType.ONLINE, logEvents.get(3).eventType);
    }

    private static Device createProductAndDevice(AppWebSocketClient client, int orgId) throws Exception {
        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, false, false, null, 0, 1000, 123D),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new TextMetaField(7, "Device Owner", Role.STAFF, false, false, false, null, "owner@blynk.cc")
        };

        Event criticalEvent = new CriticalEvent(
                1,
                "Temp is super high",
                "This is my description",
                false,
                "temp_is_high" ,
                new EventReceiver[] {
                        new EventReceiver(6, MetadataType.Contact, "Farm of Smith"),
                        new EventReceiver(7, MetadataType.Text, "Device Owner")
                },
                null,
                null
        );

        Event onlineEvent = new OnlineEvent(
                2,
                "Device is online!",
                null,
                false,
                new EventReceiver[] {
                        new EventReceiver(6, MetadataType.Contact, "Farm of Smith")
                },
                null,
                null
        );

        Event offlineEvent = new OfflineEvent(
                3,
                "Device is offline!",
                null,
                false,
                new EventReceiver[] {
                        new EventReceiver(6, MetadataType.Contact, "Farm of Smith")
                },
                null,
                null,
                0
        );

        product.events = new Event[] {
                criticalEvent,
                onlineEvent,
                offlineEvent
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device device = client.parseDevice(2);

        client.reset();
        return device;
    }

}
