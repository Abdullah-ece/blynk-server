package cc.blynk.integration.web;


import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.Shipment;
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
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null);
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseOtaProgress(5);
        assertNotNull(shipment);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(6);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), createdDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.otaStartedAt, 5000);

        TestHardClient newHardClient = createHardClient(createdDevice);

        String firmwareDownloadUrl = "https://localhost:" + properties.getHttpsPort() + pathToFirmware + "?token=";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        String s = newHardClient.getBody(2);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(2));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.requestSentAt, 5000);

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
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.requestSentAt, 5000);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.firmwareRequestedAt, 5000);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.firmwareUploadedAt, 5000);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.finishedAt, 5000);
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
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null);
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseOtaProgress(5);
        assertNotNull(shipment);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(6);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), createdDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.otaStartedAt, 5000);

        TestHardClient newHardClient = createHardClient(createdDevice);

        String firmwareDownloadUrl = "https://localhost:" + properties.getHttpsPort() + pathToFirmware + "?token=";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        String s = newHardClient.getBody(2);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(2));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.REQUEST_SENT, createdDevice.deviceOtaInfo.status);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.requestSentAt, 5000);
        assertEquals(-1L, createdDevice.deviceOtaInfo.firmwareRequestedAt);
        assertEquals(-1L, createdDevice.deviceOtaInfo.firmwareUploadedAt);
        assertEquals(-1L, createdDevice.deviceOtaInfo.finishedAt);

        createFile(parsedFirmwareInfo, firmwareDownloadUrl);

        client.reset();
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.FIRMWARE_UPLOADED, createdDevice.deviceOtaInfo.status);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.requestSentAt, 5000);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.firmwareRequestedAt, 5000);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.firmwareUploadedAt, 5000);
        assertEquals(-1L, createdDevice.deviceOtaInfo.finishedAt);
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
        createdDevice = client.parseDevice(5);
        assertNotNull(createdDevice);
        client.reset();

        TestHardClient newHardClient = createHardClient(createdDevice);

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));

        ShipmentDTO shipmentDTO = new ShipmentDTO(2, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null);
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseOtaProgress(2);
        assertNotNull(shipment);

        String s = newHardClient.getBody(3);
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
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.requestSentAt, 5000);
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
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null);
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseOtaProgress(5);
        assertNotNull(shipment);

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(6);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        //todo check within shipment
        //assertEquals(getUserName(), newDevice.deviceOtaInfo.otaStartedBy);
        //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(7);
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

    private FirmwareInfo getFirmwareInfo(AppWebSocketClient client, String pathToFirmware) throws Exception {
        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        client.getFirmwareInfo(pathToFirmware);
        FirmwareInfo parsedFirmwareInfo = client.parseFirmwareInfo(2);
        assertNotNull(parsedFirmwareInfo);
        assertEquals("0.8.1", parsedFirmwareInfo.version);
        assertEquals(BoardType.TI_CC3220, parsedFirmwareInfo.boardType);
        assertEquals("Dec 13 2018 15:04:29", parsedFirmwareInfo.buildDate);
        assertEquals("7AC03C4FECAB96547DBB50350425A204", parsedFirmwareInfo.md5Hash);

        return parsedFirmwareInfo;
    }

    private Device createProductAndDevice(AppWebSocketClient client) throws Exception {
        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Name", null, true),
                createDeviceNameMeta(2, "Device Name", "My Default device Name", false)
        };
        client.createProduct(product);
        ProductDTO productDTO = client.parseProductDTO(3);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = productDTO.id;
        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(4);
        assertNotNull(createdDevice);

        return createdDevice;
    }

    private TestHardClient createHardClient(Device createdDevice) throws Exception {
        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice.token);
        newHardClient.verifyResult(ok(1));

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
}
