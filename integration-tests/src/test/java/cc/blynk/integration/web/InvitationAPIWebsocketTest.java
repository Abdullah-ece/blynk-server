package cc.blynk.integration.web;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.auth.UserStatus;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.enums.ProvisionType;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.protocol.enums.Response;
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
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
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
    public void userSendInvitationToExistingUser() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, getUserName(), "Dmitriy", 3);
        client.verifyResult(webJson(1, "User already exists."));
    }

    @Test
    public void canInviteExisting() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.canInviteUser(getUserName());
        client.verifyResult(webJson(1, getUserName() + " already registered in the system."));
    }

    @Test
    public void canInviteExistingPending() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, "test1@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(1));

        client.canInviteUser("test1@gmail.com");
        client.verifyResult(webJson(2, "Invitation for test1@gmail.com was already sent."));
    }

    @Test
    public void testLoginWithoutPasswordSet() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, "test1@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(1));

        TestAppClient appClient = new TestAppClient("localhost", properties);
        appClient.start();
        appClient.loginNoHash("test1@gmail.com", "123");
        appClient.verifyResult(webJson(1,
                "Account is not activated. Please set the password via the invitation link.",
                Response.FACEBOOK_USER_LOGIN_WITH_PASS));
    }

    @Test
    public void sendInvitationFromRegularUser() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.inviteUser(orgId, "test2@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(1));

        verify(holder.mailWrapper).sendHtml(eq("test2@gmail.com"), eq("Invitation to Blynk Inc. dashboard."), contains("/dashboard/invite?token="));
        verify(holder.mailWrapper).sendHtml(eq("test2@gmail.com"), eq("Invitation to Blynk Inc. dashboard."), contains("https://localhost:10443/static/logo.png"));
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
        client.inviteUser(orgId, "test@gmail.com", "Dmitriy", 3);
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
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(2);
        assertNotNull(user);
        assertEquals("test@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(3, user.roleId);
        assertEquals(orgId, user.orgId);

        appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(webJson(1, "Invitation expired or was used already."));

        newHttpClient.close();
    }

    //https://github.com/blynkkk/dash/issues/2010
    @Test
    public void invitationFlowViaResetPassBackdoor() throws Exception {
        //Step 1. Create minimal project with 1 widget.
        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();
        appClient.login("super@blynk.cc", "1");
        appClient.verifyResult(ok(1));
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 0;
        dashBoard.name = "123";
        appClient.createDash(dashBoard);
        appClient.verifyResult(ok(2));

        DashBoard childDash = new DashBoard();
        childDash.id = 123;
        childDash.name = "Test";
        childDash.isPreview = true;
        appClient.createDash(childDash);
        appClient.verifyResult(ok(3));

        App app = new App(null, Theme.BlynkLight,
                ProvisionType.DYNAMIC,
                0, false, "My app", null, new int[] {childDash.id});
        appClient.createApp(app);
        appClient.stop();

        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        String email = "test@gmail.com";
        client.inviteUser(orgId, email, "Dmitriy", 3);
        client.verifyResult(ok(1));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(email),
                eq("Invitation to Blynk Inc. dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq(email),
                eq("Invitation to Blynk Inc. dashboard."), contains("/dashboard/invite?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/invite?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }


        AppWebSocketClient anonym = defaultClient();
        anonym.start();
        anonym.resetPass("start", email, "Blynk");
        anonym.verifyResult(ok(1));

        bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(email), eq("Reset your Blynk Inc. Dashboard password"), bodyArgumentCapture.capture());
        body = bodyArgumentCapture.getValue();

        token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq(email), eq("Reset your Blynk Inc. Dashboard password"), contains("/dashboard" + "/resetPass?token="));

        inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/resetPass?token=" + token);

        //we don't need cookie from initial login here
        newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        client = defaultClient();
        client.start();

        String hash = SHA256Util.makeHash("123", email);
        client.resetPassReset(token, hash);
        client.verifyResult(ok(1));

        client.login(email, "123");
        client.verifyResult(ok(2));
        client.getAccount();
        User user = client.parseAccount(3);
        assertNotNull(user);
        assertEquals(email, user.email);
        assertEquals(UserStatus.Active, user.status);

        User daoUser =  holder.userDao.getByName(email);
        assertNotNull(daoUser.profile.dashBoards);
        assertEquals(1, daoUser.profile.dashBoards.length);
        assertEquals("Test", daoUser.profile.dashBoards[0].name);
    }

    @Test
    public void invitationFullFromSubOrgFlowNoIcon() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg1", "Europe/Kiev", null, true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(2));
        client.inviteUser(subOrgDTO.id, "test3@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(3));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("test3@gmail.com"),
                eq("Invitation to SubOrg1 dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq("test3@gmail.com"), eq("Invitation to SubOrg1 dashboard."), contains("https://localhost:10443/static/logo.png"));
        verify(holder.mailWrapper).sendHtml(eq("test3@gmail.com"),
                eq("Invitation to SubOrg1 dashboard."), contains("/dashboard/invite?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/invite?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        String passHash = SHA256Util.makeHash("123", "test3@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(2);
        assertNotNull(user);
        assertEquals("test3@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(3, user.roleId);
        assertEquals(subOrgDTO.id, user.orgId);

        appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(webJson(1, "Invitation expired or was used already."));

        newHttpClient.close();
    }

    @Test
    public void invitationFullFromSubOrgFlowWithAnotherIcon() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Organization subOrg = new Organization("SubOrg2", "Europe/Kiev", "/static/123.png", true, -1);
        client.createOrganization(subOrg);
        OrganizationDTO subOrgDTO = client.parseOrganizationDTO(1);

        client.trackOrg(subOrgDTO.id);
        client.verifyResult(ok(2));
        client.inviteUser(subOrgDTO.id, "test4@gmail.com", "Dmitriy", 3);
        client.verifyResult(ok(3));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq("test4@gmail.com"),
                eq("Invitation to SubOrg2 dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq("test4@gmail.com"), eq("Invitation to SubOrg2 dashboard."),
                contains("https://localhost:10443/static/123.png"));
        verify(holder.mailWrapper).sendHtml(eq("test4@gmail.com"),
                eq("Invitation to SubOrg2 dashboard."), contains("/dashboard/invite?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/invite?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        String passHash = SHA256Util.makeHash("123", "test4@gmail.com");
        AppWebSocketClient appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(ok(1));

        appWebSocketClient.getAccount();
        User user = appWebSocketClient.parseAccount(2);
        assertNotNull(user);
        assertEquals("test4@gmail.com", user.email);
        assertEquals("Dmitriy", user.name);
        assertEquals(3, user.roleId);
        assertEquals(subOrgDTO.id, user.orgId);

        appWebSocketClient = defaultClient();
        appWebSocketClient.start();
        appWebSocketClient.loginViaInvite(token, passHash);
        appWebSocketClient.verifyResult(webJson(1, "Invitation expired or was used already."));

        newHttpClient.close();
    }
}
