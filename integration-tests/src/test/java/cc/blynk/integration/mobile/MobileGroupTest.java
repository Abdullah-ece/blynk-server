package cc.blynk.integration.mobile;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.LabeledValueDisplay;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.group.BaseGroupTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.group.Group;
import cc.blynk.server.core.model.widgets.ui.tiles.group.GroupLabel;
import cc.blynk.server.core.model.widgets.ui.tiles.group.SwitchWith3LabelsGroupTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.db.dao.RawEntryWithPin;
import cc.blynk.server.workers.GroupValueUpdaterWorker;
import cc.blynk.server.workers.ReportingWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.APIBaseTest.createTemplateIdMeta;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.deviceConnected;
import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.server.core.model.device.BoardType.ESP8266;
import static cc.blynk.server.core.protocol.enums.Command.MOBILE_HARDWARE_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class MobileGroupTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void createBasicGroupAndSendControlCommandAndViewAggregatedData() throws Exception {
        AppWebSocketClient webClient = loggedDefaultClient("super@blynk.cc", "1");

        //create basic product
        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        webClient.createProduct(product);
        ProductDTO fromApiProduct = webClient.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        TestAppClient mobileClient = new TestAppClient("localhost", properties.getHttpsPort());
        mobileClient.start();
        mobileClient.login(getUserName(), "1");
        mobileClient.verifyResult(ok(1));

        //create device tiles widget
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        mobileClient.createWidget(1, deviceTiles);
        mobileClient.verifyResult(ok(2));

        //create tile template for provisioned device
        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);
        mobileClient.createTemplate(1, widgetId, tileTemplate);
        mobileClient.verifyResult(ok(3));

        //create group template
        DataStream viewDataStream = new DataStream((short) 34, PinType.VIRTUAL, AggregationFunctionType.AVG);
        GroupLabel[] groupLabels = new GroupLabel[] {
                new GroupLabel(viewDataStream, null, null, -1, null, -1, -1, -1)
        };
        DataStream switchDataStream = new DataStream((short) 33, PinType.VIRTUAL);
        SwitchWith3LabelsGroupTemplate switchWith3LabelsGroupTemplate = new SwitchWith3LabelsGroupTemplate(
                1, null, "Base Group Template", null, null, null, -1,
                switchDataStream,
                -1,
                groupLabels
        );
        mobileClient.createGroupTemplate(1, widgetId, switchWith3LabelsGroupTemplate);
        mobileClient.verifyResult(ok(4));


        //provision device
        Device newDevice1 = new Device();
        newDevice1.name = "My New Device";
        newDevice1.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice1);
        Device deviceFromApi1 = mobileClient.parseDevice(5);
        assertNotNull(deviceFromApi1);
        assertNotNull(deviceFromApi1.token);
        //finally connecting the hardware1
        TestHardClient newHardClient1 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient1.start();
        newHardClient1.login(deviceFromApi1.token);
        newHardClient1.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi1.id));
        newHardClient1.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient1.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        mobileClient.reset();

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice2);
        Device deviceFromApi2 = mobileClient.parseDevice(1);
        assertNotNull(deviceFromApi2);
        assertNotNull(deviceFromApi2.token);
        //finally connecting the hardware2
        TestHardClient newHardClient2 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient2.start();
        newHardClient2.login(deviceFromApi2.token);
        newHardClient2.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi2.id));
        newHardClient2.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient2.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        mobileClient.reset();

        mobileClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(mobileClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(2, deviceTiles.tiles.length);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);
        assertNotNull(deviceTiles.groupTemplates);
        assertEquals(1, deviceTiles.groupTemplates.length);
        BaseGroupTemplate baseGroupTemplate = deviceTiles.groupTemplates[0];
        assertTrue(baseGroupTemplate instanceof SwitchWith3LabelsGroupTemplate);
        assertEquals(1, baseGroupTemplate.id);
        assertEquals("Base Group Template", baseGroupTemplate.name);

        Group group = new Group(
                10, "My new group", baseGroupTemplate.id,
                new int[] {deviceFromApi1.id, deviceFromApi2.id},
                new DataStream[] {switchDataStream},
                new DataStream[] {viewDataStream}
        );
        mobileClient.createGroup(1, widgetId, group);
        mobileClient.verifyResult(ok(2));


        mobileClient.hardwareForGroup(1, widgetId, group.id, switchDataStream.pin, "100");

        //check both hardware got command
        newHardClient1.verifyResult(hardware(3, "vw 33 100"));
        newHardClient2.verifyResult(hardware(3, "vw 33 100"));

        newHardClient1.send("hardware vw 34 200");
        newHardClient2.send("hardware vw 34 100");

        mobileClient.verifyResult(hardware(3, deviceFromApi1.id + " vw 34 200"));
        mobileClient.verifyResult(hardware(3, deviceFromApi2.id + " vw 34 100"));

        mobileClient.reset();
        mobileClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(mobileClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.groups);
        assertEquals(1, deviceTiles.groups.length);
        group = deviceTiles.groups[0];
        assertEquals("My new group", group.name);

        assertEquals(1, group.controlDataStreams.length);
        assertEquals(33, group.controlDataStreams[0].pin);
        assertEquals("100", group.controlDataStreams[0].value);

        assertEquals(1, group.viewDataStreams.length);
        assertEquals(34, group.viewDataStreams[0].pin);
        assertEquals("150.0", group.viewDataStreams[0].value);
    }

    @Test
    public void sameTestAsAboveButWithLoadProfile() throws Exception {
        AppWebSocketClient webClient = loggedDefaultClient("super@blynk.cc", "1");

        //create basic product
        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        webClient.createProduct(product);
        ProductDTO fromApiProduct = webClient.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        TestAppClient mobileClient = new TestAppClient("localhost", properties.getHttpsPort());
        mobileClient.start();
        mobileClient.login(getUserName(), "1");
        mobileClient.verifyResult(ok(1));

        //create device tiles widget
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        mobileClient.createWidget(1, deviceTiles);
        mobileClient.verifyResult(ok(2));

        //create tile template for provisioned device
        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);
        mobileClient.createTemplate(1, widgetId, tileTemplate);
        mobileClient.verifyResult(ok(3));

        //create group template
        DataStream viewDataStream = new DataStream((short) 34, PinType.VIRTUAL, AggregationFunctionType.AVG);
        GroupLabel[] groupLabels = new GroupLabel[] {
                new GroupLabel(viewDataStream, null, null, -1, null, -1, -1, -1)
        };
        DataStream switchDataStream = new DataStream((short) 33, PinType.VIRTUAL);
        SwitchWith3LabelsGroupTemplate switchWith3LabelsGroupTemplate = new SwitchWith3LabelsGroupTemplate(
                1, null, "Base Group Template", null, null, null, -1,
                switchDataStream,
                -1,
                groupLabels
        );
        mobileClient.createGroupTemplate(1, widgetId, switchWith3LabelsGroupTemplate);
        mobileClient.verifyResult(ok(4));


        //provision device
        Device newDevice1 = new Device();
        newDevice1.name = "My New Device";
        newDevice1.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice1);
        Device deviceFromApi1 = mobileClient.parseDevice(5);
        assertNotNull(deviceFromApi1);
        assertNotNull(deviceFromApi1.token);
        //finally connecting the hardware1
        TestHardClient newHardClient1 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient1.start();
        newHardClient1.login(deviceFromApi1.token);
        newHardClient1.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi1.id));
        newHardClient1.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient1.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        mobileClient.reset();

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice2);
        Device deviceFromApi2 = mobileClient.parseDevice(1);
        assertNotNull(deviceFromApi2);
        assertNotNull(deviceFromApi2.token);
        //finally connecting the hardware2
        TestHardClient newHardClient2 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient2.start();
        newHardClient2.login(deviceFromApi2.token);
        newHardClient2.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi2.id));
        newHardClient2.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient2.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        mobileClient.reset();

        mobileClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(mobileClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(2, deviceTiles.tiles.length);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);
        assertNotNull(deviceTiles.groupTemplates);
        assertEquals(1, deviceTiles.groupTemplates.length);
        BaseGroupTemplate baseGroupTemplate = deviceTiles.groupTemplates[0];
        assertTrue(baseGroupTemplate instanceof SwitchWith3LabelsGroupTemplate);
        assertEquals(1, baseGroupTemplate.id);
        assertEquals("Base Group Template", baseGroupTemplate.name);

        Group group = new Group(
                10, "My new group", baseGroupTemplate.id,
                new int[] {deviceFromApi1.id, deviceFromApi2.id},
                new DataStream[] {switchDataStream},
                new DataStream[] {viewDataStream}
        );
        mobileClient.createGroup(1, widgetId, group);
        mobileClient.verifyResult(ok(2));


        mobileClient.hardwareForGroup(1, widgetId, group.id, switchDataStream.pin, "100");

        //check both hardware got command
        newHardClient1.verifyResult(hardware(3, "vw 33 100"));
        newHardClient2.verifyResult(hardware(3, "vw 33 100"));

        newHardClient1.send("hardware vw 34 200");
        newHardClient2.send("hardware vw 34 100");

        mobileClient.verifyResult(hardware(3, deviceFromApi1.id + " vw 34 200"));
        mobileClient.verifyResult(hardware(3, deviceFromApi2.id + " vw 34 100"));

        mobileClient.reset();
        mobileClient.loadProfileGzipped(1);
        DashBoard dashBoard = mobileClient.parseDash(1);
        assertNotNull(dashBoard);
        deviceTiles = dashBoard.getWidgetByType(DeviceTiles.class);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.groups);
        assertEquals(1, deviceTiles.groups.length);
        group = deviceTiles.groups[0];
        assertEquals("My new group", group.name);

        assertEquals(1, group.controlDataStreams.length);
        assertEquals(33, group.controlDataStreams[0].pin);
        assertEquals("100", group.controlDataStreams[0].value);

        assertEquals(1, group.viewDataStreams.length);
        assertEquals(34, group.viewDataStreams[0].pin);
        assertEquals("150.0", group.viewDataStreams[0].value);
    }


    @Test
    public void testGroupViewDataStreamsAreUpdatedInRealtime() throws Exception {
        holder.sessionDao.getOrgSession(orgId).closeAll();
        AppWebSocketClient webClient = loggedDefaultClient("super@blynk.cc", "1");

        //create basic product
        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        webClient.createProduct(product);
        ProductDTO fromApiProduct = webClient.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        TestAppClient mobileClient = new TestAppClient("localhost", properties.getHttpsPort());
        mobileClient.start();
        mobileClient.login(getUserName(), "1");
        mobileClient.verifyResult(ok(1));

        //create device tiles widget
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        mobileClient.createWidget(1, deviceTiles);
        mobileClient.verifyResult(ok(2));

        //create tile template for provisioned device
        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);
        mobileClient.createTemplate(1, widgetId, tileTemplate);
        mobileClient.verifyResult(ok(3));

        //create group template
        DataStream viewDataStream = new DataStream((short) 34, PinType.VIRTUAL, AggregationFunctionType.AVG);
        GroupLabel[] groupLabels = new GroupLabel[] {
                new GroupLabel(viewDataStream, null, null, -1, null, -1, -1, -1)
        };
        DataStream switchDataStream = new DataStream((short) 33, PinType.VIRTUAL);
        SwitchWith3LabelsGroupTemplate switchWith3LabelsGroupTemplate = new SwitchWith3LabelsGroupTemplate(
                1, null, "Base Group Template", null, null, null, -1,
                switchDataStream,
                -1,
                groupLabels
        );
        mobileClient.createGroupTemplate(1, widgetId, switchWith3LabelsGroupTemplate);
        mobileClient.verifyResult(ok(4));


        //provision device
        Device newDevice1 = new Device();
        newDevice1.name = "My New Device";
        newDevice1.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice1);
        Device deviceFromApi1 = mobileClient.parseDevice(5);
        assertNotNull(deviceFromApi1);
        assertNotNull(deviceFromApi1.token);
        //finally connecting the hardware1
        TestHardClient newHardClient1 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient1.start();
        newHardClient1.login(deviceFromApi1.token);
        newHardClient1.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi1.id));
        newHardClient1.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient1.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        mobileClient.reset();

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice2);
        Device deviceFromApi2 = mobileClient.parseDevice(1);
        assertNotNull(deviceFromApi2);
        assertNotNull(deviceFromApi2.token);
        //finally connecting the hardware2
        TestHardClient newHardClient2 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient2.start();
        newHardClient2.login(deviceFromApi2.token);
        newHardClient2.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi2.id));
        newHardClient2.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient2.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        mobileClient.reset();

        mobileClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(mobileClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(2, deviceTiles.tiles.length);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);
        assertNotNull(deviceTiles.groupTemplates);
        assertEquals(1, deviceTiles.groupTemplates.length);
        BaseGroupTemplate baseGroupTemplate = deviceTiles.groupTemplates[0];
        assertTrue(baseGroupTemplate instanceof SwitchWith3LabelsGroupTemplate);
        assertEquals(1, baseGroupTemplate.id);
        assertEquals("Base Group Template", baseGroupTemplate.name);

        Group group = new Group(
                10, "My new group", baseGroupTemplate.id,
                new int[] {deviceFromApi1.id, deviceFromApi2.id},
                new DataStream[] {switchDataStream},
                new DataStream[] {viewDataStream}
        );
        mobileClient.createGroup(1, widgetId, group);
        mobileClient.verifyResult(ok(2));

        newHardClient1.send("hardware vw 34 200");
        newHardClient2.send("hardware vw 34 100");

        mobileClient.verifyResult(hardware(3, deviceFromApi1.id + " vw 34 200"));
        mobileClient.verifyResult(hardware(3, deviceFromApi2.id + " vw 34 100"));

        mobileClient.reset();
        mobileClient.loadProfileGzipped(1);
        DashBoard dashBoard = mobileClient.parseDash(1);
        assertNotNull(dashBoard);
        deviceTiles = dashBoard.getWidgetByType(DeviceTiles.class);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.groups);
        assertEquals(1, deviceTiles.groups.length);
        group = deviceTiles.groups[0];
        assertEquals("My new group", group.name);

        assertEquals(1, group.viewDataStreams.length);
        assertEquals(34, group.viewDataStreams[0].pin);
        assertEquals("150.0", group.viewDataStreams[0].value);

        newHardClient2.send("hardware vw 34 400");
        mobileClient.verifyResult(hardware(4, deviceFromApi2.id + " vw 34 400"));

        GroupValueUpdaterWorker groupValueUpdaterWorker = new GroupValueUpdaterWorker(holder.sessionDao, holder.organizationDao);
        groupValueUpdaterWorker.run();
        mobileClient.verifyResult(new StringMessage(1, MOBILE_HARDWARE_GROUP, "1-" + widgetId + "-" + group.id + b(" vw 34 300.0")));
    }

    @Test
    public void testViewWidgetsInsideGroupWork() throws Exception {
        AppWebSocketClient webClient = loggedDefaultClient("super@blynk.cc", "1");

        //create basic product
        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        webClient.createProduct(product);
        ProductDTO fromApiProduct = webClient.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        TestAppClient mobileClient = new TestAppClient("localhost", properties.getHttpsPort());
        mobileClient.start();
        mobileClient.login(getUserName(), "1");
        mobileClient.verifyResult(ok(1));

        //create device tiles widget
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        mobileClient.createWidget(1, deviceTiles);
        mobileClient.verifyResult(ok(2));

        //create tile template for provisioned device
        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);
        mobileClient.createTemplate(1, widgetId, tileTemplate);
        mobileClient.verifyResult(ok(3));

        //create group template
        DataStream viewDataStream = new DataStream((short) 34, PinType.VIRTUAL, AggregationFunctionType.AVG);
        GroupLabel[] groupLabels = new GroupLabel[] {
                new GroupLabel(viewDataStream, null, null, -1, null, -1, -1, -1)
        };
        DataStream switchDataStream = new DataStream((short) 33, PinType.VIRTUAL);
        LabeledValueDisplay label = new LabeledValueDisplay();
        label.id = 132;
        label.x = 2;
        label.y = 2;
        label.pin = 37;
        label.pinType = PinType.VIRTUAL;
        SwitchWith3LabelsGroupTemplate switchWith3LabelsGroupTemplate = new SwitchWith3LabelsGroupTemplate(
                1, new Widget[] {label}, "Base Group Template", null, null, null, -1,
                switchDataStream,
                -1,
                groupLabels
        );
        mobileClient.createGroupTemplate(1, widgetId, switchWith3LabelsGroupTemplate);
        mobileClient.verifyResult(ok(4));


        //provision device
        Device newDevice1 = new Device();
        newDevice1.name = "My New Device";
        newDevice1.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice1);
        Device deviceFromApi1 = mobileClient.parseDevice(5);
        assertNotNull(deviceFromApi1);
        assertNotNull(deviceFromApi1.token);
        //finally connecting the hardware1
        TestHardClient newHardClient1 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient1.start();
        newHardClient1.login(deviceFromApi1.token);
        newHardClient1.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi1.id));
        newHardClient1.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient1.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi1.id));
        mobileClient.reset();

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.boardType = BoardType.ESP32_Dev_Board;
        mobileClient.getProvisionToken(newDevice2);
        Device deviceFromApi2 = mobileClient.parseDevice(1);
        assertNotNull(deviceFromApi2);
        assertNotNull(deviceFromApi2.token);
        //finally connecting the hardware2
        TestHardClient newHardClient2 = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient2.start();
        newHardClient2.login(deviceFromApi2.token);
        newHardClient2.verifyResult(ok(1));
        mobileClient.never(deviceConnected(1, "1-" + deviceFromApi2.id));
        newHardClient2.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient2.verifyResult(ok(2));
        mobileClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        webClient.verifyResult(deviceConnected(2, deviceFromApi2.id));
        mobileClient.reset();

        mobileClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(mobileClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(2, deviceTiles.tiles.length);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);
        assertNotNull(deviceTiles.groupTemplates);
        assertEquals(1, deviceTiles.groupTemplates.length);
        BaseGroupTemplate baseGroupTemplate = deviceTiles.groupTemplates[0];
        assertTrue(baseGroupTemplate instanceof SwitchWith3LabelsGroupTemplate);
        assertEquals(1, baseGroupTemplate.id);
        assertEquals("Base Group Template", baseGroupTemplate.name);

        Group group = new Group(
                10, "My new group", baseGroupTemplate.id,
                new int[] {deviceFromApi1.id, deviceFromApi2.id},
                new DataStream[] {switchDataStream},
                new DataStream[] {viewDataStream}
        );
        mobileClient.createGroup(1, widgetId, group);
        mobileClient.verifyResult(ok(2));

        newHardClient1.send("hardware vw 37 200");
        newHardClient2.send("hardware vw 37 100");

        mobileClient.verifyResult(hardware(3, deviceFromApi1.id + " vw 37 200"));
        mobileClient.verifyResult(hardware(3, deviceFromApi2.id + " vw 37 100"));

        ReportingWorker reportingWorker = new ReportingWorker(holder.reportingDBManager);
        reportingWorker.run();

        mobileClient.reset();
        mobileClient.getGroupWidgetsData(1, widgetId, group.id);
        List<RawEntryWithPin> entries = mobileClient.getGroupWidgetsData();
        assertNotNull(entries);
        assertEquals(1, entries.size());
        RawEntryWithPin entry = entries.get(0);
        assertEquals(150, entry.value, 0.00001);
        assertEquals(37, entry.pin);
        assertEquals(PinType.VIRTUAL, entry.pinType);
    }
}
