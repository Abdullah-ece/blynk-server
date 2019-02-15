package cc.blynk.integration.web.reporting;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.ShipmentDeviceStatus;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.Shipment;
import cc.blynk.server.core.model.web.product.ShipmentProgress;
import cc.blynk.server.db.ReportingDBManager;
import cc.blynk.server.workers.ReportingWorker;
import cc.blynk.utils.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.deviceConnected;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.integration.TestUtil.internal;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.otaStatus;
import static cc.blynk.integration.TestUtil.sleep;
import static cc.blynk.integration.TestUtil.upload;
import static cc.blynk.integration.web.OTAWebSocketsTest.checkDeviceStatus;
import static cc.blynk.integration.web.OTAWebSocketsTest.checkShipment;
import static cc.blynk.integration.web.OTAWebSocketsTest.createFile;
import static cc.blynk.integration.web.OTAWebSocketsTest.createHardClient;
import static cc.blynk.integration.web.OTAWebSocketsTest.createProduct;
import static cc.blynk.integration.web.OTAWebSocketsTest.createProductAndDevice;
import static cc.blynk.integration.web.OTAWebSocketsTest.getFirmwareInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 02.13.19.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportingOTAEventsTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    private static CloseableHttpClient httpclient;
    private static String uploadHttpsUrl;

    @BeforeClass
    public static void initClient() throws Exception {
        // Allow TLSv1 protocol only
        httpclient = getDefaultHttpsClient();
        uploadHttpsUrl = String.format("https://localhost:%s" + properties.getUploadPath(), properties.getHttpsPort());
    }

    @Before
    public void clearDB() throws Exception {
        // clickhouse doesn't have normal way of data removal, so using "hack"
        holder.reportingDBManager.executeSQL("ALTER TABLE reporting_shipment_events DELETE where ts > 0");
    }

    @Test
    public void testCollectingOTAEventsStatDuringFullFlowWithReportingWorker() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware, "0.8.1",
                BoardType.TI_CC3220, "Dec 13 2018 15:04:29", "7AC03C4FECAB96547DBB50350425A204",
                properties, httpclient);

        Device createdDevice = createProductAndDevice(client, orgId);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, pathToFirmware, "original name",
                new int[] {createdDevice.id}, "title", parsedFirmwareInfo, 5, null, new ShipmentProgress());
        client.otaStart(shipmentDTO);
        Shipment shipment = client.parseShipment(1);
        checkShipment(shipment, shipmentDTO, 1);

        int orgId = 1;
        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(2);

        assertNotNull(createdDevice);
        assertNotNull(createdDevice.deviceShipmentInfo);
        assertEquals(ShipmentDeviceStatus.STARTED, createdDevice.deviceShipmentInfo.status);

        TestHardClient newHardClient = createHardClient(createdDevice.token, properties);

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
        assertNotNull(createdDevice.deviceShipmentInfo);
        assertEquals(ShipmentDeviceStatus.REQUEST_SENT, createdDevice.deviceShipmentInfo.status);

        createFile(parsedFirmwareInfo, firmwareDownloadUrl, null, null, httpclient);

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
        assertNotNull(createdDevice.deviceShipmentInfo);
        assertNotNull(createdDevice.hardwareInfo);
        assertEquals(ShipmentDeviceStatus.SUCCESS, createdDevice.deviceShipmentInfo.status);
        assertEquals("Dec 13 2018 15:04:29", createdDevice.hardwareInfo.build);

        Map<ShipmentDeviceStatus, Integer> expected = Map.of(
                ShipmentDeviceStatus.STARTED,            1,
                ShipmentDeviceStatus.REQUEST_SENT,       1,
                ShipmentDeviceStatus.FIRMWARE_REQUESTED, 1,
                ShipmentDeviceStatus.FIRMWARE_UPLOADED,  1,
                ShipmentDeviceStatus.SUCCESS,            1
        );

        checkOTAStatsWithWorker(expected, shipment.id, holder.reportingDBManager);
    }

    @Test
    public void testCollectingOTAEventsStatWhileReachingDownloadLimitWithReportingWorker() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        client.getTempSecureToken();
        String uploadToken = client.parseToken(1).token;

        String pathToFirmware = upload(httpclient, uploadHttpsUrl, uploadToken, "static/ota/Airius_CC3220SF_v081.ota.tar");

        FirmwareInfo parsedFirmwareInfo = getFirmwareInfo(client, pathToFirmware, "0.8.1",
                BoardType.TI_CC3220, "Dec 13 2018 15:04:29", "7AC03C4FECAB96547DBB50350425A204",
                properties, httpclient);

        Device createdDevice = createProductAndDevice(client, orgId);

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
        assertNotNull(createdDevice.deviceShipmentInfo);
        assertEquals(ShipmentDeviceStatus.STARTED, createdDevice.deviceShipmentInfo.status);

        TestHardClient newHardClient = createHardClient(createdDevice.token, properties);

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        client.verifyResult(otaStatus(1, shipment.id, createdDevice.id, ShipmentDeviceStatus.DOWNLOAD_LIMIT_REACHED));

        checkDeviceStatus(client, createdDevice.id, orgId, ShipmentDeviceStatus.DOWNLOAD_LIMIT_REACHED);

        Map<ShipmentDeviceStatus, Integer> expected = Map.of(
                ShipmentDeviceStatus.STARTED,                1,
                ShipmentDeviceStatus.DOWNLOAD_LIMIT_REACHED, 1
        );

        checkOTAStatsWithWorker(expected, shipment.id, holder.reportingDBManager);
    }

    @Test
    public void testCollectingOTAEventsStatHavingDeviceFirmwareUploadFailureWithReportingWorker() throws Exception {
        String user = getUserName();
        AppWebSocketClient client = loggedDefaultClient(user, "1");

        ProductDTO productDTO = createProduct(client, null, null);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = productDTO.id;
        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(1);
        assertNotNull(createdDevice);

        ShipmentDTO shipmentDTO = new ShipmentDTO(1, 1, createdDevice.productId, "pathToFirmware", "original name",
                new int[] {createdDevice.id}, "title", null, 1, null, new ShipmentProgress());
        long now = System.currentTimeMillis();
        Shipment shipment = new Shipment(shipmentDTO, user, now);

        createdDevice.startShipment(shipment, 0);
        holder.reportingDBManager.collectEvent(shipment.id, createdDevice);

        Session session = holder.sessionDao.getOrgSession(orgId);
        createdDevice.firmwareUploadFailure(session, 1, shipment);
        holder.reportingDBManager.collectEvent(shipment.id, createdDevice);

        client.verifyResult(otaStatus(1, shipment.id, createdDevice.id, ShipmentDeviceStatus.FAILURE));

        assertNotNull(createdDevice.deviceShipmentInfo);
        assertEquals(ShipmentDeviceStatus.FAILURE, createdDevice.deviceShipmentInfo.status);

        Map<ShipmentDeviceStatus, Integer> expected = Map.of(
                ShipmentDeviceStatus.STARTED, 1,
                ShipmentDeviceStatus.FAILURE, 1
        );

        checkOTAStatsWithWorker(expected, shipment.id, holder.reportingDBManager);
    }

    private void checkOTAStatsWithWorker(Map<ShipmentDeviceStatus, Integer> expected, int shipmentId, ReportingDBManager reportingDBManager) {
        ReportingWorker reportingWorker = new ReportingWorker(reportingDBManager);
        reportingWorker.run();
        sleep(60);

        Map<ShipmentDeviceStatus, Integer> messagesCount = holder.reportingDBManager.reportingOTAStatsDao.selectShipmentStatusMessagesCount(shipmentId);
        assertNotNull(messagesCount);

        for (Map.Entry<ShipmentDeviceStatus, Integer> entry: expected.entrySet()) {
            Integer expectedValue = entry.getValue();
            Integer receivedValue = messagesCount.get(entry.getKey());

            assertEquals(expectedValue, receivedValue);
        }
    }
}
