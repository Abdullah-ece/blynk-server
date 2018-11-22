package cc.blynk.integration.https;

import cc.blynk.integration.APIBaseTest;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.sleep;
import static cc.blynk.integration.TestUtil.webJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountAPIWebsocketTest extends APIBaseTest {

    @Test
    public void getOwnProfileNotAuthorized() throws Exception {
        AppWebSocketClient client = defaultClient();
        client.start();
        client.getAccount();
        while (!client.isClosed()) {
            sleep(50);
        }
        assertTrue(client.isClosed());
    }

    @Test
    public void getOwnProfileWorks() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(regularUser);
        client.getAccount();
        User user = client.parseAccount(1);
        assertNotNull(user);
        assertEquals("user@blynk.cc", user.email);
        assertEquals("user@blynk.cc", user.name);
    }

    @Test
    public void logout() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(regularUser);
        client.getAccount();
        User user = client.parseAccount(1);
        assertNotNull(user);
        client.logout();
        client.verifyResult(ok(2));

        while (!client.isClosed()) {
            sleep(50);
        }
        assertTrue(client.isClosed());
    }

    @Test
    public void getOwnProfileReturnOnlySpecificFields() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);
        client.getAccount();
        User user = client.parseAccount(1);
        assertNotNull(user);
        assertEquals("admin@blynk.cc", user.email);
        assertEquals("admin@blynk.cc", user.name);
        assertEquals(1, user.orgId);
        assertNull(user.pass);
        assertEquals(Role.SUPER_ADMIN_ROLE_ID, user.roleId);
        assertEquals(UserStatus.Active, user.status);
        assertNotNull(user.profile);
        assertEquals(0, user.profile.dashBoards.length);
    }

    @Test
    public void updateOwnProfileWorks() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);
        admin.name = "123@123.com";
        client.updateAccount(admin);
        client.getAccount();
        User updatedUser = client.parseAccount(2);
        assertNotNull(updatedUser);
        assertEquals("admin@blynk.cc", updatedUser.email);
        assertEquals( "123@123.com",  updatedUser.name);
    }

    @Test
    public void updateOwnProfileWorksOnlyForName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);

        User user = new User();
        user.name = "123@123.com";
        user.roleId = 100;
        user.email = "1333@123.com";

        client.updateAccount(user);
        client.getAccount();
        User updatedUser = client.parseAccount(2);
        assertNotNull(updatedUser);
        assertEquals("123@123.com",  updatedUser.name);
        assertEquals("admin@blynk.cc", updatedUser.email);
        assertEquals(0, updatedUser.roleId);
    }

    @Test
    public void updateOwnProfileInvalidName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(admin);
        User user = new User();
        user.name = "";

        client.updateAccount(user);
        client.verifyResult(webJson(1, "Account info is not valid."));
    }

    @Test
    public void deleteInvitedUser() throws Exception {
        String invitedUser = "invited@gmail.com";
        AppWebSocketClient client = loggedDefaultClient(admin);
        client.getAccount();
        User user = client.parseAccount(1);

        client.inviteUser(user.orgId, invitedUser, "Dmitriy", 3);
        client.verifyResult(ok(2));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(invitedUser),
                eq("Invitation to Blynk Inc. dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();
        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));

        String passHash = SHA256Util.makeHash("123", invitedUser);
        AppWebSocketClient client2 = defaultClient();
        client2.start();
        client2.loginViaInvite(token, passHash);
        client2.verifyResult(ok(1));

        client2.getAccount();
        User user2 = client2.parseAccount(2);
        assertNotNull(user2);
        assertEquals(invitedUser, user2.email);
        assertEquals("Dmitriy", user2.name);
        assertEquals(3, user2.roleId);
        assertEquals(user.orgId, user2.orgId);

        client.deleteUser(user2.orgId, user2.email);
        client.verifyResult(ok(3));

        while (!client2.isClosed()) {
            sleep(50);
        }
        assertTrue(client2.isClosed());
    }

}
