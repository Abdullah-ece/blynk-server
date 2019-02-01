package cc.blynk.integration.web;


import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
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

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/blnkinf2.0.0.bin");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        client.getFirmwareInfo(pathToFirmware);
        FirmwareInfo parsedFirmwareInfo = client.parseFirmwareInfo(2);
        assertNotNull(parsedFirmwareInfo);
        assertEquals("2.0.0", parsedFirmwareInfo.version);
        assertEquals(BoardType.NodeMCU, parsedFirmwareInfo.boardType);
        assertEquals("May  9 2018 12:36:07", parsedFirmwareInfo.buildDate);
        assertEquals("DA2A7DDC95F46ED14126F5BCEF304833", parsedFirmwareInfo.md5Hash);

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

        OtaDTO otaDTO = new OtaDTO(1, 1, newDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, false);
        client.otaStart(otaDTO);
        Shipment shipment = client.parseOtaProgress(5);
        assertNotNull(shipment);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(6);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceOtaInfo);
        assertEquals(OTADeviceStatus.STARTED, createdDevice.deviceOtaInfo.status);
        assertEquals(getUserName(), createdDevice.deviceOtaInfo.otaStartedBy);
        assertEquals(System.currentTimeMillis(), createdDevice.deviceOtaInfo.otaStartedAt, 5000);
        assertEquals(pathToFirmware, createdDevice.deviceOtaInfo.pathToFirmware);
        assertEquals("May  9 2018 12:36:07", createdDevice.deviceOtaInfo.buildDate);

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice.token);
        newHardClient.verifyResult(ok(1));

        String firmwareDownloadUrl = "http://localhost:" + properties.getHttpPort() + pathToFirmware + "?token=";
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
        assertEquals(pathToFirmware, createdDevice.deviceOtaInfo.pathToFirmware);
        assertEquals("May  9 2018 12:36:07", createdDevice.deviceOtaInfo.buildDate);

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

        newHardClient.stop();

        newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(createdDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "May  9 2018 12:36:07");
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
        assertEquals(pathToFirmware, createdDevice.deviceOtaInfo.pathToFirmware);
        assertEquals("May  9 2018 12:36:07", createdDevice.deviceOtaInfo.buildDate);
        assertEquals("May  9 2018 12:36:07", createdDevice.hardwareInfo.build);
    }

}
