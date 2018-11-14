package cc.blynk.integration;

import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.Holder;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceNameMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceOwnerMetaField;
import cc.blynk.server.core.model.web.product.metafields.ListMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TemplateIdMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.server.servers.application.MobileAndHttpsServer;
import cc.blynk.utils.SHA256Util;
import cc.blynk.utils.properties.ServerProperties;
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
import java.util.Collections;
import java.util.List;

import static cc.blynk.integration.BaseTest.getRelativeDataFolder;
import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.integration.TestUtil.createDefaultHolder;
import static cc.blynk.integration.TestUtil.createDefaultOrg;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.04.17.
 */
public abstract class APIBaseTest extends CounterBase {

    protected static String rootPath;
    protected static ServerProperties properties;
    protected static int tcpHardPort;
    protected static String httpsAdminServerUrl;

    protected Holder holder;
    protected CloseableHttpClient httpclient;
    protected BaseServer httpsAdminServer;

    protected User admin;
    protected User regularAdmin;
    protected User regularUser;
    public String token;

    @BeforeClass
    public static void initRootPath() {
        rootPath = "/api";
        properties = new ServerProperties(Collections.emptyMap());
        tcpHardPort = properties.getHttpPort();
        httpsAdminServerUrl = String.format("https://localhost:%s" + rootPath, properties.getHttpsPort());
    }

    @Before
    public void init() throws Exception {
        properties.setProperty("data.folder", getDataFolder());
        this.holder = createDefaultHolder(properties, "db-test.properties");
        assertNotNull(holder.dbManager.getConnection());

        this.httpsAdminServer = new MobileAndHttpsServer(holder).start();

        // Allow TLSv1 protocol only
        this.httpclient = getDefaultHttpsClient();

        String name = "admin@blynk.cc";
        String pass = "admin";
        admin = new User(name, SHA256Util.makeHash(pass, name), 1, "local", "127.0.0.1", false, Role.SUPER_ADMIN_ROLE_ID);
        admin.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        admin.status = UserStatus.Active;
        holder.userDao.add(admin);

        name = "admin2@blynk.cc";
        pass = "admin2";
        regularAdmin = new User(name, SHA256Util.makeHash(pass, name), 1, "local", "127.0.0.1", false, 1);
        regularAdmin.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        regularAdmin.status = UserStatus.Active;
        holder.userDao.add(regularAdmin);

        name = "user@blynk.cc";
        pass = "user";
        regularUser = new User(name, SHA256Util.makeHash(pass, name), 1, "local", "127.0.0.1", false, 2);
        regularUser.profile.dashBoards = new DashBoard[] {
                new DashBoard()
        };
        regularUser.status = UserStatus.Active;
        holder.userDao.add(regularUser);

        Organization org = holder.organizationDao.create(
                createDefaultOrg()
        );

        Device device = new Device();
        device.name = "Default Device";
        this.token = holder.deviceDao.createWithPredefinedId(org.id, regularUser.email, device).token;
    }

    @After
    public void shutdown() throws Exception {
        httpsAdminServer.close();
        holder.close();
        httpclient.close();
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

    public static ClientPair initAppAndHardPair() throws Exception {
        return TestUtil.initAppAndHardPair("localhost", properties.getHttpsPort(), properties.getHttpPort(), getUserName(), "1", "user_profile_json.txt", properties, 10000);
    }

    public static MeasurementUnitMetaField createMeasurementMeta(int id, String name, double value, MeasurementUnit measurementUnit) {
        return new MeasurementUnitMetaField(id, name, new int[] {1}, false, false, false, null, measurementUnit, value, 0, 1000, 1);
    }

    public static NumberMetaField createNumberMeta(int id, String name, double value) {
        return createNumberMeta(id, name, value, false);
    }

    public static NumberMetaField createNumberMeta(int id, String name, double value, boolean include) {
        return new NumberMetaField(id, name, new int[] {1}, include, false, false, null, 0, 1000, value, 1);
    }

    public static TextMetaField createTextMeta(int id, String name, String value) {
        return createTextMeta(id, name, value, false);
    }

    public static TextMetaField createTextMeta(int id, String name, String value, boolean include) {
        return new TextMetaField(id, name, new int[] {1}, include, false, false, null, value);
    }

    public static DeviceOwnerMetaField createDeviceOwnerMeta(int id, String name, String value, boolean include) {
        return new DeviceOwnerMetaField(id, name, new int[] {1}, include, false, false, null, value);
    }

    public static DeviceNameMetaField createDeviceNameMeta(int id, String name, String value, boolean include) {
        return new DeviceNameMetaField(id, name, new int[] {1}, include, false, false, null, value);
    }

    public static ListMetaField createListMeta(int id, String name, String templateId) {
        return new ListMetaField(id, name, new int[] {1}, false, false, true, null, new String[] {templateId}, null);
    }

    public static TemplateIdMetaField createTemplateIdMeta(int id, String name, String templateId) {
        return new TemplateIdMetaField(id, name, new int[] {1}, false, false, true, null, new String[] {templateId}, null);
    }

    public static TemplateIdMetaField createTemplateIdMeta(int id, String name, String templateId, boolean includeInProvision) {
        return new TemplateIdMetaField(id, name, new int[] {1}, includeInProvision, false, true, null, new String[] {templateId}, null);
    }

    public static ContactMetaField createContactMeta(int id, String name) {
        return new ContactMetaField(id, name, new int[] {1}, false, false, false, "Tech Support",
                "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                "+38063673333",  false, "My street", false,
                "Ukraine", false,
                "Kyiv", false, "Ukraine", false, "03322", false, false);
    }

    public static ContactMetaField createContactMeta(int id, String name, String icon) {
        return new ContactMetaField(id, name, new int[] {1}, false, false, false, icon,
                "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                "+38063673333",  false, "My street", false,
                "Ukraine", false,
                "Kyiv", false, "Ukraine", false, "03322", false, false);
    }
}
