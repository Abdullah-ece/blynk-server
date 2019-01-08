package cc.blynk.integration.web;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.model.http.ResponseUserEntity;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.serialization.JsonParser;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.integration.TestUtil.consumeText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginAPITest extends APIBaseTest {

    @Test
    public void testGetNonExistingUser() throws Exception {
        String testUser = "dima@dima.ua";
        HttpPut request = new HttpPut(httpsAdminServerUrl + "/users/" + "xxx/" + testUser);
        request.setEntity(new StringEntity(new ResponseUserEntity("123").toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetWrongUrl() throws Exception {
        String testUser = "dima@dima.ua";
        HttpPut request = new HttpPut(httpsAdminServerUrl + "/urs213213/" + "xxx/" + testUser);
        request.setEntity(new StringEntity(new ResponseUserEntity("123").toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testLoginReturnsBadRequest() throws Exception {
        HttpPost loginRequest = new HttpPost(httpsAdminServerUrl + "/login");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", admin.email));
        nvps.add(new BasicNameValuePair("password", "fake pass"));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }

        loginRequest = new HttpPost(httpsAdminServerUrl + "/login");
        nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", null));
        nvps.add(new BasicNameValuePair("password", null));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }

        loginRequest = new HttpPost(httpsAdminServerUrl + "/login");
        nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", "123"));
        nvps.add(new BasicNameValuePair("password", admin.pass));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void adminLoginFlowSupport()  throws Exception {
        HttpGet loadLoginPageRequest = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard");
        try (CloseableHttpResponse response = httpclient.execute(loadLoginPageRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String loginPage = consumeText(response);
            //todo add full page match?
            assertTrue(loginPage.contains("<div id=\"app\">"));
        }

        login(admin.email, admin.pass);

        HttpGet loadAdminPage = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard");
        try (CloseableHttpResponse response = httpclient.execute(loadAdminPage)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String adminPage = consumeText(response);
            //todo add full page match?
            //assertTrue(adminPage.contains("Blynk Administration"));
            //assertTrue(adminPage.contains("admin.js"));
        }
    }

    @Test
    public void testLogoutAfterLogin()  throws Exception {
        HttpPost loginRequest = new HttpPost(httpsAdminServerUrl + "/login");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", admin.email));
        nvps.add(new BasicNameValuePair("password", admin.pass));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        String sessionId;

        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header cookieHeader = response.getFirstHeader("set-cookie");
            assertNotNull(cookieHeader);
            String[] split = cookieHeader.getValue().split("=|;", 3);
            assertEquals("session", split[0]);
            sessionId = split[1];
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals("admin@blynk.cc", user.email);
            assertEquals("admin@blynk.cc", user.name);
            //assertEquals("84inR6aLx6tZGaQyLrZSEVYCxWW8L88MG+gOn2cncgM=", user.pass);
        }

        HttpPost logoutRequest = new HttpPost(httpsAdminServerUrl + "/logout");
        try (CloseableHttpResponse response = httpclient.execute(logoutRequest)) {
            assertEquals(301, response.getStatusLine().getStatusCode());
            String location = response.getFirstHeader("location").getValue();
            assertEquals("/api", location);
            Header cookieHeader = response.getFirstHeader("set-cookie");
            assertNotNull(cookieHeader);
            String[] split = cookieHeader.getValue().split("=|;", 3);
            assertEquals("session", split[0]);
            assertEquals("", split[1]);
            assertTrue(split[2].contains("Max-Age=0;"));
        }

        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet(httpsAdminServerUrl + "/users/" + testUser + "-" + appName);

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }

        request = new HttpGet(httpsAdminServerUrl + "/users/" + testUser + "-" + appName);

        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("session", sessionId);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        try (CloseableHttpResponse response = httpclient.execute(request, localContext)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetUserFromAdminPageNoAccess() throws Exception {
        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet(httpsAdminServerUrl + "/users/" + testUser + "-" + appName);

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetUserFromAdminPageNoAccessWithFakeCookie() throws Exception {
        String testUser = "dmitriy@blynk.cc";
        String appName = "Blynk";
        HttpGet request = new HttpGet(httpsAdminServerUrl + "/users/" + testUser + "-" + appName);
        request.setHeader("set-cookie", "session=123");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testLoginAndAccessNonExistingResource() throws Exception {
        login(admin.email, admin.pass);
        HttpGet request = new HttpGet(httpsAdminServerUrl.replace(rootPath, "") + "/static/logoxxx.png");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
        }

        HttpGet request2 = new HttpGet(httpsAdminServerUrl.replace(rootPath, "") + "/static/logoxxx.png");

        try (CloseableHttpResponse response = httpclient.execute(request2)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
        }

    }

    @Test
    public void testMakeRootPathRequest() throws Exception {
        HttpGet request = new HttpGet("https://localhost:" + properties.getHttpsPort());

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetFavIconHttps() throws Exception {
        HttpGet request = new HttpGet(httpsAdminServerUrl.replace(rootPath, "") + "/favicon.ico");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getStaticFile() throws Exception {
        HttpGet request = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/static/index.html");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void testGetFavIconHttp() throws Exception {
        HttpGet request = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/favicon.ico");

        try (CloseableHttpResponse response = httpclient.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

}
