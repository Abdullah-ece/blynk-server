package cc.blynk.integration.web;


import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.OTADeviceStatus;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.ShipmentProgress;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
import cc.blynk.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.internal;
import static cc.blynk.integration.TestUtil.ok;
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
public class OTATest extends APIBaseTest {

    private BaseServer hardwareServer;

    @Before
    public void init() throws Exception {
        super.init();
        this.hardwareServer = new HardwareAndHttpAPIServer(holder).start();
    }

    @After
    public void shutdownHardwareServer() {
        this.hardwareServer.close();
    }

    @Test
    public void otaBasicFlowForDeviceConnectedAfterOTAStarted() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/Airius_CC3220SF_v081.ota.tar");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"0.8.1\",\"boardType\":\"TI CC3220\",\"buildDate\":\"Dec 13 2018 15:04:29\",\"md5Hash\":\"7AC03C4FECAB96547DBB50350425A204\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new ShipmentDTO(0, 1, newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", firmwareInfo, 5, null, new ShipmentProgress()).toString(),
                ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTADeviceStatus.STARTED, newDevice.deviceOtaInfo.status);
            //todo check within shipment
            //assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(newDevice.token);
        newHardClient.verifyResult(ok(1));

        String firmwareDownloadUrl = "https://localhost:" + properties.getHttpsPort() + pathToFirmware + "?token=";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));

        String s = newHardClient.getBody(2);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        newHardClient.verifyResult(ok(2));
        //newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTADeviceStatus.REQUEST_SENT, newDevice.deviceOtaInfo.status);
        }

        Path tmpFile = Files.createTempFile("123", "test");
        try (CloseableHttpResponse response = httpclient.execute(new HttpGet(firmwareDownloadUrl))) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header header = response.getFirstHeader(StaticFileHandler.MD5_HEADER);
            assertNotNull(header);
            String md5Hash = header.getValue();
            assertEquals(md5Hash, firmwareInfo.md5Hash);
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
        newHardClient.login(newDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "Dec 13 2018 15:04:29");
        newHardClient.verifyResult(ok(2));
        newHardClient.never(internal(7777, "ota\0" + firmwareDownloadUrl));

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertNotNull(newDevice.hardwareInfo);
            assertEquals(OTADeviceStatus.SUCCESS, newDevice.deviceOtaInfo.status);
            assertEquals("Dec 13 2018 15:04:29", newDevice.hardwareInfo.build);
        }
    }


    @Test
    public void otaFullFlowForDeviceConnectedAfterOTAStarted() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/Airius_CC3220SF_v081.ota.tar");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"0.8.1\",\"boardType\":\"TI CC3220\",\"buildDate\":\"Dec 13 2018 15:04:29\",\"md5Hash\":\"7AC03C4FECAB96547DBB50350425A204\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new ShipmentDTO(0, 1, newDevice.productId,
                pathToFirmware, "original name", new int[] {1}, "title", firmwareInfo, 5, null, new ShipmentProgress()).toString(),
                ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTADeviceStatus.STARTED, newDevice.deviceOtaInfo.status);
            //todo check within shipment
            //assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(newDevice.token);
        newHardClient.verifyResult(ok(1));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        String firmwareDownloadUrl = "https://localhost:" + properties.getHttpsPort() + pathToFirmware + "?token=";
        newHardClient.verifyResult(ok(2));

        String s = newHardClient.getBody(2);
        assertTrue(s.startsWith("ota\0" + firmwareDownloadUrl));
        firmwareDownloadUrl = StringUtils.split2(s)[1];
        //newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTADeviceStatus.REQUEST_SENT, newDevice.deviceOtaInfo.status);
        }

        Path tmpFile = Files.createTempFile("123", "test");
        try (CloseableHttpResponse response = httpclient.execute(new HttpGet(firmwareDownloadUrl))) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header header = response.getFirstHeader(StaticFileHandler.MD5_HEADER);
            assertNotNull(header);
            String md5Hash = header.getValue();
            assertEquals(md5Hash, firmwareInfo.md5Hash);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(tmpFile.toFile())) {
                    entity.writeTo(outstream);
                }
            }
        }

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTADeviceStatus.FIRMWARE_UPLOADED, newDevice.deviceOtaInfo.status);
        }

    }

    @Test
    public void otaBasicFlowForDeviceConnectedWhenOTAStarted() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/Airius_CC3220SF_v081.ota.tar");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"0.8.1\",\"boardType\":\"TI CC3220\",\"buildDate\":\"Dec 13 2018 15:04:29\",\"md5Hash\":\"7AC03C4FECAB96547DBB50350425A204\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", properties.getHttpPort());
        newHardClient.start();
        newHardClient.login(newDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new ShipmentDTO(1, 1, newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", firmwareInfo, 5, null, new ShipmentProgress()).toString(),
                ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        String s = newHardClient.getBody(3);
        assertTrue(s.startsWith(b("ota https://localhost:" + properties.getHttpsPort()) + pathToFirmware + "?token="));
        //newHardClient.verifyResult(internal(7777, b("ota http://localhost:" + properties.getHttpPort()) + pathToFirmware + "?token="));

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            //todo check within shipment
            //assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
            assertEquals(OTADeviceStatus.REQUEST_SENT, newDevice.deviceOtaInfo.status);
        }
    }

    @Test
    public void otaStop() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/Airius_CC3220SF_v081.ota.tar");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"0.8.1\",\"boardType\":\"TI CC3220\",\"buildDate\":\"Dec 13 2018 15:04:29\",\"md5Hash\":\"7AC03C4FECAB96547DBB50350425A204\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.NodeMCU;
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new ShipmentDTO(0, 1, newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", firmwareInfo, 5, null, new ShipmentProgress()).toString(),
                ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTADeviceStatus.STARTED, newDevice.deviceOtaInfo.status);
            //todo check within shipment
            //assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            //assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
        }

        post = new HttpPost(httpsAdminServerUrl + "/ota/stop");
        post.setEntity(new StringEntity(new ShipmentDTO(0, 1, newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", firmwareInfo, 5, null, new ShipmentProgress()).toString(),
                ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNull(newDevice.deviceOtaInfo);
        }
    }

    private String upload(String filename) throws Exception {
        InputStream logoStream = OTATest.class.getResourceAsStream("/" + filename);

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/upload");
        ContentBody fileBody = new InputStreamBody(logoStream, ContentType.APPLICATION_OCTET_STREAM, filename);
        StringBody stringBody1 = new StringBody("Message 1", ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        builder.addPart("text1", stringBody1);
        HttpEntity entity = builder.build();

        post.setEntity(entity);

        String staticPath;
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            staticPath = consumeText(response);

            assertNotNull(staticPath);
            assertTrue(staticPath.startsWith("/static"));
            assertTrue(staticPath.endsWith("tar"));
        }

        return staticPath;
    }

    private int createProduct() throws Exception {
        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.logoUrl = "/logoUrl";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "My Default device Name", true),
                createDeviceOwnerMeta(2, "Device Owner", "Device owner", true)
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, new ProductDTO(product)).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi.id;
        }
    }

}
