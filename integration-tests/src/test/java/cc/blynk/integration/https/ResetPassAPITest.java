package cc.blynk.integration.https;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.utils.JsonParser;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResetPassAPITest extends APIBaseTest {

    @Test
    public void sendResetNotAuthorized() throws Exception {
        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/account/resetPass");

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void sendResetAuthorized() throws Exception {
        login(admin.email, admin.pass);

        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/account/resetPass");

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        verify(mailWrapper).sendHtml(eq(admin.email), eq("Password reset request."), contains(rootPath + "#/resetPass?token="));
    }

    @Test
    public void resetFullFlow() throws Exception {
        login(admin.email, admin.pass);

        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/account/resetPass");

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(mailWrapper, timeout(1000).times(1)).sendHtml(eq(admin.email), eq("Password reset request."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String url = body.substring(body.indexOf("https"), body.length() - 3);

        String token = body.substring(body.indexOf("=") + 1, body.indexOf("&"));
        assertEquals(32, token.length());

        url = url.replace("knight-qa.blynk.cc", "localhost:" + httpsPort);
        assertTrue(url.startsWith("https://localhost:" + httpsPort + rootPath + "#/resetPass?token="));

        verify(mailWrapper).sendHtml(eq(admin.email), eq("Password reset request."), contains(rootPath + "#/resetPass?token="));

        HttpGet inviteGet = new HttpGet(url);

        //we don't need cookie from initial login here
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(initUnsecuredSSLContext(), new MyHostVerifier());
        CloseableHttpClient newHttpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        HttpPost loginRequest = new HttpPost(httpsAdminServerUrl + "/resetPass");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("token", token));
        nvps.add(new BasicNameValuePair("password", "123"));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = newHttpClient.execute(loginRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header cookieHeader = response.getFirstHeader("set-cookie");
            assertNotNull(cookieHeader);
            assertTrue(cookieHeader.getValue().startsWith("session="));
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals(admin.email, user.email);
            assertEquals(admin.name, user.name);
            assertEquals("123", user.pass);
            assertEquals(admin.role, user.role);
            assertEquals(admin.orgId, user.orgId);
        }
    }


}
