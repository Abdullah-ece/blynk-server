package cc.blynk.integration.tcp;

import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.dao.ProvisionTokenValue;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.controls.Terminal;
import cc.blynk.server.core.model.widgets.outputs.ValueDisplay;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
import cc.blynk.server.notifications.push.android.AndroidGCMMessage;
import cc.blynk.server.notifications.push.enums.Priority;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.appSync;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.createDevice;
import static cc.blynk.integration.TestUtil.deviceConnected;
import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Response.DEVICE_NOT_IN_NETWORK;
import static cc.blynk.server.core.protocol.model.messages.MessageFactory.produce;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class DeviceWorkflowTest extends SingleServerInstancePerTest {

    private static int tcpHardPort;

    @BeforeClass
    public static void initPort() {
        tcpHardPort = properties.getHttpPort();
    }

    @Test
    public void testSendHardwareCommandToMultipleDevices() throws Exception {
        Device device0 = new Device();
        device0.id = 0;
        device0.name = "My Dashboard";
        device0.boardType = BoardType.Arduino_UNO;
        device0.status = Status.ONLINE;

        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;
        device1.status = Status.OFFLINE;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        clientPair.appClient.reset();

        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        assertNotNull(devices);
        assertEquals(2, devices.length);

        assertEqualDevice(device0, devices[0]);
        assertEqualDevice(device1, devices[1]);

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(ok(1));
        device1.status = Status.ONLINE;

        clientPair.appClient.send("hardware 1 vw 100 100");
        clientPair.hardwareClient.verifyResult(hardware(2, "vw 100 100"));
        hardClient2.never(hardware(2, "vw 1 100"));

        clientPair.appClient.send("hardware 1-0 vw 100 101");
        clientPair.hardwareClient.verifyResult(hardware(3, "vw 100 101"));
        hardClient2.never(hardware(3, "vw 1 101"));

        clientPair.appClient.send("hardware 1-1 vw 100 102");
        hardClient2.verifyResult(hardware(4, "vw 100 102"));
        clientPair.hardwareClient.never(hardware(4, "vw 100 102"));
    }

    @Test
    public void testDeviceWentOfflineMessage() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(device.token);
        hardClient2.verifyResult(ok(1));

        hardClient2.stop().await();

        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        verify(holder.gcmWrapper, timeout(500).times(1)).send(objectArgumentCaptor.capture(), any(), any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();

        String expectedJson = new AndroidGCMMessage("token", Priority.normal, null, "Your My Device went offline.", 1).toJson();
        assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testSendHardwareCommandToAppFromMultipleDevices() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        clientPair.appClient.reset();

        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-1"));

        clientPair.hardwareClient.send("hardware vw 100 101");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(1, b("1-0 vw 100 101"))));

        hardClient2.send("hardware vw 100 100");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new HardwareMessage(2, b("1-1 vw 100 100"))));
    }

    @Test
    public void testSendDeviceSpecificPMMessage() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":1}");
        clientPair.appClient.verifyResult(ok(1));

        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice(2);
        assertNotNull(device);
        assertNotNull(device.token);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(createDevice(2, device)));

        TestHardClient hardClient = new TestHardClient("localhost", tcpHardPort);
        hardClient.start();

        hardClient.login(device.token);
        hardClient.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-1"));

        String expectedBody = "pm 1 out";
        verify(hardClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE, b(expectedBody))));
        verify(hardClient.responseMock, times(2)).channelRead(any(), any());
        hardClient.stop().awaitUninterruptibly();
    }

    @Test
    public void testSendPMOnActivateForMultiDevices() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":33}");
        clientPair.appClient.verifyResult(ok(1));

        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice(2);
        assertNotNull(device);
        assertNotNull(device.token);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(createDevice(2, device)));

        TestHardClient hardClient = new TestHardClient("localhost", tcpHardPort);
        hardClient.start();

        hardClient.login(device.token);
        hardClient.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-1"));

        verify(hardClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE, b("pm 33 out"))));
        verify(hardClient.responseMock, times(2)).channelRead(any(), any());

        clientPair.appClient.deactivate(1);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(ok(3)));

        hardClient.reset();
        clientPair.hardwareClient.reset();

        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(ok(4));

        verify(hardClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE, b("pm 33 out"))));
        verify(hardClient.responseMock, times(1)).channelRead(any(), any());

        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE, b("pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in"))));
        verify(clientPair.hardwareClient.responseMock, times(1)).channelRead(any(), any());


        hardClient.stop().awaitUninterruptibly();
    }

    @Test
    public void testActivateForMultiDevices() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(ok(2));

        clientPair.appClient.activate(1);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(ok(3)));

        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, DEVICE_NOT_IN_NETWORK)));
    }

    @Test
    public void testActivateAndGetSyncForMultiDevices() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":33, \"value\":1}");
        clientPair.appClient.verifyResult(ok(1));

        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice(2);

        assertNotNull(device);
        assertNotNull(device.token);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(createDevice(2, device)));

        clientPair.appClient.reset();
        clientPair.appClient.activate(1);

        verify(clientPair.appClient.responseMock, timeout(500).times(13)).channelRead(any(), any());

        clientPair.appClient.verifyResult(ok(1));

        clientPair.appClient.verifyResult(appSync(b("1-0 dw 1 1")));
        clientPair.appClient.verifyResult(appSync(b("1-0 dw 2 1")));
        clientPair.appClient.verifyResult(appSync(b("1-0 aw 3 0")));
        clientPair.appClient.verifyResult(appSync(b("1-0 dw 5 1")));
        clientPair.appClient.verifyResult(appSync(b("1-0 vw 4 244")));
        clientPair.appClient.verifyResult(appSync(b("1-0 aw 7 3")));
        clientPair.appClient.verifyResult(appSync(b("1-0 aw 30 3")));
        clientPair.appClient.verifyResult(appSync(b("1-0 vw 0 89.888037459418")));
        clientPair.appClient.verifyResult(appSync(b("1-0 vw 11 -58.74774244674501")));
        clientPair.appClient.verifyResult(appSync(b("1-0 vw 13 60 143 158")));
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(appSync(b("1-1 dw 33 1"))));
    }

    @Test
    public void testOfflineOnlineStatusForMultiDevices() throws Exception {
        Device device0 = new Device();
        device0.id = 0;
        device0.name = "My Dashboard";
        device0.boardType = BoardType.Arduino_UNO;

        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(device.token);
        hardClient2.verifyResult(ok(1));

        device0.status = Status.ONLINE;
        device1.status = Status.ONLINE;

        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(3);
        assertNotNull(devices);
        assertEquals(2, devices.length);

        assertEqualDevice(device0, devices[0]);
        assertEqualDevice(device1, devices[1]);

        hardClient2.stop().await();
        device1.status = Status.OFFLINE;

        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");

        devices = clientPair.appClient.parseDevices();
        assertNotNull(devices);
        assertEquals(2, devices.length);

        assertEqualDevice(device0, devices[0]);
        assertEqualDevice(device1, devices[1]);
    }

    @Test
    public void testCorrectOnlineStatusForDisconnect() throws Exception {
        Device device0 = new Device();
        device0.id = 0;
        device0.name = "My Dashboard";
        device0.boardType = BoardType.Arduino_UNO;
        device0.status = Status.ONLINE;

        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        assertNotNull(devices);
        assertEquals(1, devices.length);

        assertEqualDevice(device0, devices[0]);

        clientPair.hardwareClient.stop().await();
        device0.status = Status.OFFLINE;

        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices(2);

        assertNotNull(devices);
        assertEquals(1, devices.length);

        assertEqualDevice(device0, devices[0]);
        assertEquals(System.currentTimeMillis(), devices[0].disconnectTime, 5000);
    }

    @Test
    public void testCorrectConnectTime() throws Exception {
        long now = System.currentTimeMillis();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        assertNotNull(devices);
        assertEquals(1, devices.length);
        assertEquals(now, devices[0].connectTime, 10000);
    }

    @Test
    public void testCorrectOnlineStatusForReconnect() throws Exception {
        Device device0 = new Device();
        device0.id = 0;
        device0.name = "My Dashboard";
        device0.boardType = BoardType.Arduino_UNO;
        device0.status = Status.ONLINE;

        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        assertNotNull(devices);
        assertEquals(1, devices.length);

        assertEqualDevice(device0, devices[0]);

        clientPair.hardwareClient.stop().await();

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(devices[0].token);
        hardClient2.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-0"));

        clientPair.appClient.reset();

        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices();

        assertNotNull(devices);
        assertEquals(1, devices.length);

        assertEqualDevice(device0, devices[0]);
    }


    @Test
    public void testHardwareChannelClosedOnDashRemoval() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(device.token);
        hardClient2.verifyResult(ok(1));

        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(ok(2));

        long tries = 0;
        //waiting for channel to be closed.
        //but only limited amount if time
        while (!clientPair.hardwareClient.isClosed() && tries < 100) {
            sleep(10);
            tries++;
        }

        assertTrue(clientPair.hardwareClient.isClosed());
        assertTrue(hardClient2.isClosed());
    }

    @Test
    public void testHardwareDataRemovedWhenDeviceRemoved() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);

        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        ValueDisplay valueDisplay = new ValueDisplay();
        valueDisplay.id = 11111;
        valueDisplay.x = 1;
        valueDisplay.y = 2;
        valueDisplay.height = 1;
        valueDisplay.width = 1;
        valueDisplay.deviceId = 1;
        valueDisplay.pin = 1;
        valueDisplay.pinType = PinType.VIRTUAL;
        clientPair.appClient.createWidget(1, valueDisplay);
        clientPair.appClient.verifyResult(ok(2));

        Terminal terminal = new Terminal();
        terminal.id = 11112;
        terminal.x = 1;
        terminal.y = 2;
        terminal.height = 1;
        terminal.width = 1;
        terminal.deviceId = 1;
        terminal.pin = 3;
        terminal.pinType = PinType.VIRTUAL;
        clientPair.appClient.createWidget(1, terminal);
        clientPair.appClient.verifyResult(ok(3));

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(device.token);
        hardClient2.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-1"));

        hardClient2.send("hardware vw 1 123");
        clientPair.appClient.verifyResult(hardware(2, "1-1 vw 1 123"));

        hardClient2.send("hardware vw 2 124");
        clientPair.appClient.verifyResult(hardware(3, "1-1 vw 2 124"));

        hardClient2.send("hardware vw 3 125");
        clientPair.appClient.verifyResult(hardware(4, "1-1 vw 3 125"));

        hardClient2.send("hardware vw 3 126");
        clientPair.appClient.verifyResult(hardware(5, "1-1 vw 3 126"));

        clientPair.appClient.send("deleteDevice 1\0" + "1");
        verify(clientPair.appClient.responseMock, timeout(1000)).channelRead(any(), eq(ok(4)));

        clientPair.appClient.sync(1, 1);
        clientPair.appClient.neverAfter(500, appSync(1111, "1-1 vw 1 123"));
        clientPair.appClient.never(appSync(1111, "1-1 vw 2 124"));
        clientPair.appClient.never(appSync(1111, "1-1 vw 3 125"));
        clientPair.appClient.never(appSync(1111, "1-1 vw 3 126"));
    }

    @Test
    public void testTemporaryTokenWorksAsExpected() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.getProvisionToken(device1);
        device1 = clientPair.appClient.parseDevice(1);
        assertNotNull(device1);
        assertEquals(1, device1.id);
        assertEquals(32, device1.token.length());

        clientPair.appClient.send("loadProfileGzipped 1");
        DashBoard dash = clientPair.appClient.parseDash(2);
        assertNotNull(dash);
        //assertEquals(1, dash.devices.length);

        assertTrue(holder.deviceDao.getDeviceTokenValue(device1.token) instanceof ProvisionTokenValue);

        TestHardClient hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(device1.token);
        hardClient2.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-1"));

        clientPair.appClient.send("loadProfileGzipped 1");
        dash = clientPair.appClient.parseDash(4);
        assertNotNull(dash);
        //assertEquals(2, dash.devices.length);

        clientPair.appClient.reset();

        hardClient2 = new TestHardClient("localhost", tcpHardPort);
        hardClient2.start();

        hardClient2.login(device1.token);
        hardClient2.verifyResult(ok(1));
        clientPair.appClient.verifyResult(deviceConnected(1, "1-1"));

        clientPair.appClient.send("loadProfileGzipped 1");
        dash = clientPair.appClient.parseDash(2);
        assertNotNull(dash);
        //assertEquals(2, dash.devices.length);

        assertFalse(holder.deviceDao.getDeviceTokenValue(device1.token) instanceof ProvisionTokenValue);
        assertFalse(holder.deviceDao.clearTemporaryTokens());
    }

    @Test
    public void testCorrectRemovalForDeviceSelector() throws Exception {
        Device device1 = new Device();
        device1.id = 1;
        device1.name = "My Device";
        device1.boardType = BoardType.ESP8266;

        clientPair.appClient.createDevice(device1);
        Device device = clientPair.appClient.parseDevice();
        assertNotNull(device);
        assertNotNull(device.token);
        clientPair.appClient.verifyResult(createDevice(1, device));

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = 21321;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(ok(2));
        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "name", "name", "iconName", BoardType.ESP8266, null,
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(ok(3));

        deviceTiles = new DeviceTiles();
        deviceTiles.id = 21322;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(ok(4));
        tileTemplate = new PageTileTemplate(1,
                null, new int[]{0}, "name", "name", "iconName", BoardType.ESP8266, null,
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(ok(5));

        clientPair.appClient.send("addEnergy " + "100000" + "\0" + "1370-3990-1414-55681");
        clientPair.appClient.verifyResult(ok(6));

        deviceTiles = new DeviceTiles();
        deviceTiles.id = 21323;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(ok(7));
        tileTemplate = new PageTileTemplate(1,
                null, new int[]{1}, "name", "name", "iconName", BoardType.ESP8266, null,
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(ok(8));

        deviceTiles = new DeviceTiles();
        deviceTiles.id = 21324;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(ok(9));
        tileTemplate = new PageTileTemplate(1,
                null, new int[]{0, 1}, "name", "name", "iconName", BoardType.ESP8266, null,
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(ok(10));

        clientPair.appClient.deleteDevice(1, 1);
        clientPair.appClient.verifyResult(ok(11));

        clientPair.appClient.getWidget(1, 21321);
        deviceTiles = (DeviceTiles) clientPair.appClient.parseWidget(12);
        assertNotNull(deviceTiles);
        assertEquals(21321, deviceTiles.id);
        assertEquals(0, deviceTiles.tiles.length);
        assertEquals(0, deviceTiles.templates[0].deviceIds.length);

        clientPair.appClient.getWidget(1, 21322);
        deviceTiles = (DeviceTiles) clientPair.appClient.parseWidget(13);
        assertNotNull(deviceTiles);
        assertEquals(21322, deviceTiles.id);
        assertEquals(1, deviceTiles.tiles.length);
        assertEquals(1, deviceTiles.templates[0].deviceIds.length);
        assertEquals(0, deviceTiles.templates[0].deviceIds[0]);

        clientPair.appClient.getWidget(1, 21323);
        deviceTiles = (DeviceTiles) clientPair.appClient.parseWidget(14);
        assertNotNull(deviceTiles);
        assertEquals(21323, deviceTiles.id);
        assertEquals(0, deviceTiles.tiles.length);
        assertEquals(0, deviceTiles.templates[0].deviceIds.length);

        clientPair.appClient.getWidget(1, 21324);
        deviceTiles = (DeviceTiles) clientPair.appClient.parseWidget(15);
        assertNotNull(deviceTiles);
        assertEquals(21324, deviceTiles.id);
        assertEquals(1, deviceTiles.tiles.length);
        assertEquals(1, deviceTiles.templates[0].deviceIds.length);
        assertEquals(0, deviceTiles.templates[0].deviceIds[0]);
    }

    private static void assertEqualDevice(Device expected, Device real) {
        assertEquals(expected.id, real.id);
        //assertEquals(expected.name, real.name);
        assertEquals(expected.boardType, real.boardType);
        assertNotNull(real.token);
        assertEquals(expected.status, real.status);
    }

}
