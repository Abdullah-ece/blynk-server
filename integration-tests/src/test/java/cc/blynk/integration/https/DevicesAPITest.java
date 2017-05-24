package cc.blynk.integration.https;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.utils.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
public class DevicesAPITest extends APIBaseTest {

    @Test
    public void getAllDevices() throws Exception {
        login(admin.email, admin.pass);

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(20, devices.length);

            System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
        }


    }


}
