package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.metafields.AddressMetaField;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.CoordinatesMetaField;
import cc.blynk.server.core.model.web.product.metafields.CostMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.core.model.web.product.metafields.SwitchMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.web.product.metafields.TimeMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Date;

import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.webJson;
import static java.time.LocalTime.ofSecondOfDay;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductAPIWebsocketTest extends SingleServerInstancePerTestWithDBAndNewOrg {

    @Test
    public void getNonExistingProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getProduct(1333);
        client.verifyResult(webJson(1, "Cannot find product with passed id."));
    }

    @Test
    public void getProductById() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "getProductById";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        client.createProduct(orgId, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);

        client.getProduct(fromApi.id);
        fromApi = client.parseProduct(2);
        assertNotNull(fromApi);
    }

    @Test
    public void createProductWithNoName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        client.createProduct(orgId, product);
        client.verifyResult(webJson(1, "Product is empty or has no name."));
    }

    @Test
    public void updateProductWithNoName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "updateProductWithNoName";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        client.createProduct(orgId, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertEquals(product.boardType, fromApi.boardType);
        assertEquals(product.connectionType, fromApi.connectionType);
        assertEquals(0, fromApi.version);

        product.id = 1;
        product.name = "";
        client.updateProduct(orgId, product);
        client.verifyResult(webJson(2, "Product is empty or has no name."));
    }

    @Test
    public void createProductWithMetafields() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductWithMetafields";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My Farm", Role.ADMIN, false, false, false, null, "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", Role.ADMIN, false, false, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, false, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, false, false, null, 0, 1000, 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, false, false, null, MeasurementUnit.Celsius, 36, 0, 100),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, false, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN,  false,false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, false, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, false, false, null, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", Role.ADMIN, false, false, false, null, new Date().getTime()),
                new MeasurementUnitMetaField(10, "None Unit", Role.ADMIN, false, false, false, null, MeasurementUnit.None, 36, 0, 100),
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        client.createProduct(orgId, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertEquals(product.boardType, fromApi.boardType);
        assertEquals(product.connectionType, fromApi.connectionType);
        assertEquals(product.logoUrl, fromApi.logoUrl);
        assertEquals(0, fromApi.version);
        assertNotEquals(0, fromApi.lastModifiedTs);
        assertNotNull(fromApi.dataStreams);
        assertNotNull(fromApi.metaFields);
        assertEquals(11, fromApi.metaFields.length);
    }

    @Test
    public void createProductWithWidgets() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductWithWidgets";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My Farm", Role.ADMIN, false, false, false, null, "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", Role.ADMIN, false, false, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, false, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, false, false, null, 0, 100, 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, false, false, null, MeasurementUnit.Celsius, 36, 0, 100),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, false, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, false, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, false, false, null, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", Role.ADMIN, false, false, false, null, new Date().getTime())
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.x = 1;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.createProduct(orgId, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertEquals(product.boardType, fromApi.boardType);
        assertEquals(product.connectionType, fromApi.connectionType);
        assertEquals(product.logoUrl, fromApi.logoUrl);
        assertEquals(0, fromApi.version);
        assertNotEquals(0, fromApi.lastModifiedTs);
        assertNotNull(fromApi.dataStreams);
        assertNotNull(fromApi.metaFields);
        assertEquals(10, fromApi.metaFields.length);
        assertNotNull(fromApi.webDashboard);
        assertEquals(1, fromApi.webDashboard.widgets.length);
        assertEquals("123", fromApi.webDashboard.widgets[0].label);
        assertEquals(1, fromApi.webDashboard.widgets[0].x);
        assertEquals(2, fromApi.webDashboard.widgets[0].y);
        assertEquals(10, fromApi.webDashboard.widgets[0].height);
        assertEquals(20, fromApi.webDashboard.widgets[0].width);

        product.id = fromApi.id;
        product.description = "Description2";

        webLabel = new WebLabel();
        webLabel.label = "updated";
        webLabel.x = 1;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.updateProduct(orgId, product);
        fromApi = client.parseProduct(2);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertNotNull(fromApi.webDashboard);
        assertEquals(1, fromApi.webDashboard.widgets.length);
        assertEquals("updated", fromApi.webDashboard.widgets[0].label);
        assertEquals(1, fromApi.webDashboard.widgets[0].x);
        assertEquals(2, fromApi.webDashboard.widgets[0].y);
        assertEquals(10, fromApi.webDashboard.widgets[0].height);
        assertEquals(20, fromApi.webDashboard.widgets[0].width);
    }

    @Test
    public void create2ProductsWithSameName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "create2ProductsWithSameName";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        client.createProduct(orgId, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);

        Product product2 = new Product();
        product2.name = "create2ProductsWithSameName";
        product2.description = "Description";
        product2.boardType = "ESP8266";
        product2.connectionType = ConnectionType.WI_FI;
        product2.logoUrl = "/static/logo.png";

        client.createProduct(orgId, product);
        client.verifyResult(webJson(2, "Organization already has product with that name."));
    }

    @Test
    public void createAndUpdateProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createAndUpdateProduct";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        client.createProduct(orgId, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertEquals(product.boardType, fromApi.boardType);
        assertEquals(product.connectionType, fromApi.connectionType);
        assertEquals(0, fromApi.version);

        product.id = fromApi.id;
        product.name = "Updated Name";
        product.description = "Description2";

        client.updateProduct(orgId, product);
        fromApi = client.parseProduct(2);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertEquals("Updated Name", fromApi.name);
        assertEquals("Description2", fromApi.description);
    }

    @Test
    public void getEmptyListOfProducts() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getProducts();
        Product[] fromApi = client.parseProducts(1);
        assertNotNull(fromApi);
        assertEquals(0, fromApi.length);
    }

    @Test
    public void getListOfProducts() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "getListOfProducts";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        client.getProducts();
        Product[] fromApiProducts = client.parseProducts(2);
        assertNotNull(fromApiProducts);
        assertEquals(1, fromApiProducts.length);

        Product product2 = new Product();
        product2.name = "getListOfProducts2";

        client.createProduct(orgId, product2);
        fromApiProduct = client.parseProduct(3);
        assertNotNull(fromApiProduct);

        client.getProducts();
        fromApiProducts = client.parseProducts(4);
        assertNotNull(fromApiProducts);
        assertEquals(2, fromApiProducts.length);
    }

    @Test
    public void createProductAndDelete() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductAndDelete";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        client.getProducts();
        Product[] fromApiProducts = client.parseProducts(2);
        assertNotNull(fromApiProducts);
        assertEquals(1, fromApiProducts.length);

        client.deleteProduct(fromApiProduct.id);
        client.verifyResult(ok(3));

        client.getProducts();
        fromApiProducts = client.parseProducts(4);
        assertNotNull(fromApiProducts);
        assertEquals(0, fromApiProducts.length);
    }

    @Test
    public void cantDeleteProductWithDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductAndDelete";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.deleteProduct(fromApiProduct.id);
        client.verifyResult(webJson(3, "You are not allowed to remove product with devices."));
    }

    @Test
    public void testAddMetaDataFieldInChildDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My test metafield", Role.ADMIN, false, false, false, null, "Default Device")
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(1, newDevice.metaFields.length);
        TextMetaField textMetaField = (TextMetaField) newDevice.metaFields[0];
        assertEquals(1, textMetaField.id);
        assertEquals("My test metafield", textMetaField.name);
        assertEquals(Role.ADMIN, textMetaField.role);
        assertEquals("Default Device", textMetaField.value);

        newDevice.metaFields[0] = new TextMetaField(textMetaField.id,
                textMetaField.name, textMetaField.role, false, false, false, null, "My updated value");

        client.updateDevice(orgId, newDevice);
        newDevice = client.parseDevice(3);
        assertNotNull(newDevice);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(1, newDevice.metaFields.length);
        textMetaField = (TextMetaField) newDevice.metaFields[0];
        assertEquals(1, textMetaField.id);
        assertEquals("My test metafield", textMetaField.name);
        assertEquals(Role.ADMIN, textMetaField.role);
        assertEquals("My updated value", textMetaField.value);

        fromApiProduct.metaFields = new MetaField[] {
                product.metaFields[0],
                new NumberMetaField(2, "New metafield", Role.ADMIN, false, false, false, null, 0, 100, 123)
        };

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProduct(4);
        assertEquals(2, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        newDevice = client.parseDevice(5);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(2, newDevice.metaFields.length);
        textMetaField = (TextMetaField) newDevice.metaFields[0];
        assertEquals(1, textMetaField.id);
        assertEquals("My test metafield", textMetaField.name);
        assertEquals(Role.ADMIN, textMetaField.role);
        assertEquals("My updated value", textMetaField.value);

        NumberMetaField numberMetaField = (NumberMetaField) newDevice.metaFields[1];
        assertEquals(2, numberMetaField.id);
        assertEquals("New metafield", numberMetaField.name);
        assertEquals(Role.ADMIN, numberMetaField.role);
        assertEquals(123, numberMetaField.value, 0.1);
    }

    @Test
    public void testUpdateMetaDataFieldInChildDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new TextMetaField(1, "My test metafield", Role.ADMIN, false, false, false, null, "Default Device")
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);

        fromApiProduct.metaFields[0] = new TextMetaField(1,
                "Me updated test metafield", Role.USER, false, false, false, null, "Default Device");

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProduct(3);
        assertNotNull(fromApiProduct);
        assertEquals(1, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        Device device = client.parseDevice(4);
        assertNotNull(newDevice);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(1, device.metaFields.length);
        TextMetaField textMetaField = (TextMetaField) device.metaFields[0];
        assertEquals(1, textMetaField.id);
        assertEquals("Me updated test metafield", textMetaField.name);
        assertEquals(Role.USER, textMetaField.role);
        assertEquals("Default Device", textMetaField.value);
    }

    @Test
    public void testUpdateContactMetaDataFieldInChildDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                new ContactMetaField(1, "Farm of Smith", Role.ADMIN, false, false, false, "Tech Support",
                        "Dmitriy", true, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false, "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false)
        };

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(1, newDevice.metaFields.length);
        ContactMetaField contactMetaField = (ContactMetaField) newDevice.metaFields[0];
        assertEquals(1, contactMetaField.id);
        assertEquals("Farm of Smith", contactMetaField.name);
        assertEquals(Role.ADMIN, contactMetaField.role);
        assertTrue(contactMetaField.isFirstNameEnabled);
        assertFalse(contactMetaField.isLastNameEnabled);

        fromApiProduct.metaFields[0] = new ContactMetaField(1, "Farm of Smith", Role.ADMIN, false, false, false, "Tech Support",
                "Dmitriy", true, "Dumanskiy", true, "dmitriy@blynk.cc", false,
                "+38063673333",  false, "My street", false, "Ukraine", false,
                "Kyiv", false, "Ukraine", false, "03322", false, false);

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProduct(3);
        assertNotNull(fromApiProduct);
        assertEquals(1, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        Device device = client.parseDevice(4);
        assertNotNull(newDevice);
        assertNotNull(device.metaFields);
        assertEquals(1, device.metaFields.length);
        contactMetaField = (ContactMetaField) device.metaFields[0];
        assertTrue(contactMetaField.isFirstNameEnabled);
        assertTrue(contactMetaField.isLastNameEnabled);
    }

    @Test
    @Ignore
    //todo
    public void createProductAndDeleteRegularUserCantDelete() throws Exception {

    }

    @Test
    public void canDeleteProductRequest() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        client.canDeleteProduct(fromApiProduct.id);
        client.verifyResult(ok(2));

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(3);
        assertNotNull(newDevice);

        client.canDeleteProduct(fromApiProduct.id);
        client.verifyResult(webJson(4, "You can't delete product with devices."));
    }

    @Test
    public void createProductForSubOrgAndCannotUpdateItDirectly() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("Sub Org", "Some TimeZone", "/static/logo.png", false, orgId);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        Organization fromApiOrg = client.parseOrganization(2);
        assertNotNull(fromApiOrg);
        assertEquals(orgId, fromApiOrg.parentId);
        assertEquals(organization.name, fromApiOrg.name);
        assertEquals(organization.tzName, fromApiOrg.tzName);
        assertNotNull(fromApiOrg.products);
        assertEquals(1, fromApiOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiOrg.products[0].parentId);

        client.updateProduct(fromApiOrg.id, fromApiOrg.products[0]);
        client.verifyResult(webJson(3, "Sub Org can't do anything with the Product Templates created by Meta Org."));

        client.deleteProduct(fromApiOrg.products[0].id);
        client.verifyResult(webJson(4, "Sub Org can't do anything with the Product Templates created by Meta Org."));
    }

    @Test
    public void createProductForSubOrgAndUpdateItViaParentProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";

        client.createProduct(orgId, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("New Sub Org", "Some TimeZone", "/static/logo.png", false, orgId);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        Organization fromApiOrg = client.parseOrganization(2);
        assertNotNull(fromApiOrg);
        assertEquals(orgId, fromApiOrg.parentId);
        assertEquals(organization.name, fromApiOrg.name);
        assertEquals(organization.tzName, fromApiOrg.tzName);
        assertNotNull(fromApiOrg.products);
        assertEquals(1, fromApiOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiOrg.products[0].parentId);

        fromApiProduct.name = "Updated Name";
        client.updateProduct(orgId, fromApiProduct);
        fromApiProduct = client.parseProduct(3);
        assertNotNull(fromApiProduct);
        assertEquals("Updated Name", fromApiProduct.name);

        client.getProduct(fromApiOrg.products[0].id);
        Product subProduct = client.parseProduct(4);
        assertNotNull(subProduct);
        assertEquals("Updated Name", subProduct.name);
        assertEquals(fromApiProduct.id, subProduct.parentId);

    }
}
