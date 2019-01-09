package cc.blynk.integration.web;


import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.upload;
import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class UploadAPITest extends APIBaseTest {

    private static String uploadUrl;

    @BeforeClass
    public static void initUrl() {
        uploadUrl = httpsAdminServerUrl + "/upload";
    }

    @Test
    //todo fix
    public void uploadFileToServerNoAuth() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("admin@blynk.cc", "admin");
        client.getTempSecureToken();
        String token = client.parseToken(1).token;
        String pathToImage = upload(httpclient, uploadUrl, token, "logo.png");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToImage);

        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void uploadFailsNoToken() throws Exception {
        login(admin.email, admin.pass);

        InputStream logoStream = UploadAPITest.class.getResourceAsStream("/" + "logo.png");

        HttpPost post = new HttpPost(httpsAdminServerUrl + "/upload");
        ContentBody fileBody = new InputStreamBody(logoStream, ContentType.APPLICATION_OCTET_STREAM, "logo.png");
        StringBody stringBody1 = new StringBody("123", ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("upfile", fileBody);
        builder.addPart("token", stringBody1);
        HttpEntity entity = builder.build();

        post.setEntity(entity);

        try (CloseableHttpResponse response = httpclient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            String errorMessage = consumeText(response);
            assertEquals("{\"error\":{\"message\":\"No auth token for upload.\"}}", errorMessage);
        }
    }

    @Test
    public void uploadFileToServer() throws Exception {
        login(admin.email, admin.pass);

        AppWebSocketClient client = loggedDefaultClient("admin@blynk.cc", "admin");
        client.getTempSecureToken();
        String token = client.parseToken(1).token;
        String pathToImage = upload(httpclient, uploadUrl, token, "logo.png");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort() + pathToImage);

        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void uploadFileWithSpacesToServer() throws Exception {
        login(admin.email, admin.pass);

        AppWebSocketClient client = loggedDefaultClient("admin@blynk.cc", "admin");
        client.getTempSecureToken();
        String token = client.parseToken(1).token;
        String pathToImage = upload(httpclient, uploadUrl, token, "logo with space in name.png");

        HttpGet index = new HttpGet("https://localhost:" + properties.getHttpsPort()  + pathToImage);

        try (CloseableHttpResponse response = httpclient.execute(index)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void upload5FilesToServer() throws Exception {
        login(admin.email, admin.pass);

        AppWebSocketClient client = loggedDefaultClient("admin@blynk.cc", "admin");
        client.getTempSecureToken();
        String token = client.parseToken(1).token;

        upload(httpclient, uploadUrl, token, "logo.png");
        upload(httpclient, uploadUrl, token, "logo.png");
        upload(httpclient, uploadUrl, token, "logo.png");
        upload(httpclient, uploadUrl, token, "logo.png");
        upload(httpclient, uploadUrl, token, "logo.png");
    }

}
