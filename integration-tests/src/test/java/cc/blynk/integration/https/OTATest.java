package cc.blynk.integration.https;


import cc.blynk.core.http.handlers.StaticFileHandler;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.api.http.dashboard.dto.StartOtaDTO;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.ota.OTAStatus;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.hardware.HardwareAndHttpAPIServer;
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

import static cc.blynk.integration.IntegrationBase.b;
import static cc.blynk.integration.IntegrationBase.internal;
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
    public void shutdown() {
        super.shutdown();
        this.hardwareServer.close();
    }

    @Test
    public void otaBasicFlowForDeviceConnectedAfterOTAStarted() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/blnkinf2.0.0.bin");

        HttpGet index = new HttpGet("https://localhost:" + httpsPort + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"2.0.0\",\"boardType\":\"NodeMCU\",\"buildDate\":\"May  9 2018 12:36:07\",\"md5Hash\":\"DA2A7DDC95F46ED14126F5BCEF304833\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = "NodeMCU";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new StartOtaDTO(newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", true, firmwareInfo).toString(),
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
            assertEquals(OTAStatus.STARTED, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", httpPort);
        newHardClient.start();
        newHardClient.send("login " + newDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        String firmwareDownloadUrl = "http://localhost:" + httpPort + pathToFirmware + "?token=1";
        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));
        newHardClient.verifyResult(ok(2));

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTAStatus.REQUEST_SENT, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.requestSentAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
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

        newHardClient = new TestHardClient("localhost", httpPort);
        newHardClient.start();
        newHardClient.send("login " + newDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build ") + "May  9 2018 12:36:07");
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
            assertEquals(OTAStatus.SUCCESS, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.requestSentAt, 5000);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.firmwareRequestedAt, 5000);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.firmwareUploadedAt, 5000);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.finishedAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
            assertEquals("May  9 2018 12:36:07", newDevice.hardwareInfo.build);
        }
    }


    @Test
    public void otaFullFlowForDeviceConnectedAfterOTAStarted() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/blnkinf2.0.0.bin");

        HttpGet index = new HttpGet("https://localhost:" + httpsPort + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"2.0.0\",\"boardType\":\"NodeMCU\",\"buildDate\":\"May  9 2018 12:36:07\",\"md5Hash\":\"DA2A7DDC95F46ED14126F5BCEF304833\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = "NodeMCU";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new StartOtaDTO(newDevice.productId,
                pathToFirmware, "original name", new int[] {1}, "title", true, firmwareInfo).toString(),
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
            assertEquals(OTAStatus.STARTED, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", httpPort);
        newHardClient.start();
        newHardClient.send("login " + newDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        String firmwareDownloadUrl = "http://localhost:" + httpPort + pathToFirmware + "?token=1";
        newHardClient.verifyResult(internal(7777, "ota\0" + firmwareDownloadUrl));
        newHardClient.verifyResult(ok(2));

        getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(OTAStatus.REQUEST_SENT, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.requestSentAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
            assertEquals(-1L, newDevice.deviceOtaInfo.firmwareRequestedAt);
            assertEquals(-1L, newDevice.deviceOtaInfo.firmwareUploadedAt);
            assertEquals(-1L, newDevice.deviceOtaInfo.finishedAt);
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
            assertEquals(OTAStatus.FIRMWARE_UPLOADED, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.requestSentAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.firmwareRequestedAt, 5000);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.firmwareUploadedAt, 5000);
            assertEquals(-1L, newDevice.deviceOtaInfo.finishedAt);
        }

    }

    @Test
    public void otaBasicFlowForDeviceConnectedWhenOTAStarted() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/blnkinf2.0.0.bin");

        HttpGet index = new HttpGet("https://localhost:" + httpsPort + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"2.0.0\",\"boardType\":\"NodeMCU\",\"buildDate\":\"May  9 2018 12:36:07\",\"md5Hash\":\"DA2A7DDC95F46ED14126F5BCEF304833\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = "NodeMCU";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
        }

        TestHardClient newHardClient = new TestHardClient("localhost", httpPort);
        newHardClient.start();
        newHardClient.send("login " + newDevice.token);
        verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));

        newHardClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
        newHardClient.verifyResult(ok(2));

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new StartOtaDTO(newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", true, firmwareInfo).toString(),
                ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        newHardClient.verifyResult(internal(7777, b("ota http://localhost:" + httpPort) + pathToFirmware + "?token=1"));

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices/1/1");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
            assertNotNull(newDevice.deviceOtaInfo);
            assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
            assertEquals(OTAStatus.REQUEST_SENT, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.requestSentAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
        }
    }

    @Test
    public void otaFailsAsFirmwareDoesntCorrespondToDeviceBoardType() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/blnkinf2.0.0.bin");

        HttpGet index = new HttpGet("https://localhost:" + httpsPort + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"2.0.0\",\"boardType\":\"NodeMCU\",\"buildDate\":\"May  9 2018 12:36:07\",\"md5Hash\":\"DA2A7DDC95F46ED14126F5BCEF304833\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = "ESP32";//wrong board
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            newDevice = JsonParser.readAny(responseString, Device.class);
            assertNotNull(newDevice);
        }


        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new StartOtaDTO(newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", true, firmwareInfo).toString(),
                ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"My New Device board type doesn't correspond to firmware board type.\"}}", consumeText(response));
        }
    }

    @Test
    public void otaStop() throws Exception {
        login(admin.email, admin.pass);

        String pathToFirmware = upload("static/ota/blnkinf2.0.0.bin");

        HttpGet index = new HttpGet("https://localhost:" + httpsPort + pathToFirmware);
        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/ota/firmwareInfo?file=" + pathToFirmware);

        FirmwareInfo firmwareInfo;
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String firmwareInfoString = consumeText(response);
            assertNotNull(firmwareInfoString);
            assertEquals("{\"version\":\"2.0.0\",\"boardType\":\"NodeMCU\",\"buildDate\":\"May  9 2018 12:36:07\",\"md5Hash\":\"DA2A7DDC95F46ED14126F5BCEF304833\"}", firmwareInfoString);
            firmwareInfo = JsonParser.MAPPER.readValue(firmwareInfoString, FirmwareInfo.class);
        }

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = "NodeMCU";
        newDevice.productId = createProduct();

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices/1");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/ota/start");
        post.setEntity(new StringEntity(new StartOtaDTO(newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", true, firmwareInfo).toString(),
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
            assertEquals(OTAStatus.STARTED, newDevice.deviceOtaInfo.otaStatus);
            assertEquals(admin.email, newDevice.deviceOtaInfo.otaStartedBy);
            assertEquals(System.currentTimeMillis(), newDevice.deviceOtaInfo.otaStartedAt, 5000);
            assertEquals(pathToFirmware, newDevice.deviceOtaInfo.pathToFirmware);
            assertEquals("May  9 2018 12:36:07", newDevice.deviceOtaInfo.buildDate);
        }

        post = new HttpPost(httpsAdminServerUrl + "/ota/stop");
        post.setEntity(new StringEntity(new StartOtaDTO(newDevice.productId, pathToFirmware, "original name", new int[] {1}, "title", true, firmwareInfo).toString(),
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
            assertTrue(staticPath.endsWith("bin"));
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
                new NumberMetaField(1, "Jopa", Role.STAFF, false, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, "My Default device Name")
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi.id;
        }
    }

}
