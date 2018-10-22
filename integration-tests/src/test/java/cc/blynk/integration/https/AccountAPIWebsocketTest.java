package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.permissions.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountAPIWebsocketTest extends APIBaseTest {

    @Test
    public void getOwnProfileNotAuthorized() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.getAccount();
        while (!appWebSocketClient.isClosed()) {
            sleep(50);
        }
        assertTrue(appWebSocketClient.isClosed());
    }

    @Test
    public void getOwnProfileWorks() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.loggedDefaultClient(regularUser);
        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(1);
        assertNotNull(user);
        assertEquals("user@blynk.cc", user.email);
        assertEquals("user@blynk.cc", user.name);
    }

    @Test
    public void logout() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.loggedDefaultClient(regularUser);
        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(1);
        assertNotNull(user);
        appWebSocketClient.logout();
        appWebSocketClient.verifyResult(ok(2));

        while (!appWebSocketClient.isClosed()) {
            sleep(50);
        }
        assertTrue(appWebSocketClient.isClosed());
    }

    @Test
    public void getOwnProfileReturnOnlySpecificFields() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.loggedDefaultClient(admin);
        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(1);
        assertNotNull(user);
        assertEquals("admin@blynk.cc", user.email);
        assertEquals("admin@blynk.cc", user.name);
        assertEquals(1, user.orgId);
        assertNull(user.pass);
        assertEquals(Role.SUPER_ADMIN_ROLE_ID, user.roleId);
        assertEquals(UserStatus.Active, user.status);
        assertNotNull(user.profile);
        assertEquals(1, user.profile.dashBoards.length);;
    }

    @Test
    public void updateOwnProfileWorks() throws Exception {
        AppWebSocketClient appWebSocketClient = TestUtil.loggedDefaultClient(admin);
        admin.name = "123@123.com";
        appWebSocketClient.updateAccount(admin);
        appWebSocketClient.getAccount();
        User updatedUser = appWebSocketClient.parseAccount(2);
        assertNotNull(updatedUser);
        assertEquals("admin@blynk.cc", updatedUser.email);
        assertEquals( "123@123.com",  updatedUser.name);
    }

}
