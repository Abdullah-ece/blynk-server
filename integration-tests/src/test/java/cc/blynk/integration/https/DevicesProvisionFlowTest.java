package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.tcp.TestSslHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.ProvisionType;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.DeviceNameMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceOwnerMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceReferenceMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.TemplateIdMetaField;
import cc.blynk.server.core.model.widgets.outputs.ValueDisplay;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.Tile;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.ButtonTileTemplate;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import cc.blynk.server.core.protocol.enums.Command;
import cc.blynk.server.core.protocol.model.messages.MessageBase;
import cc.blynk.server.core.protocol.model.messages.StringMessage;
import cc.blynk.server.notifications.mail.QrHolder;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.StringUtils;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.APIBaseTest.createMeasurementMeta;
import static cc.blynk.integration.APIBaseTest.createTemplateIdMeta;
import static cc.blynk.integration.TestUtil.appSync;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.deviceConnected;
import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static cc.blynk.integration.TestUtil.updateProductMetafields;
import static cc.blynk.server.core.model.device.BoardType.ESP8266;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.10.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DevicesProvisionFlowTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    @Ignore
    public void connectHardwareWithProvisionToken() throws Exception {
        String provisionToken = "daaaa444763d403ea91bf1a89feda9e3";
        String templateId = "TMPL99744";
        TestHardClient newHardClient = new TestHardClient("airiusfans-qa.blynk.cc", 80);
        newHardClient.start();
        newHardClient.send("login " + provisionToken);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.5.4 tmpl " + templateId + " h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build DOOM"));
        newHardClient.verifyResult(ok(2));

        while (true) {
            sleep(1000);
            newHardClient.send("ping");
        }
    }

    @Test
    public void fullProvisionTest() throws Exception {
        //Step 1. Create new user to be Blynk app project creator
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        int parentId = 0;

        //Step 2. Create minimal project with 1 widget.
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = parentId;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        //Step 2. Create minimal project with 1 widget.
        ValueDisplay valueDisplay = new ValueDisplay();
        valueDisplay.label = "Temperature";
        valueDisplay.id = 1;
        valueDisplay.pin = 1;
        valueDisplay.width = 4;
        valueDisplay.height = 1;
        valueDisplay.pinType = PinType.VIRTUAL;
        appClient.createWidget(parentId, valueDisplay);
        appClient.verifyResult(ok(3));

        DashBoard childDash = new DashBoard();
        childDash.id = 123;
        childDash.name = "Test";
        childDash.parentId = parentId;
        childDash.isPreview = true;
        appClient.createDash(childDash);
        appClient.verifyResult(ok(4));

        appClient.createWidget(childDash.id, valueDisplay);
        appClient.verifyResult(ok(5));

        //Step 3. Create the app
        App app = new App(null, Theme.BlynkLight,
                ProvisionType.DYNAMIC,
                0, false, "My app", null, new int[] {childDash.id});
        appClient.createApp(app);
        App appFromApi = appClient.parseApp(6);
        assertNotNull(appFromApi);
        assertNotNull(appFromApi.id);
        assertTrue(appFromApi.id.startsWith("blynk"));
        appClient.send("emailQr " + childDash.id + StringUtils.BODY_SEPARATOR_STRING + appFromApi.id);
        appClient.verifyResult(ok(7));
        verify(holder.mailWrapper, timeout(1000)).sendWithAttachment(eq(superUser), eq("My app" + " - App details"), eq(holder.textHolder.dynamicMailBody.replace("{project_name}", "Test")), any(QrHolder.class));

        //Step 4. Invite new user
        String invitedUser = "test@gmail.com";
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, invitedUser, "Dmitriy", 3);
        client.verifyResult(ok(1));
        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(invitedUser),
                eq("Invitation to Blynk Inc. dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();
        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());
        verify(holder.mailWrapper).sendHtml(eq(invitedUser),
                eq("Invitation to Blynk Inc. dashboard."), contains("/dashboard/invite?token="));
        String passHash = SHA256Util.makeHash("123", invitedUser);
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(2);
        TestCase.assertNotNull(user);
        assertEquals(invitedUser, user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(3, user.roleId);
        assertEquals(orgId, user.orgId);

        //Step 5. Login via app
        TestAppClient invitedUserAppClient = new TestAppClient(properties);
        invitedUserAppClient.start();
        invitedUserAppClient.login(invitedUser, "123");
        invitedUserAppClient.verifyResult(ok(1));

        invitedUserAppClient.loadProfileGzipped();
        Profile profile = invitedUserAppClient.parseProfile(2);
        assertNotNull(profile);
        assertNotNull(profile.dashBoards);
        assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        assertNotNull(dashBoard);
        ValueDisplay valueDisplayInNewProfile = (ValueDisplay) dashBoard.getWidgetById(valueDisplay.id);
        assertNotNull(valueDisplayInNewProfile);
        assertEquals(parentId, dashBoard.parentId);
        assertEquals(1, dashBoard.id);
        assertTrue(dashBoard.isPreview);
        assertTrue(dashBoard.isActive);
    }

    @Test
    public void fullProvisionTestAndRemoveDevice() throws Exception {
        String superUser = "super@blynk.cc";
        String pass = "1";
        AppWebSocketClient client = loggedDefaultClient(superUser, pass);

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(deviceConnected(2, deviceFromApi.id));
        client.verifyResult(deviceConnected(2, deviceFromApi.id));

        appClient.reset();
        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(1);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(3, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct2.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);
        assertEquals("iconName", provisionedDevice.iconName);
        assertEquals(ESP8266, provisionedDevice.boardType);
        assertEquals("My Default device Name", provisionedDevice.name);

        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(2), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(1, deviceTiles.tiles.length);
        assertEquals(provisionedDevice.id, deviceTiles.tiles[0].deviceId);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);

        //superuser removes the device
        client.reset();
        client.deleteDevice(orgId, deviceFromApi.id);
        client.verifyResult(ok(1));

        appClient.reset();
        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(0, deviceTiles.tiles.length);

        assertTrue(newHardClient.isClosed());
    }

    @Test
    public void fullProvisionTestForSuperAdmin() throws Exception {
        //Step 1. Create new user to be Blynk app project creator
        String superUser = "super@blynk.cc";
        String pass = "1";

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login(superUser, pass);
        appClient.verifyResult(ok(1));

        int parentId = 0;

        //Step 2. Create minimal project with 1 widget.
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = parentId;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        ValueDisplay valueDisplay = new ValueDisplay();
        valueDisplay.label = "Temperature";
        valueDisplay.id = 1;
        valueDisplay.pin = 1;
        valueDisplay.width = 4;
        valueDisplay.height = 1;
        valueDisplay.pinType = PinType.VIRTUAL;
        appClient.createWidget(parentId, valueDisplay);
        appClient.verifyResult(ok(3));

        DashBoard childDash = new DashBoard();
        childDash.id = 123;
        childDash.name = "Test";
        childDash.parentId = parentId;
        childDash.isPreview = true;
        childDash.isActive = true;
        appClient.createDash(childDash);
        appClient.verifyResult(ok(4));

        appClient.createWidget(childDash.id, valueDisplay);
        appClient.verifyResult(ok(5));

        //Step 3. Create the app
        App app = new App(null, Theme.BlynkLight,
                ProvisionType.DYNAMIC,
                0, false, "My app", null, new int[] {childDash.id});
        appClient.createApp(app);
        App appFromApi = appClient.parseApp(6);
        assertNotNull(appFromApi);
        assertNotNull(appFromApi.id);
        assertTrue(appFromApi.id.startsWith("blynk"));
        appClient.send("emailQr " + childDash.id + StringUtils.BODY_SEPARATOR_STRING + appFromApi.id);
        appClient.verifyResult(ok(7));
        verify(holder.mailWrapper, timeout(1000)).sendWithAttachment(eq(superUser), eq("My app" + " - App details"), eq(holder.textHolder.dynamicMailBody.replace("{project_name}", "Test")), any(QrHolder.class));

        //Step 4. No need for invite step as we are already here

        //Step 5. Login via app
        TestAppClient invitedUserAppClient = new TestAppClient(properties);
        invitedUserAppClient.start();
        invitedUserAppClient.login(superUser, pass, "2.27.0", "Android", app.id);
        invitedUserAppClient.verifyResult(ok(1));

        invitedUserAppClient.loadProfileGzipped();
        Profile profile = invitedUserAppClient.parseProfile(2);
        assertNotNull(profile);
        assertNotNull(profile.dashBoards);
        assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        assertNotNull(dashBoard);
        ValueDisplay valueDisplayInNewProfile = (ValueDisplay) dashBoard.getWidgetById(valueDisplay.id);
        assertNotNull(valueDisplayInNewProfile);
        assertEquals(parentId, dashBoard.parentId);
        assertEquals(123, dashBoard.id);
        assertTrue(dashBoard.isPreview);
        assertTrue(dashBoard.isActive);
    }

    @Test
    public void metafieldsForMobileFilteredByIncludeInProvision() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getDevice(createdDevice.id);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(2, metaFields.length);
    }

    @Test
    public void testGetDevicesByReferenceMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                new DeviceReferenceMetaField(2, "Device Ref", new int[] {1}, true, true, true, null, null, -1L),
                createDeviceNameMeta(3, "Device Name", "111", true)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        fromApiProduct = updateProductMetafields(fromApiProduct,
                createDeviceOwnerMeta(1, "Device Name", null, true),
                new DeviceReferenceMetaField(2, "Device Ref", new int[] {1}, true, true, true, null, new int[] {fromApiProduct.id}, -1L),
                createDeviceNameMeta(3, "Device Name", "111", true)
        );

        client.updateProduct(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(2);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getDevice(createdDevice.id, true);
        Device device = appClient.parseDevice(2);
        MetaField[] metaFields = device.metaFields;
        assertNotNull(metaFields);
        assertEquals(3, metaFields.length);
        MetaField metaField = metaFields[1];
        assertTrue(metaField instanceof DeviceReferenceMetaField);

        appClient.getDevicesByReferenceMetafield(createdDevice.id, metaField.id);
        Device[] deviceDTOS = appClient.parseDevices(3);
        assertNotNull(deviceDTOS);
        assertEquals(1, deviceDTOS.length);
        assertEquals("My New Device", deviceDTOS[0].name);
    }

    private static Device getDeviceById(Device[] devices, int id) {
        for (Device device : devices) {
            if (device.id == id) {
                return device;
            }
        }
        return null;
    }

    @Test
    public void testProvisionFlow() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device owner", "test@gmail.com", true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001", true)
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));

        newHardClient.send("ping");
        newHardClient.verifyResult(ok(3));

        appClient.verifyResult(TestUtil.deviceConnected(2, deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id, true);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(2, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct2.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);

        DeviceOwnerMetaField deviceOwnerMetaField = (DeviceOwnerMetaField) provisionedDevice.metaFields[0];
        assertEquals(1, deviceOwnerMetaField.id);
        assertEquals("Device owner", deviceOwnerMetaField.name);
        //expecting email of the user who provisioning the device
        assertEquals(getUserName(), deviceOwnerMetaField.value);

        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) provisionedDevice.metaFields[1];
        assertEquals(2, deviceNameMetaField.id);
        assertEquals("Device Name", deviceNameMetaField.name);
        assertEquals("My Default device Name", deviceNameMetaField.value);

        //should be filtered
        //TemplateIdMetaField templateIdMetaField = (TemplateIdMetaField) provisionedDevice.metaFields[2];
        //assertEquals(3, templateIdMetaField.id);
        //assertEquals("Template Id", templateIdMetaField.name);
        //assertEquals("TMPL0001", templateIdMetaField.options[0]);

        //apps call get devices and not getDevice method
        //so we have to make sure that provisioned device is returned
        appClient.getDevices();
        Device[] allDevices = appClient.parseDevices(5);
        assertNotNull(allDevices);
        provisionedDevice = getDeviceById(allDevices, deviceFromApi.id);
        assertNotNull(provisionedDevice);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(TestUtil.deviceConnected(1, deviceFromApi.id));
    }

    @Test
    public void testProvisionedDeviceIsVisibleForOrgAdmin() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device owner", "test@gmail.com", true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001", true)
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));

        newHardClient.send("ping");
        newHardClient.verifyResult(ok(3));

        appClient.verifyResult(TestUtil.deviceConnected(2, deviceFromApi.id));

        //apps call get devices and not getDevice method
        //so we have to make sure that provisioned device is returned
        appClient.getDevices();
        Device[] allDevices = appClient.parseDevices(4);
        assertNotNull(allDevices);
        assertEquals(2, allDevices.length);
        Device provisionedDevice = getDeviceById(allDevices, deviceFromApi.id);
        assertNotNull(provisionedDevice);

        TestAppClient superAdminClient = new TestAppClient("localhost", properties.getHttpsPort());
        superAdminClient.start();
        superAdminClient.login("super@blynk.cc", "1");
        superAdminClient.verifyResult(ok(1));

        superAdminClient.getDevices(orgId);
        Device[] allDevicesForAdmin = superAdminClient.parseDevices(2);
        assertNotNull(allDevicesForAdmin);
        assertEquals(2, allDevicesForAdmin.length);
        Device provisionedDeviceForAdmin = getDeviceById(allDevicesForAdmin, deviceFromApi.id);
        assertNotNull(provisionedDeviceForAdmin);
    }

    @Test
    public void testProvisionFlowWithDeviceTiles() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createMeasurementMeta(1, "Jopa", 1, MeasurementUnit.Celsius),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false),
                createDeviceOwnerMeta(3, "Device Owner", null, false)
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(deviceConnected(2, deviceFromApi.id));
        client.verifyResult(deviceConnected(2, deviceFromApi.id));

        //here we check, that webapp retrieves WEB_CREATE_DEVICE after device was provisioned.
        String response = null;
        ArgumentCaptor<MessageBase> objectArgumentCaptor = ArgumentCaptor.forClass(StringMessage.class);
        verify(client.responseMock, timeout(1000).times(4)).channelRead(any(), objectArgumentCaptor.capture());
        List<MessageBase> arguments = objectArgumentCaptor.getAllValues();
        for (MessageBase messageBase : arguments) {
            if (messageBase.command == Command.WEB_CREATE_DEVICE) {
                response = ((StringMessage) messageBase).body;
            }
        }
        Device createdDeviceFromTheProvision = JsonParser.parseDevice(response, -1);
        assertNotNull(createdDeviceFromTheProvision);
        assertNotNull(createdDeviceFromTheProvision.metaFields);
        assertEquals(3, createdDeviceFromTheProvision.metaFields.length);
        assertEquals(fromApiProduct2.id, createdDeviceFromTheProvision.productId);
        assertNotNull(createdDeviceFromTheProvision.hardwareInfo);
        assertEquals("TMPL0001", createdDeviceFromTheProvision.hardwareInfo.templateId);
        assertEquals("iconName", createdDeviceFromTheProvision.iconName);
        assertEquals(ESP8266, createdDeviceFromTheProvision.boardType);
        assertEquals("My Default device Name", createdDeviceFromTheProvision.name);

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(6);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(3, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct2.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);
        assertEquals("iconName", provisionedDevice.iconName);
        assertEquals(ESP8266, provisionedDevice.boardType);
        assertEquals("My Default device Name", provisionedDevice.name);

        client.reset();
        //we need separate call here as getDevice for mobile has filtered devices
        client.getDevice(orgId, deviceFromApi.id);
        Device webDevice = client.parseDevice(1);
        MetaField templateIdMeta = webDevice.findMetaFieldById(3);
        assertNotNull(templateIdMeta);
        assertTrue(templateIdMeta instanceof TemplateIdMetaField);
        assertEquals("TMPL0001", ((TemplateIdMetaField) templateIdMeta).selectedOption);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(1000)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(deviceConnected(1, deviceFromApi.id));
        appClient.reset();

        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(1, deviceTiles.tiles.length);
        assertEquals(provisionedDevice.id, deviceTiles.tiles[0].deviceId);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);
    }

    @Test
    public void testProvisionFlowWithDeviceTilesAndLoadGzippedProfile() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createMeasurementMeta(1, "Jopa", 1, MeasurementUnit.Celsius),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false),
                createDeviceOwnerMeta(3, "Device Owner", null, false),
                createTemplateIdMeta(4, "Template Id", "TMPL0001")
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        PageTileTemplate tileTemplate = new PageTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", ESP8266, new DataStream((byte) 1, PinType.VIRTUAL),
                false, null, null, null, -75056000, -231, FontSize.LARGE, false, 2);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(deviceConnected(2, deviceFromApi.id));
        client.verifyResult(deviceConnected(2, deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(6);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(4, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);
        assertEquals("iconName", provisionedDevice.iconName);
        assertEquals(ESP8266, provisionedDevice.boardType);
        assertEquals("My Default device Name", provisionedDevice.name);

        newHardClient.send("hardware vw 1 123");
        appClient.verifyResult(hardware(3, deviceFromApi.id + " vw 1 123"));

        client.reset();
        appClient.reset();

        appClient.loadProfileGzipped();
        Profile profile = appClient.parseProfile(1);
        assertNotNull(profile);
        assertNotNull(profile.dashBoards);
        assertEquals(1, profile.dashBoards.length);
        DashBoard dashBoard = profile.dashBoards[0];

        deviceTiles = dashBoard.getWidgetByType(DeviceTiles.class);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof PageTileTemplate);
        PageTileTemplate pageTileTemplate = (PageTileTemplate) deviceTiles.templates[0];
        assertEquals("name", pageTileTemplate.name);
        assertEquals(1, deviceTiles.tiles.length);
        assertEquals(provisionedDevice.id, deviceTiles.tiles[0].deviceId);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);
        Tile tile = deviceTiles.tiles[0];
        assertEquals(deviceFromApi.id, tile.deviceId);
        assertEquals(tileTemplate.id, tile.templateId);
        assertNotNull(tile.dataStream);
        assertEquals(1, tile.dataStream.pin);
        assertEquals(PinType.VIRTUAL, tile.dataStream.pinType);
        assertEquals("123", tile.dataStream.value);
    }

    @Test
    public void testProvisionFlowWithDeviceTilesAndValuesAreFilledFromServer() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product2";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Owner", getUserName(), true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        client.createProduct(orgId, product);
        ProductDTO fromApiProduct2 = client.parseProductDTO(1);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        long widgetId = 21321;

        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        deviceTiles.color = -231;

        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(ok(3));

        ButtonTileTemplate tileTemplate = new ButtonTileTemplate(1,
                null, null, "TMPL0001", "name", "iconName", BoardType.ESP8266,
                new DataStream((short) 1, PinType.VIRTUAL),
                false, false, false, null, null);

        appClient.createTemplate(1, widgetId, tileTemplate);
        appClient.verifyResult(ok(4));

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(deviceConnected(2, deviceFromApi.id));
        client.verifyResult(deviceConnected(2, deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(6);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(3, provisionedDevice.metaFields.length);
        assertEquals(fromApiProduct2.id, provisionedDevice.productId);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);
        assertEquals("iconName", provisionedDevice.iconName);
        assertEquals(ESP8266, provisionedDevice.boardType);
        assertEquals("My Default device Name", provisionedDevice.name);

        client.reset();
        //we need separate call here as getDevice for mobile has filtered devices
        client.getDevice(orgId, deviceFromApi.id);
        Device webDevice = client.parseDevice(1);
        MetaField templateIdMeta = webDevice.findMetaFieldById(3);
        assertNotNull(templateIdMeta);
        assertTrue(templateIdMeta instanceof TemplateIdMetaField);
        assertEquals("TMPL0001", ((TemplateIdMetaField) templateIdMeta).selectedOption);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(1000)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(deviceConnected(1, deviceFromApi.id));
        appClient.reset();
        client.reset();

        appClient.getWidget(1, widgetId);
        deviceTiles = (DeviceTiles) JsonParser.parseWidget(appClient.getBody(1), 0);
        assertNotNull(deviceTiles);
        assertEquals(widgetId, deviceTiles.id);
        assertNotNull(deviceTiles.templates);
        assertEquals(1, deviceTiles.templates.length);
        assertTrue(deviceTiles.templates[0] instanceof ButtonTileTemplate);
        ButtonTileTemplate buttonTileTemplate = (ButtonTileTemplate) deviceTiles.templates[0];
        assertEquals("name", buttonTileTemplate.name);
        assertEquals(1, deviceTiles.tiles.length);
        assertEquals(provisionedDevice.id, deviceTiles.tiles[0].deviceId);
        assertEquals(tileTemplate.id, deviceTiles.tiles[0].templateId);

        client.track(provisionedDevice.id);
        client.verifyResult(ok(1));

        appClient.send("hardware " + provisionedDevice.id + " vw 1 1");
        client.verifyResult(appSync(2, provisionedDevice.id + " vw 1 1"));
        appClient.loadProfileGzipped();
        Profile userProfile = appClient.parseProfile(2);
        assertNotNull(userProfile);
        DashBoard dashBoard = userProfile.getDashById(1);
        assertNotNull(dashBoard);
        deviceTiles = (DeviceTiles) dashBoard.getWidgetById(widgetId);
        assertNotNull(deviceTiles);
        assertNotNull(deviceTiles.tiles);
        assertNotNull(deviceTiles.tiles[0]);
        assertEquals("1", deviceTiles.tiles[0].dataStream.value);
    }

    @Test
    public void testProvisionFlowNoTemplateIdMetafield() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0002")
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true)
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;
        newDevice.productId = fromApiProduct.id;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(TestUtil.deviceConnected(2, deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        assertNotNull(provisionedDevice.metaFields);

        //id of default product that is first in the list
        assertEquals(0, provisionedDevice.productId);
        assertEquals(2, provisionedDevice.metaFields.length);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertEquals("TMPL0001", provisionedDevice.hardwareInfo.templateId);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(TestUtil.deviceConnected(1, deviceFromApi.id));
    }

    @Test
    public void testProvisionFlowNoTemplateIdInDevice() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-1"));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(TestUtil.deviceConnected(2, deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        //id of default product that is first in the list
        assertEquals(0, provisionedDevice.productId);
        assertNotNull(provisionedDevice.metaFields);
        assertEquals(2, provisionedDevice.metaFields.length);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertNull(provisionedDevice.hardwareInfo.templateId);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(TestUtil.deviceConnected(1, deviceFromApi.id));
    }

    @Test
    public void testProvisionFlowNoTemplateIdInDeviceWithSSLClient() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
        };

        client.createProduct(orgId, product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Product product2 = new Product();
        product2.name = "My product2";
        product2.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001")
        };
        client.createProduct(orgId, product2);
        ProductDTO fromApiProduct2 = client.parseProductDTO(2);
        assertNotNull(fromApiProduct2);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        TestAppClient appClient = new TestAppClient("localhost", properties.getHttpsPort());
        appClient.start();
        appClient.login(getUserName(), "1");
        appClient.verifyResult(ok(1));
        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        TestSslHardClient newHardClient = new TestSslHardClient("localhost", properties.getHttpsPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.never(deviceConnected(1, "1-" + deviceFromApi.id));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));
        appClient.verifyResult(TestUtil.deviceConnected(2, deviceFromApi.id));

        appClient.getDevice(deviceFromApi.id);
        Device provisionedDevice = appClient.parseDevice(4);
        assertNotNull(provisionedDevice);
        //id of default product that is first in the list
        assertEquals(0, provisionedDevice.productId);

        assertNotNull(provisionedDevice.metaFields);
        assertEquals(2, provisionedDevice.metaFields.length);
        assertNotNull(provisionedDevice.hardwareInfo);
        assertNull(provisionedDevice.hardwareInfo.templateId);

        newHardClient.stop();
        appClient.reset();

        newHardClient = new TestSslHardClient("localhost", properties.getHttpsPort());
        newHardClient.start();
        newHardClient.login(deviceFromApi.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
        appClient.verifyResult(TestUtil.deviceConnected(1, deviceFromApi.id));
    }
}
