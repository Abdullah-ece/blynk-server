package cc.blynk.integration.https;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Date;

import static cc.blynk.integration.TestUtil.illegalCommand;
import static cc.blynk.integration.TestUtil.illegalCommandBody;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.notAllowed;
import static cc.blynk.integration.TestUtil.ok;
import static java.time.LocalTime.ofSecondOfDay;
import static org.junit.Assert.assertEquals;
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
        client.verifyResult(illegalCommand(1));
    }

    @Test
    public void getProductById() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "getProductById";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        client.createProduct(1, product);
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

        client.createProduct(1, product);
        client.verifyResult(illegalCommandBody(1));
    }

    @Test
    public void updateProductWithNoName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "updateProductWithNoName";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;

        client.createProduct(1, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);
        assertEquals(product.description, fromApi.description);
        assertEquals(product.boardType, fromApi.boardType);
        assertEquals(product.connectionType, fromApi.connectionType);
        assertEquals(0, fromApi.version);

        product.id = 1;
        product.name = "";
        client.updateProduct(1, product);
        client.verifyResult(illegalCommandBody(2));
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
                new TextMetaField(1, "My Farm", Role.ADMIN, false, "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", Role.ADMIN, false, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, ofSecondOfDay(60), ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, MeasurementUnit.Celsius, "36"),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", Role.ADMIN, false, new Date().getTime()),
                new MeasurementUnitMetaField(10, "None Unit", Role.ADMIN, false, MeasurementUnit.None, "36"),
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        client.createProduct(1, product);
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
                new TextMetaField(1, "My Farm", Role.ADMIN, false, "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", Role.ADMIN, false, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, ofSecondOfDay(60), ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, MeasurementUnit.Celsius, "36"),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", Role.ADMIN, false, new Date().getTime())
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

        client.createProduct(1, product);
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

        client.updateProduct(1, product);
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

        client.createProduct(1, product);
        Product fromApi = client.parseProduct(1);
        assertNotNull(fromApi);
        assertEquals(product.name, fromApi.name);

        Product product2 = new Product();
        product2.name = "create2ProductsWithSameName";
        product2.description = "Description";
        product2.boardType = "ESP8266";
        product2.connectionType = ConnectionType.WI_FI;
        product2.logoUrl = "/static/logo.png";

        client.createProduct(1, product);
        client.verifyResult(illegalCommandBody(2));
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

        client.createProduct(1, product);
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

        client.updateProduct(1, product);
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

        client.createProduct(1, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        client.getProducts();
        Product[] fromApiProducts = client.parseProducts(2);
        assertNotNull(fromApiProducts);
        assertEquals(1, fromApiProducts.length);

        Product product2 = new Product();
        product2.name = "getListOfProducts2";

        client.createProduct(1, product2);
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

        client.createProduct(1, product);
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

        client.createProduct(1, product);
        Product fromApiProduct = client.parseProduct(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = 1;

        client.createDevice(1, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.deleteProduct(fromApiProduct.id);
        client.verifyResult(notAllowed(3));
    }



}
