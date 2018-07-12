package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.consumeText;
import static cc.blynk.utils.AppNameUtil.BLYNK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountAPITest extends APIBaseTest {

    @Test
    public void getOwnProfileNotAuthorized() throws Exception {
        HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/account");
        try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getOwnProfileWorks() throws Exception {
        login(admin.email, admin.pass);

        HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/account");
        try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals("admin@blynk.cc", user.email);
            assertEquals("admin@blynk.cc", user.name);
        }
    }

    @Test
    public void getOwnProfileReturnOnlySpecificFields() throws Exception {
        login(admin.email, admin.pass);

        HttpGet getOwnProfile = new HttpGet(httpsAdminServerUrl + "/account");
        try (CloseableHttpResponse response = httpclient.execute(getOwnProfile)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals("admin@blynk.cc", user.email);
            assertEquals("admin@blynk.cc", user.name);
            assertEquals(BLYNK, user.appName);
            assertNull(user.pass);
            assertEquals(Role.SUPER_ADMIN, user.role);
            assertEquals(UserStatus.Active, user.status);
            assertNotNull(user.profile);
            assertEquals(1, user.profile.dashBoards.length);
        }
    }

    @Test
    public void updateOwnProfileWorks() throws Exception {
        login(admin.email, admin.pass);

        admin.name = "123@123.com";
        HttpPost updateOwnProfileRequest = new HttpPost(httpsAdminServerUrl + "/account");
        updateOwnProfileRequest.setEntity(new StringEntity(admin.toString(), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = httpclient.execute(updateOwnProfileRequest)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            User user = JsonParser.parseUserFromString(consumeText(response));
            assertNotNull(user);
            assertEquals("admin@blynk.cc", user.email);
            assertEquals( "123@123.com",  user.name);
        }
    }

}
