package cc.blynk.integration;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.application.AppAndHttpsServer;
import cc.blynk.utils.SHA256Util;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.createDefaultHolder;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.utils.AppNameUtil.BLYNK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.04.17.
 */
public abstract class APIBaseTest extends BaseTest {

    protected static String rootPath;
    protected CloseableHttpClient httpclient;
    protected BaseServer httpsAdminServer;
    protected String httpsAdminServerUrl;

    protected User admin;
    protected User regularAdmin;
    protected User regularUser;

    @BeforeClass
    public static void initRootPath() {
        rootPath = "/api";
    }

    @Before
    public void init() throws Exception {
        this.holder = createDefaultHolder(properties, "db-test.properties");
        assertNotNull(holder.dbManager.getConnection());

        this.httpsAdminServer = new AppAndHttpsServer(holder).start();

        httpsAdminServerUrl = String.format("https://localhost:%s" + rootPath, properties.getHttpsPort());

        // Allow TLSv1 protocol only
        this.httpclient = getDefaultHttpsClient();

        String name = "admin@blynk.cc";
        String pass = "admin";
        admin = new User(name, SHA256Util.makeHash(pass, name), BLYNK, "local", "127.0.0.1", false, Role.SUPER_ADMIN);
        admin.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        admin.status = UserStatus.Active;
        holder.userDao.add(admin);

        name = "admin2@blynk.cc";
        pass = "admin2";
        regularAdmin = new User(name, SHA256Util.makeHash(pass, name), BLYNK, "local", "127.0.0.1", false, Role.ADMIN);
        regularAdmin.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        regularAdmin.status = UserStatus.Active;
        holder.userDao.add(regularAdmin);

        name = "user@blynk.cc";
        pass = "user";
        regularUser = new User(name, SHA256Util.makeHash(pass, name), BLYNK, "local", "127.0.0.1", false, Role.STAFF);
        regularUser.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        regularUser.status = UserStatus.Active;
        holder.userDao.add(regularUser);

        holder.organizationDao.create(new Organization("Blynk Inc.", "Europe/Kiev", "/static/logo.png", true));
    }

    @After
    public void shutdown() {
        httpsAdminServer.close();
    }

    @Override
    public String getDataFolder() {
        return getRelativeDataFolder("/profiles");
    }

    protected void login(String email, String pass) throws Exception {
        login(httpclient, httpsAdminServerUrl, email, pass);
    }

    protected void login(CloseableHttpClient httpclient, String server, String email, String pass) throws Exception {
        HttpPost loginRequest = new HttpPost(server + "/login");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", email));
        nvps.add(new BasicNameValuePair("password", pass));
        loginRequest.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(loginRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Header cookieHeader = response.getFirstHeader("set-cookie");
            assertNotNull(cookieHeader);
            assertTrue(cookieHeader.getValue().startsWith("session="));
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals(email, user.email);
            assertEquals(email, user.name);
            assertNull(user.pass);
        }
    }

}
