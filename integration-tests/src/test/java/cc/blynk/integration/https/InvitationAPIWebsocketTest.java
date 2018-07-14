package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.utils.SHA256Util;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.integration.TestUtil.illegalCommand;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class InvitationAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void sendInvitationForNonExistingOrganization() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(1000, "dmitriy@blynk.cc", "Dmitriy", Role.STAFF);
        client.verifyResult(illegalCommand(1));
    }

    @Test
    //todo finish
    public void userCantSendInvitation() throws Exception {
        /*
        String name = "user2@blynk.cc";
        String pass = "user2";
        User simpleUser = new User(name, SHA256Util.makeHash(pass, name), BLYNK, "local", "127.0.0.1", false, Role.USER);
        holder.userDao.add(simpleUser);

        login(simpleUser.email, simpleUser.pass);

        String email = "dmitriy@blynk.cc";
        HttpPost inviteReq = new HttpPost(httpsAdminServerUrl + "/organization/1/invite");
        String data = new UserInviteDTO(email, "Dmitriy", Role.USER).toString();
        inviteReq.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(inviteReq)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"You are not allowed to perform this action.\"}}", consumeText(response));
        }
        */
    }

    @Test
    public void userSendInvitationToExistingUser() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, getUserName(), "Dmitriy", Role.STAFF);
        client.verifyResult(illegalCommand(1));
    }

    @Test
    public void sendInvitationFromRegularUser() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, "test@gmail.com", "Dmitriy", Role.STAFF);
        client.verifyResult(ok(1));

        verify(holder.mailWrapper).sendHtml(eq("test@gmail.com"), eq("Invitation to Blynk Inc. dashboard."), contains("/dashboard/invite?token="));
    }

    @Test
    public void invitationLandingWorks() throws Exception {
        CloseableHttpClient httpclient = getDefaultHttpsClient();
        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/invite?token=123");

        try (CloseableHttpResponse response = httpclient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
        httpclient.close();
    }

    @Test
    public void invitationFullFlow() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, "test@gmail.com", "Dmitriy", Role.STAFF);
        client.verifyResult(ok(1));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("test@gmail.com"),
                eq("Invitation to Blynk Inc. dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq("test@gmail.com"),
                eq("Invitation to Blynk Inc. dashboard."), contains("/dashboard/invite?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/invite?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        String passHash = SHA256Util.makeHash("123", "test@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        User user = appWebSocketClient.parseAccount(1);
        assertNotNull(user);
        assertEquals("test@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(Role.STAFF, user.role);
        assertEquals(orgId, user.orgId);

        appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(illegalCommand(1));

        newHttpClient.close();
    }
}
