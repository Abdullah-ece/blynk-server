package cc.blynk.integration.https;

import cc.blynk.server.core.model.web.Organization;
import cc.blynk.utils.JsonParser;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationAPITest extends APIBaseTest {

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

        Organization organization = new Organization("1", "2", "/static/logo.png");
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

        Organization organization = new Organization("My Org", "Some TimeZone", "/static/logo.png");

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/organization");
        req.setEntity(new StringEntity(organization.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteOrganizationNotAllowedForRegularAdmin() throws Exception {
        holder.organizationDao.add(new Organization("BLynk Inc.", "Europe/Kiev", "/static/logo.png"));

        login(regularAdmin.email, regularAdmin.pass);

        HttpDelete req2 = new HttpDelete(httpsAdminServerUrl + "/organization/2");

        try (CloseableHttpResponse response = httpclient.execute(req2)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void createOrganization() throws Exception {
        login(admin.email, admin.pass);

        Organization organization = new Organization("My Org", "Some TimeZone", "/static/logo.png");

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

        Organization organization = new Organization("1", "2", "/static/logo.png");
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

}
