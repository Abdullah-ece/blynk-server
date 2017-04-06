package cc.blynk.integration.https;

import cc.blynk.integration.BaseTest;
import cc.blynk.server.Holder;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.model.AppName;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.http.HttpsAPIServer;
import cc.blynk.utils.JsonParser;
import cc.blynk.utils.SHA256Util;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationAPITest extends BaseTest {

    private static String rootPath;
    private BaseServer httpsAdminServer;
    private CloseableHttpClient httpclient;
    private String httpsAdminServerUrl;
    private User admin;
    private User regularAdmin;
    private User regularUser;

    @BeforeClass
    public static void initRootPath() {
        rootPath = staticHolder.props.getProperty("admin.rootPath");
    }

    @After
    public void shutdown() {
        httpsAdminServer.close();
    }

    @Before
    public void init() throws Exception {
        Holder holder = new Holder(properties, twitterWrapper, mailWrapper, gcmWrapper, smsWrapper, "db-test.properties");
        assertNotNull(holder.dbManager.getConnection());

        this.httpsAdminServer = new HttpsAPIServer(holder, false).start();

        httpsAdminServerUrl = String.format("https://localhost:%s" + rootPath, httpsPort);

        SSLContext sslcontext = initUnsecuredSSLContext();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new MyHostVerifier());
        this.httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        String name = "admin@blynk.cc";
        String pass = "admin";
        admin = new User(name, SHA256Util.makeHash(pass, name), AppName.BLYNK, "local", false, Role.SUPER_ADMIN);

        name = "admin2@blynk.cc";
        pass = "admin2";
        regularAdmin = new User(name, SHA256Util.makeHash(pass, name), AppName.BLYNK, "local", false, Role.ADMIN);

        name = "user@blynk.cc";
        pass = "user";
        regularUser = new User(name, SHA256Util.makeHash(pass, name), AppName.BLYNK, "local", false, Role.STAFF);

        holder.userDao.add(admin);
        holder.userDao.add(regularAdmin);
        holder.userDao.add(regularUser);

        holder.organizationDao.add(new Organization("BLynk Inc.", "Europe/Kiev"));
    }

    @Override
    public String getDataFolder() {
        return getRelativeDataFolder("/profiles");
    }

    private SSLContext initUnsecuredSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{ tm }, null);

        return context;
    }

    @Test
    public void getOrgNotAuthorized() throws Exception {
        HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/organization");
        try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getOrganization() throws Exception {
        login(admin.email, admin.pass);

        HttpGet req = new HttpGet(httpsAdminServerUrl + "/organization");

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Organization fromApi = JsonParser.parseOrganization(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals("BLynk Inc.", fromApi.name);
            assertEquals("Europe/Kiev", fromApi.tzName);
        }
    }

    @Test
    public void updateOrganizationNotAllowedForRegularUser() throws Exception {
        login(regularUser.email, regularUser.pass);

        Organization organization = new Organization("1", "2");
        organization.id = 1;

        HttpPost req = new HttpPost(httpsAdminServerUrl + "/organization");
        req.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createOrganizationNotAllowedForRegularAdmin() throws Exception {
        login(regularAdmin.email, regularAdmin.pass);

        Organization organization = new Organization("My Org", "Some TimeZone");

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/organization");
        req.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteOrganizationNotAllowedForRegularAdmin() throws Exception {
        holder.organizationDao.add(new Organization("BLynk Inc.", "Europe/Kiev"));

        login(regularAdmin.email, regularAdmin.pass);

        HttpDelete req2 = new HttpDelete(httpsAdminServerUrl + "/organization/2");

        try (CloseableHttpResponse response = httpclient.execute(req2)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createOrganization() throws Exception {
        login(admin.email, admin.pass);

        Organization organization = new Organization("My Org", "Some TimeZone");

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/organization");
        req.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Organization fromApi = JsonParser.parseOrganization(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(2, fromApi.id);
            assertEquals(organization.name, fromApi.name);
            assertEquals(organization.tzName, fromApi.tzName);
        }
    }

    @Test
    public void updateOrganization() throws Exception {
        login(admin.email, admin.pass);

        Organization organization = new Organization("1", "2");
        organization.id = 1;

        HttpPost req = new HttpPost(httpsAdminServerUrl + "/organization");
        req.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Organization fromApi = JsonParser.parseOrganization(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            assertEquals(organization.name, fromApi.name);
            assertEquals(organization.tzName, fromApi.tzName);
        }
    }


    @Test
    public void deleteOrganization() throws Exception {
        createOrganization();

        HttpDelete req = new HttpDelete(httpsAdminServerUrl + "/organization/1");

        //do not allow to delete initial org
        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }

        HttpDelete req2 = new HttpDelete(httpsAdminServerUrl + "/organization/2");

        try (CloseableHttpResponse response = httpclient.execute(req2)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }


    private void login(String name, String pass) throws Exception {
        HttpPost loginRequest = new HttpPost(httpsAdminServerUrl + "/login");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", name));
        nvps.add(new BasicNameValuePair("password", pass));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header cookieHeader = response.getFirstHeader("set-cookie");
            assertNotNull(cookieHeader);
            assertTrue(cookieHeader.getValue().startsWith("session="));
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals(name, user.email);
            assertEquals(name, user.name);
            assertEquals(pass, user.pass);
        }
    }

    private class MyHostVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
