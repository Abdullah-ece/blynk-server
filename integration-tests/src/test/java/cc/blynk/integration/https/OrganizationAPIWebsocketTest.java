package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.web.handlers.logic.organization.LocationDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void getLocationsForProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, false, null, 0, 1000, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, false, true, null, "My Default device Name"),
                new LocationMetaField(3, "Device Location", Role.ADMIN, false, false, null,
                        "Warehouse 13",
                        true, "Baklazhana street 15",
                        false, null,
                        false, null,
                        false, null,
                        false, null,
                        false, false, 0, 0,
                        false, null,
                        false, 0,
                        false, null,
                        false, null,
                        false, null,
                        false, false,
                        null)
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        Device newDevice2 = new Device();
        newDevice2.name = "My New Device 2";
        newDevice2.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice2);
        Device createdDevice2 = client.parseDevice(3);
        assertNotNull(createdDevice2);

        client.getProductLocations(fromApiProduct.id);
        LocationDTO[] locationDTOS = client.parseLocationsDTO(4);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getMetafield(createdDevice.id, locationDTOS[0].id);
        LocationMetaField metaField = (LocationMetaField) client.parseMetafield(5);
        assertNotNull(metaField);
        assertEquals(3, metaField.id);
        assertEquals("Device Location", metaField.name);
        assertEquals("Warehouse 13", metaField.siteName);
        assertTrue(metaField.isLocationEnabled);
        assertEquals("Baklazhana street 15", metaField.streetAddress);
    }

    @Test
    public void getLocationsForProductWithAutoComplete() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                new NumberMetaField(1, "Jopa", Role.STAFF, false, false, null, 0, 1000, 123D),
                new TextMetaField(2, "Device Name", Role.ADMIN, false, true, null, "My Default device Name"),
                new LocationMetaField(3, "Device Location", Role.ADMIN, false, false, null,
                        "Warehouse 13",
                        true, "Baklazhana street 15",
                        false, null,
                        false, null,
                        false, null,
                        false, null,
                        false, false, 0, 0,
                        false, null,
                        false, 0,
                        false, null,
                        false, null,
                        false, null,
                        false, false,
                        null)
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.getProductLocations(fromApiProduct.id, "Ware");
        LocationDTO[] locationDTOS = client.parseLocationsDTO(3);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getProductLocations(fromApiProduct.id, "ware");
        locationDTOS = client.parseLocationsDTO(4);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getProductLocations(fromApiProduct.id, "care");
        locationDTOS = client.parseLocationsDTO(5);
        assertNotNull(locationDTOS);
        assertEquals(0, locationDTOS.length);

        client.getProductLocations(fromApiProduct.id, "13");
        locationDTOS = client.parseLocationsDTO(6);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);

        client.getProductLocations(fromApiProduct.id, "house");
        locationDTOS = client.parseLocationsDTO(7);
        assertNotNull(locationDTOS);
        assertEquals(1, locationDTOS.length);
        assertEquals(createdDevice.id, locationDTOS[0].deviceId);
        assertEquals("Warehouse 13", locationDTOS[0].siteName);
        assertEquals(3, locationDTOS[0].id);
    }

}
