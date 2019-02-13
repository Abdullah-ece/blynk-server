package cc.blynk.integration.web;


import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.DeviceOtaInfo;
import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.Shipment;
import cc.blynk.server.core.model.web.product.ShipmentProgress;
import cc.blynk.server.core.model.web.product.ShipmentStatus;
import cc.blynk.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.deviceConnected;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.integration.TestUtil.internal;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.otaShipmentStatus;
import static cc.blynk.integration.TestUtil.otaStatus;
import static cc.blynk.integration.TestUtil.upload;
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
public class OTAWebSocketsTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    private static CloseableHttpClient httpclient;
    private static String uploadHttpsUrl;

    @BeforeClass
    public static void initClient() throws Exception {
        // Allow TLSv1 protocol only
        httpclient = getDefaultHttpsClient();
        uploadHttpsUrl = String.format("https://localhost:%s" + properties.getUploadPath(), properties.getHttpsPort());
    }

    @Test
    public void otaBasicFlowForDeviceConnectedAfterOTAStarted() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), createdDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.otaStartedAt, 5000);

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        String firmwareDownloadUrl = "https://localhost:" + properties.getHttpsPort() + pathToFirmware + "?token=";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        String s = newHardClient.getBody(1);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(1));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);

        createFile(parsedFirmwareInfo, firmwareDownloadUrl);

        newHardClient.stop();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "Dec 13 2018 15:04:29");
        newHardClient.verifyResult(ok(2));
        newHardClient.never(internal(7777, "ota\0" + firmwareDownloadUrl));
        client.verifyResult(deviceConnected(1, createdDevice.id));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertNotNull(createdDevice.hardwareInfo);
        assertEquals(OTADeviceStatus.SUCCESS, createdDevice.deviceOtaInfo.status);
        assertEquals("Dec 13 2018 15:04:29", createdDevice.hardwareInfo.build);
    }

    @Test
    public void  otaFullFlowForDeviceConnectedAfterOTAStarted() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), createdDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.otaStartedAt, 5000);

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        String firmwareDownloadUrl = "https://localhost:" + properties.getHttpsPort() + pathToFirmware + "?token=";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        String s = newHardClient.getBody(1);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(1));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);

        createFile(parsedFirmwareInfo, firmwareDownloadUrl);

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.FIRMWARE_UPLOADED, createdDevice.deviceOtaInfo.status);
    }

    @Test
    public void otaBasicFlowForDeviceConnectedWhenOTAStarted() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        client.reset();

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(1));

        ShipmentDTO shipmentDTO = new ShipmentDTO(2, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(2);
        checkShipment(shipment, shipmentDTO, 1);

        String s = newHardClient.getBody(2);
        assertTrue(s.startsWith(b("ota https://localhost:" + properties.getHttpsPort()) + pathToFirmware + "?token="));
        //newHardClient.verifyResult(internal(7777, b("ota http://localhost:" + properties.getHttpPort()) + pathToFirmware + "?token="));


        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        //todo check within shipment
        //assertEquals(getUserName(), newDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);
    }

    @Test
    public void otaStop() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, null);
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), newDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        client.reset();

        // ota stop here
        client.otaStop(shipmentDTO);
        client.verifyResult(ok(1));

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);
        assertNull(createdDevice.deviceOtaInfo);
    }

    @Test
    public void testShipment() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);
    }

    @Test
    public void testOTAStatusHavingDeviceSuccess() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        Device createdDevice = createProductAndDevice(client);

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));


        String s = newHardClient.getBody(1);
        String firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(1));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);

        createFile(parsedFirmwareInfo, firmwareDownloadUrl);

        newHardClient.stop();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "Dec 13 2018 15:04:29");
        newHardClient.verifyResult(ok(2));
        newHardClient.never(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.verifyResult(deviceConnected(1, createdDevice.id));
        client.verifyResult(otaStatus(2, shipment.id, createdDevice.id, OTADeviceStatus.SUCCESS));
        client.verifyResult(otaShipmentStatus(0, shipment.id, createdDevice.id, ShipmentStatus.FINISH));

        checkDeviceStatus(client, createdDevice.id, orgId, OTADeviceStatus.SUCCESS);
    }

    @Test
    public void testOTAShipmentStatusHavingSeveralDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        ProductDTO productDTO = createProduct(client);

        Device createdDevice = createDevice(client, productDTO.id, "My New Device");

        Device createdDevice1 = createDevice(client, productDTO.id, "My New Device1");

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id, createdDevice1.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 2);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        String httpPort = "" + properties.getHttpsPort();
        String firmwareDownloadUrl = "https://localhost:" + httpPort + pathToFirmware + "?token=";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        String s = newHardClient.getBody(1);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(1));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);

        createFile(parsedFirmwareInfo, firmwareDownloadUrl);

        newHardClient.stop();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "Dec 13 2018 15:04:29");
        newHardClient.verifyResult(ok(2));
        newHardClient.never(internal(7777, "ota\0" + firmwareDownloadUrl));
        client.verifyResult(deviceConnected(1, createdDevice.id));

        checkDeviceStatus(client, createdDevice.id, orgId, OTADeviceStatus.SUCCESS);


        client.reset();

        parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        newHardClient.reset();
        newHardClient = createHardClient(createdDevice1.token);

        firmwareDownloadUrl = "https://localhost:" + httpPort + pathToFirmware + "?token=";

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        s = newHardClient.getBody(1);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(1));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        createFile(parsedFirmwareInfo, firmwareDownloadUrl);

        newHardClient.stop();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice1.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "Dec 13 2018 15:04:29");
        newHardClient.verifyResult(ok(2));
        newHardClient.never(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.verifyResult(deviceConnected(1, createdDevice1.id), 2);
        client.verifyResult(otaStatus(2, shipment.id, createdDevice1.id, OTADeviceStatus.SUCCESS));
        client.verifyResult(otaShipmentStatus(0, shipment.id, createdDevice1.id, ShipmentStatus.FINISH));

        checkDeviceStatus(client, createdDevice1.id, orgId, OTADeviceStatus.SUCCESS);
    }

    @Test
    public void testOTAStatusHavingDeviceFirmwareDownloadLimitReached() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        // set attemptsLimit to -1 to reach download limit
        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, -1, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), createdDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.otaStartedAt, 5000);

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        client.verifyResult(otaStatus(1, shipment.id, createdDevice.id, OTADeviceStatus.DOWNLOAD_LIMIT_REACHED));

        checkDeviceStatus(client, createdDevice.id, orgId, OTADeviceStatus.DOWNLOAD_LIMIT_REACHED);
    }

    @Test
    public void testOTAStatusHavingDeviceFirmwareUploadFailure() throws Exception {
        String user = getUserName();
        AppWebSocketClient client = loggedDefaultClient(user, "1");

        ProductDTO productDTO = createProduct(client);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = productDTO.id;
        newDevice.setDeviceOtaInfo(new DeviceOtaInfo(1, OTADeviceStatus.STARTED, 1));
        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, "pathToFirmware", "original name",
                new int[] {createdDevice.id}, "title", null, 1, null, new ShipmentProgress());
        long now = System.currentTimeMillis();
        Shipment shipment = new Shipment(shipmentDTO, user, now);

        Session session = holder.sessionDao.getOrgSession(orgId);
        createdDevice.firmwareUploadFailure(session, 1, user, shipment);
        client.verifyResult(otaStatus(1, shipment.id, createdDevice.id, OTADeviceStatus.FAILURE));

        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.FAILURE, createdDevice.deviceOtaInfo.status);
    }

    @Test
    public void testUserFromSameOrgDoesNotReceiveOTAStatusUpdate() throws Exception {
        String user = getUserName();
        AppWebSocketClient client = loggedDefaultClient(user, "1");

        AppWebSocketClient clientFromSameOrg = loggedDefaultClient("super@blynk.cc", "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware);

        Device createdDevice = createProductAndDevice(client);

        // set attemptsLimit to -1 to reach download limit
        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, -1, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);

        TestHardClient newHardClient = createHardClient(createdDevice.token);

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        client.verifyResult(otaStatus(1, shipment.id, createdDevice.id, OTADeviceStatus.DOWNLOAD_LIMIT_REACHED));
        client.verifyResult(otaShipmentStatus(0, shipment.id, createdDevice.id, ShipmentStatus.FINISH));

        checkDeviceStatus(client, createdDevice.id, orgId, OTADeviceStatus.DOWNLOAD_LIMIT_REACHED);

        for (OTADeviceStatus status: OTADeviceStatus.values()) {
            clientFromSameOrg.never(otaStatus(1, shipment.id, createdDevice.id, status));
        }

        for (ShipmentStatus status: ShipmentStatus.values()) {
            clientFromSameOrg.never(otaShipmentStatus(0, shipment.id, createdDevice.id, status));
        }
    }

    // resets client
    private FirmwareInfo getFirmwareInfo(AppWebSocketClient client, String pathToFirmware) throws Exception {
        client.reset();

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        client.getFirmwareInfo(pathToFirmware);
        FirmwareInfo parsedFirmwareInfo = client.parseFirmwareInfo(1);
        assertNotNull(parsedFirmwareInfo);
        assertEquals("0.8.1", parsedFirmwareInfo.version);
        assertEquals(BoardType.TI_CC3220, parsedFirmwareInfo.boardType);
        assertEquals("Dec 13 2018 15:04:29", parsedFirmwareInfo.buildDate);
        assertEquals("7AC03C4FECAB96547DBB50350425A204", parsedFirmwareInfo.md5Hash);

        client.reset();
        return parsedFirmwareInfo;
    }

    // resets client
    private Device createProductAndDevice(AppWebSocketClient client) throws Exception {
        ProductDTO productDTO = createProduct(client);

        Device createdDevice = createDevice(client, productDTO.id, "My New Device");

        client.reset();
        return createdDevice;
    }

    // resets client
    private ProductDTO createProduct(AppWebSocketClient client) throws Exception {
        client.reset();

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false)
        };
        client.createProduct(product);
        ProductDTO productDTO = client.parseProductDTO(1);

        client.reset();

        return productDTO;
    }

    private Device createDevice(AppWebSocketClient client, int productId, String deviceName) throws Exception {
        client.reset();

        Device newDevice = new Device();
        newDevice.name = deviceName;
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = productId;
        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);

        client.reset();
        return createdDevice;
    }

    // resets client
    private TestHardClient createHardClient(String deviceToken) throws Exception {
        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(deviceToken);
        newHardClient.verifyResult(ok(1));

        newHardClient.reset();
        return newHardClient;
    }

    private void createFile(FirmwareInfo parsedFirmwareInfo, String firmwareDownloadUrl) throws Exception {
        //we do request the file, so device status is changed.
        Path tmpFile = Files.createTempFile("123", "test");
        try (CloseableHttpResponse response = httpclient.execute(new HttpGet(firmwareDownloadUrl))) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header header = response.getFirstHeader(StaticFileHandler.MD5_HEADER);
            assertNotNull(header);
            String md5Hash = header.getValue();
            assertEquals(md5Hash, parsedFirmwareInfo.md5Hash);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(tmpFile.toFile())) {
                    entity.writeTo(outstream);
                }
            }
        }
    }

    private void checkShipment(Shipment shipment, ShipmentDTO shipmentDTO, int expectedDeviceCount) {
        assertNotNull(shipment);
        assertEquals(shipmentDTO.productId, shipment.productId);
        assertEquals(shipmentDTO.title, shipment.title);
        assertEquals(shipmentDTO.pathToFirmware, shipment.pathToFirmware);
        assertEquals(shipmentDTO.firmwareOriginalFileName, shipment.firmwareOriginalFileName);
        assertEquals(shipmentDTO.attemptsLimit, shipment.attemptsLimit);

        assertNotNull(shipment.deviceIds);
        assertEquals(shipmentDTO.deviceIds.length, shipment.deviceIds.length);
        assertEquals(expectedDeviceCount, shipment.deviceIds.length);
        assertEquals(shipmentDTO.deviceIds[0], shipment.deviceIds[0]);

        assertNotNull(shipment.firmwareInfo);
        assertEquals(shipmentDTO.firmwareInfo.toString(), shipment.firmwareInfo.toString());
    }

    // resets client
    private void checkDeviceStatus(AppWebSocketClient client, int deviceId, int orgId, OTADeviceStatus status) throws Exception {
        client.reset();

        client.getDevice(orgId, deviceId);
        Device device = client.parseDevice(1);
        assertNotNull(device);
        assertNotNull(device.deviceOtaInfo);
        assertEquals(status, device.deviceOtaInfo.status);

        client.reset();
    }
}
