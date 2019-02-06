package cc.blynk.integration.mobile;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.controls.Terminal;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.ButtonTileTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.appSync;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.integration.TestUtil.ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceTilesTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void createPageTemplate() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        appClient.createWidget(dashBoard.id, deviceTiles);
        appClient.verifyResult(ok(3));

        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);

        appClient.createTemplate(dashBoard.id, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        appClient.getWidget(dashBoard.id, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(5), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertEquals(-231, deviceTiles.color);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertEquals("name", deviceTiles.templates[0].name);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals(0, deviceTiles.tiles.length);
        assertEquals(-75056000, pageTileTemplate.color);
        assertEquals(-231, pageTileTemplate.tileColor);
    }

    @Test
    public void createDeviceTilesAndEditColors() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = 0;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short) 1, PinType.VIRTUAL),
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        deviceTiles.color = -231;

        appClient.updateWidget(1, deviceTiles);
        appClient.verifyResult(ok(5));

        tileTemplate = new PageTileTemplate(1,
                null, null, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short) 1, PinType.VIRTUAL),
                false, null, null, null, -1, -231, FontSize.LARGE, false, 2);

        appClient.updateTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(6));

        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(7), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertEquals(-231, deviceTiles.color);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertEquals("name", deviceTiles.templates[0].name);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals(0, deviceTiles.tiles.length);
        assertEquals(-1, pageTileTemplate.color);
        assertEquals(-231, pageTileTemplate.tileColor);
    }

    @Test
    public void createPageTemplateWithOutModeField() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        appClient.createTemplate(1, widgetId, "{\"id\":1,\"templateId\":\"123\",\"name\":\"name\",\"iconName\":\"iconName\",\"boardType\":\"ESP8266\",\"showDeviceName\":false,\"color\":0,\"tileColor\":0,\"fontSize\":\"LARGE\",\"showTileLabel\":false,\"pin\":{\"pin\":1,\"pwmMode\":false,\"rangeMappingOn\":false,\"pinType\":\"VIRTUAL\",\"min\":0.0,\"max\":255.0}}");
        appClient.verifyResult(ok(4));

        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(5), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertEquals("name", deviceTiles.templates[0].name);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        assertEquals(0, deviceTiles.tiles.length);
    }

    @Test
    public void createButtonTileTemplate() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        ButtonTileTemplate tileTemplate = new ButtonTileTemplate(1,
                null, null, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short) 1, PinType.VIRTUAL),
                false, false, false, null, null);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(5), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertEquals("name", deviceTiles.templates[0].name);
        assertTrue(deviceTiles.templates[0] instanceof ButtonTileTemplate);
        assertEquals(0, deviceTiles.tiles.length);
    }

    @Test
    public void updateViaHttpAPIWorksForDeviceTiles() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        appClient.getDevices(1);
        Device[] devices = appClient.parseDevices(4);
        Device device = devices[0];
        assertNotNull(device);
        assertNotNull(device.token);

        DataStream dataStream = new DataStream((short) 5, PinType.VIRTUAL);
        TileTemplate tileTemplate = new PageTileTemplate(1,
                null, new int[] {device.id}, "name", "name", "iconName", BoardType.ESP8266, dataStream,
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(5));

        CloseableHttpClient httpsClient = getDefaultHttpsClient();

        String httpsServerUrl = String.format("https://localhost:%s/external/api/", properties.getHttpsPort());
        HttpGet request = new HttpGet(httpsServerUrl + device.token + "/update/v5?value=111");

        try (CloseableHttpResponse response = httpsClient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        appClient.verifyResult(hardware(111, clientPair.latestDeviceId + " vw 5 111"));
        httpsClient.close();
    }

    @Test
    public void testDeviceTileAndWidgetWithMultipleValues() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        var deviceTiles = new DeviceTiles();
        deviceTiles.id = 21321;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        var tileTemplate = new PageTileTemplate(1,
                null, new int[] {0}, "name", "name", "iconName", BoardType.ESP8266, new DataStream((short) 5, PinType.VIRTUAL),
                false, null, null, null, 0, 0, FontSize.LARGE, false, 2);

        appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        appClient.verifyResult(ok(4));

        var terminal = new Terminal();
        terminal.width = 2;
        terminal.height = 2;
        terminal.pin = 6;
        terminal.pinType = PinType.VIRTUAL;

        appClient.createWidget(1, deviceTiles.id, 1, terminal);
        appClient.verifyResult(ok(5));

        //send value after we have tile for that pin
        clientPair.hardwareClient.send("hardware vw 6 111");
        clientPair.hardwareClient.send("hardware vw 6 112");
        appClient.verifyResult(hardware(1, clientPair.latestDeviceId + " vw 6 111"));
        appClient.verifyResult(hardware(2, clientPair.latestDeviceId + " vw 6 112"));

        appClient.reset();
        appClient.sync(clientPair.latestDeviceId);
        appClient.verifyResult(ok(1));
        //todo fix this. it should work too
        //appClient.verifyResult(appSync(b("1 vw 6 111")));
        appClient.verifyResult(appSync(b(clientPair.latestDeviceId + " vw 6 112")));
    }

}
