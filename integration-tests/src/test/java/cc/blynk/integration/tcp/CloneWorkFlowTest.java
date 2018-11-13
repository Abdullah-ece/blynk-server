package cc.blynk.integration.tcp;

import cc.blynk.integration.SingleServerInstancePerTestWithDB;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.controls.Slider;
import cc.blynk.utils.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.getDefaultHttpsClient;
import static cc.blynk.integration.TestUtil.serverError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CloneWorkFlowTest extends SingleServerInstancePerTestWithDB {

    @Before
    public void deleteTable() throws Exception {
        holder.dbManager.executeSQL("DELETE FROM cloned_projects");
    }

    @Test
    public void testGetNonExistingQR() throws Exception  {
        clientPair.appClient.send("getProjectByCloneCode " + 123);
        clientPair.appClient.verifyResult(serverError(1));
    }

    @Test
    public void getCloneCode() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        assertNotNull(token);
        assertEquals(32, token.length());
    }

    @Test
    public void getProjectByCloneCode() throws Exception {
        clientPair.hardwareClient.send("hardware vw 4 4");
        clientPair.hardwareClient.send("hardware vw 44 44");

        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody(3);
        assertNotNull(token);
        assertEquals(32, token.length());

        clientPair.appClient.send("getProjectByCloneCode " + token);
        DashBoard dashBoard = clientPair.appClient.parseDash(4);
        assertEquals("My Dashboard", dashBoard.name);
        Device device = new Device(); //dashBoard.devices[0];
        assertEquals(0, device.connectTime);
        assertEquals(0, device.disconnectTime);
        assertEquals(0, device.firstConnectTime);
        assertNull(device.deviceOtaInfo);
        assertNull(device.hardwareInfo);
        Slider slider = (Slider) dashBoard.getWidgetById(4);
        assertNotNull(slider);
        assertNull(slider.value);

        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(5);
        assertEquals(1, profile.dashBoards.length);
    }

    @Test
    public void getProjectByCloneCodeNew() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        assertNotNull(token);
        assertEquals(32, token.length());

        clientPair.appClient.send("getProjectByCloneCode " + token + "\0" + "new");
        DashBoard dashBoard = clientPair.appClient.parseDash(2);
        assertEquals("My Dashboard", dashBoard.name);
        Device device = new Device(); //dashBoard.devices[0];
        assertEquals(-1, dashBoard.parentId);
        assertEquals(2, dashBoard.id);
        assertEquals(0, device.connectTime);
        assertEquals(0, device.disconnectTime);
        assertEquals(0, device.firstConnectTime);
        assertNull(device.deviceOtaInfo);
        assertNull(device.hardwareInfo);
        assertNotNull(device.token);

        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(3);
        assertEquals(2, profile.dashBoards.length);
    }

    @Test
    public void getProjectByCloneCodeNewFormat() throws Exception {
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        assertNotNull(token);
        assertEquals(32, token.length());

        clientPair.appClient.send("getProjectByCloneCode " + token + StringUtils.BODY_SEPARATOR_STRING + "new");
        DashBoard dashBoard = clientPair.appClient.parseDash(2);
        assertEquals("My Dashboard", dashBoard.name);

        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(3);
        assertEquals(2, profile.dashBoards.length);
        assertEquals(2, profile.dashBoards[1].id);
    }

    @Test
    public void getProjectByNonExistingCloneCodeViaHttp() throws Exception {
        CloseableHttpClient httpsClient = getDefaultHttpsClient();

        HttpGet request = new HttpGet("https://localhost:" + properties.getHttpsPort() + "/external/api/" + 123 + "/clone");
        try (CloseableHttpResponse response = httpsClient.execute(request)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }

}
