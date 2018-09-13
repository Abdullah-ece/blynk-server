package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.utils.SHA256Util;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
@Deprecated
public class ResetPassAPITest extends APIBaseTest {

    @Test
    public void sendBadRequest() throws Exception {
        login(admin.email, admin.pass);

        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/sendResetPass");

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }

        verify(holder.mailWrapper, never()).sendHtml(any(), any(), any());
    }

    @Test
    public void resetPassForNonExistingUserShouldReturnOk() throws Exception {
        HttpPost resetReq = new HttpPost(httpsAdminServerUrl + "/sendResetPass");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", "xxx@gmail.com"));
        resetReq.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(resetReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
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
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(regularUser.email), eq("Reset your Blynk Inc. Dashboard password"), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq(regularUser.email), eq("Reset your Blynk Inc. Dashboard password"), contains("/dashboard" + "/resetPass?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/resetPass?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

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
            assertEquals(regularUser.roleId, user.roleId);
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
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(admin.email), eq("Reset your Blynk Inc. Dashboard password"), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq(admin.email), eq("Reset your Blynk Inc. Dashboard password"), contains("/dashboard" + "/resetPass?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/resetPass?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

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
            assertEquals(admin.roleId, user.roleId);
            assertEquals(admin.orgId, user.orgId);
        }
    }


}
