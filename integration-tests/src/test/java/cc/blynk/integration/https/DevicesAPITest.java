package cc.blynk.integration.https;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.utils.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
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
public class DevicesAPITest extends APIBaseTest {

    @Test
    public void createDevice() throws Exception {
        login(regularUser.email, regularUser.pass);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.metaFields = new MetaField[] {
                new NumberMetaField("Jopa", Role.STAFF, 123D)
        };

        HttpPut httpPut = new HttpPut(httpsAdminServerUrl + "/devices");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString);
            assertEquals("My New Device", device.name);
            assertEquals(1, device.id);
            assertNotNull(device.metaFields);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
        }

        httpPut = new HttpPut(httpsAdminServerUrl + "/devices");
        httpPut.setEntity(new StringEntity(newDevice.toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(httpPut)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            assertNotNull(response);
            Device device = JsonParser.parseDevice(responseString);
            assertEquals("My New Device", device.name);
            assertEquals(2, device.id);
            assertNotNull(device.metaFields);
            NumberMetaField numberMetaField = (NumberMetaField) device.metaFields[0];
            assertEquals("Jopa", numberMetaField.name);
            assertEquals(Role.STAFF, numberMetaField.role);
            assertEquals(123D, numberMetaField.value, 0.1);
        }

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(3, devices.length);

            System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
        }
    }

    @Test
    public void getAllDevices() throws Exception {
        login(admin.email, admin.pass);

        HttpGet getDevices = new HttpGet(httpsAdminServerUrl + "/devices");
        try (CloseableHttpResponse response = httpclient.execute(getDevices)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseString = consumeText(response);
            Device[] devices = JsonParser.readAny(responseString, Device[].class);
            assertNotNull(devices);
            assertEquals(1, devices.length);

            System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
        }
    }


}
