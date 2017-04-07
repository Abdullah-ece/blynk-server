package cc.blynk.integration.https;

import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.UserInvite;
import cc.blynk.utils.JsonParser;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class InvitationAPITest extends APIBaseTest {

    @Test
    public void sendInvitationNotAuthorized() throws Exception {
        String email = "dmitriy@blynk.cc";
        HttpPost inviteReq = new HttpPost(httpsAdminServerUrl + "/organization/invite");
        List <NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", email));
        nvps.add(new BasicNameValuePair("name", "Dmitriy"));
        nvps.add(new BasicNameValuePair("role", "SUPER_ADMIN"));
        inviteReq.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse response = httpclient.execute(inviteReq)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void sendInvitationForNonExistingOrganization() throws Exception {
        login(admin.email, admin.pass);

        String email = "dmitriy@blynk.cc";
        HttpPost inviteReq = new HttpPost(httpsAdminServerUrl + "/organization/invite");
        String data = JsonParser.mapper.writeValueAsString(new UserInvite(100, email, "Dmitriy", Role.STAFF));
        inviteReq.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(inviteReq)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"Wrong organization id.\"}}", consumeText(response));
        }
    }

    @Test
    public void userCantSendInvitation() throws Exception {
        login(regularUser.email, regularUser.pass);

        String email = "dmitriy@blynk.cc";
        HttpPost inviteReq = new HttpPost(httpsAdminServerUrl + "/organization/invite");
        String data = JsonParser.mapper.writeValueAsString(new UserInvite(1, email, "Dmitriy", Role.STAFF));
        inviteReq.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(inviteReq)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
            assertEquals("{\"error\":{\"message\":\"You are not allowed to perform this action.\"}}", consumeText(response));
        }
    }

    @Test
    public void sendInvitationFromSuperUser() throws Exception {
        login(admin.email, admin.pass);

        String email = "dmitriy@blynk.cc";
        HttpPost inviteReq = new HttpPost(httpsAdminServerUrl + "/organization/invite");
        String data = JsonParser.mapper.writeValueAsString(new UserInvite(1, email, "Dmitriy", Role.STAFF));
        inviteReq.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(inviteReq)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }

        verify(mailWrapper).sendHtml(eq(email), eq("Invitation to Blynk dashboard."), contains("/invite?token="));
    }

}
