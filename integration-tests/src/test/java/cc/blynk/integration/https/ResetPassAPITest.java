package cc.blynk.integration.https;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.utils.JsonParser;
import cc.blynk.utils.SHA256Util;
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
    public void sendBadRequest() throws Exception {
        login(admin.email, admin.pass);

        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/sendResetPass");

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }

        verify(mailWrapper, never()).sendHtml(any(), any(), any());
    }

    @Test
    public void resetFullFlowForNotLoggedUser() throws Exception {
        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/sendResetPass");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", regularUser.email));
        resetReq.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(mailWrapper, timeout(1000).times(1)).sendHtml(eq(regularUser.email), eq("Password reset request."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String url = body.substring(body.indexOf("https"), body.length() - 3);

        String token = body.substring(body.indexOf("=") + 1, body.indexOf("&"));
        assertEquals(32, token.length());

        url = url.replace("knight-qa.blynk.cc", "localhost:" + httpsPort);
        assertTrue(url.startsWith("https://localhost:" + httpsPort + rootPath + "#/resetPass?token="));

        verify(mailWrapper).sendHtml(eq(regularUser.email), eq("Password reset request."), contains(rootPath + "#/resetPass?token="));

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

        String hash = SHA256Util.makeHash("123", regularUser.email);

        HttpPost loginRequest = new HttpPost(httpsAdminServerUrl + "/resetPass");
        nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("token", token));
        nvps.add(new BasicNameValuePair("password", hash));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = newHttpClient.execute(loginRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header cookieHeader = response.getFirstHeader("set-cookie");
            assertNotNull(cookieHeader);
            assertTrue(cookieHeader.getValue().startsWith("session="));
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals(regularUser.email, user.email);
            assertEquals(regularUser.name, user.name);
            assertEquals(hash, user.pass);
            assertEquals(regularUser.role, user.role);
            assertEquals(regularUser.orgId, user.orgId);
        }


        login(regularUser.email, hash);
    }

    @Test
    public void resetFullFlow() throws Exception {
        login(admin.email, admin.pass);

        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/sendResetPass");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", admin.email));
        resetReq.setEntity(new UrlEncodedFormEntity(nvps));

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
        nvps = new ArrayList<>();
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
