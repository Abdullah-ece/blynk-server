package cc.blynk.integration.https;

import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.api.http.dashboard.dto.ProductAndOrgIdDTO;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DevicesWebSocketAPITest extends APIBaseTest {

    @Test
    public void createDevice() throws Exception {
        AppWebSocketClient appWebSocketClient = loggedDefaultClient(regularUser);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = createProduct();

        appWebSocketClient.createDevice(1, newDevice);
        Device createdDevice = appWebSocketClient.parseDevice(1);
        assertNotNull(createdDevice);
        assertEquals("My New Device", createdDevice.name);
        assertEquals(1, createdDevice.id);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        NumberMetaField numberMetaField = (NumberMetaField) createdDevice.metaFields[0];
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(Role.STAFF, numberMetaField.role);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(regularUser.email, createdDevice.activatedBy);

        newDevice.name = "My New Device2";
        appWebSocketClient.createDevice(1, newDevice);
        createdDevice = appWebSocketClient.parseDevice(2);
        assertNotNull(createdDevice);
        assertEquals("My New Device2", createdDevice.name);
        assertEquals(2, createdDevice.id);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        numberMetaField = (NumberMetaField) createdDevice.metaFields[0];
        assertEquals("Jopa", numberMetaField.name);
        assertEquals(Role.STAFF, numberMetaField.role);
        assertEquals(123D, numberMetaField.value, 0.1);
        assertEquals(System.currentTimeMillis(), createdDevice.activatedAt, 5000);
        assertEquals(regularUser.email, createdDevice.activatedBy);

        appWebSocketClient.getDevices(1);
        Device[] devices = appWebSocketClient.parseDevices(3);
        assertNotNull(devices);
        assertEquals(3, devices.length);
        System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(devices));
    }

    //todo change with websocket API
    private int createProduct() throws Exception {
        login(regularUser.email, regularUser.pass);

        Product product = new Product();
        product.name = "My product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.logoUrl = "/logoUrl";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, true, "My Default device Name")
        };

        HttpPut req = new HttpPut(httpsAdminServerUrl + "/product");
        req.setEntity(new StringEntity(new ProductAndOrgIdDTO(1, product).toString(), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            Product fromApi = JsonParser.parseProduct(consumeText(response));
            assertNotNull(fromApi);
            assertEquals(1, fromApi.id);
            return fromApi.id;
        }
    }

}
