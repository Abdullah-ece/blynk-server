package cc.blynk.integration.web;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.auth.User;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
public class ResetPassAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void sendBadRequest() throws Exception {
        AppWebSocketClient client = defaultClient();
        client.start();
        client.resetPass("start");
        client.verifyResult(webJson(1, "Wrong income message format."));
    }

    @Test
    public void resetPassForNonExistingUserShouldNotReturnOk() throws Exception {
        AppWebSocketClient client = defaultClient();
        client.start();
        client.resetPass("start", "xxx@gmail.com", "Blynk");
        client.verifyResult(webJson(1, "User does not exist."));
    }

    @Test
    public void resetFullFlowForNotLoggedUser() throws Exception {
        AppWebSocketClient client = defaultClient();
        client.start();
        client.resetPass("start", getUserName(), "Blynk");
        client.verifyResult(ok(1));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(getUserName()), eq("Reset your Blynk Inc. Dashboard password"), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq(getUserName()), eq("Reset your Blynk Inc. Dashboard password"), contains("/dashboard" + "/resetPass?token="));
        verify(holder.mailWrapper).sendHtml(any(), any(), contains("/static/logo.png"));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/resetPass?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        client = defaultClient();
        client.start();

        String hash = SHA256Util.makeHash("123", getUserName());
        client.resetPassReset(token, hash);
        client.verifyResult(ok(1));

        client.login(getUserName(), "123");
        client.verifyResult(ok(2));
        client.getAccount();
        User user = client.parseAccount(3);
        assertNotNull(user);
        assertEquals(getUserName(), user.email);
        assertEquals(getUserName(), user.name);
        assertEquals(3, user.roleId);
        assertEquals(orgId, user.orgId);
    }

    @Test
    public void resetFullFlowForLoggedUser() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.resetPass("start", getUserName(), "Blynk");
        client.verifyResult(ok(1));

        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(getUserName()), eq("Reset your Blynk Inc. Dashboard password"), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();

        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        assertEquals(32, token.length());

        verify(holder.mailWrapper).sendHtml(eq(getUserName()), eq("Reset your Blynk Inc. Dashboard password"), contains("/dashboard" + "/resetPass?token="));

        HttpGet inviteGet = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/dashboard" + "/resetPass?token=" + token);

        //we don't need cookie from initial login here
        CloseableHttpClient newHttpClient = getDefaultHttpsClient();

        try (CloseableHttpResponse response = newHttpClient.execute(inviteGet)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        client = defaultClient();
        client.start();

        String hash = SHA256Util.makeHash("123", getUserName());
        client.resetPassReset(token, hash);
        client.verifyResult(ok(1));

        client.login(getUserName(), "123");
        client.verifyResult(ok(2));
        client.getAccount();
        User user = client.parseAccount(3);
        assertNotNull(user);
        assertEquals(getUserName(), user.email);
        assertEquals(getUserName(), user.name);
        assertEquals(3, user.roleId);
        assertEquals(orgId, user.orgId);
    }
}
