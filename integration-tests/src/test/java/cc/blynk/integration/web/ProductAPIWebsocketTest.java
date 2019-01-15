package cc.blynk.integration.web;

import cc.blynk.integration.SingleServerInstancePerTestWithDBAndNewOrg;
import cc.blynk.integration.model.websocket.AppWebSocketClient;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.dto.ProductDTO;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.MetadataType;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.WebDashboard;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.web.product.events.system.OfflineEvent;
import cc.blynk.server.core.model.web.product.events.system.OnlineEvent;
import cc.blynk.server.core.model.web.product.events.user.CriticalEvent;
import cc.blynk.server.core.model.web.product.events.user.InformationEvent;
import cc.blynk.server.core.model.web.product.events.user.WarningEvent;
import cc.blynk.server.core.model.web.product.metafields.AddressMetaField;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.CoordinatesMetaField;
import cc.blynk.server.core.model.web.product.metafields.CostMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceNameMetaField;
import cc.blynk.server.core.model.web.product.metafields.DeviceOwnerMetaField;
import cc.blynk.server.core.model.web.product.metafields.ListMetaField;
import cc.blynk.server.core.model.web.product.metafields.LocationMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.core.model.web.product.metafields.SwitchMetaField;
import cc.blynk.server.core.model.web.product.metafields.TemplateIdMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.web.product.metafields.TimeMetaField;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.WebSlider;
import cc.blynk.server.core.model.widgets.web.WebSource;
import cc.blynk.server.core.model.widgets.web.WebSwitch;
import cc.blynk.server.core.model.widgets.web.label.WebLabel;
import cc.blynk.utils.SHA256Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Date;

import static cc.blynk.integration.APIBaseTest.createContactMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceNameMeta;
import static cc.blynk.integration.APIBaseTest.createDeviceOwnerMeta;
import static cc.blynk.integration.APIBaseTest.createMeasurementMeta;
import static cc.blynk.integration.APIBaseTest.createNumberMeta;
import static cc.blynk.integration.APIBaseTest.createTemplateIdMeta;
import static cc.blynk.integration.APIBaseTest.createTextMeta;
import static cc.blynk.integration.TestUtil.createWebLabelWidget;
import static cc.blynk.integration.TestUtil.createWebSliderWidget;
import static cc.blynk.integration.TestUtil.createWebSwitchWidget;
import static cc.blynk.integration.TestUtil.defaultClient;
import static cc.blynk.integration.TestUtil.loggedDefaultClient;
import static cc.blynk.integration.TestUtil.ok;
import static cc.blynk.integration.TestUtil.updateProductMetafields;
import static cc.blynk.integration.TestUtil.updateProductName;
import static cc.blynk.integration.TestUtil.updateProductWebDash;
import static cc.blynk.integration.TestUtil.webJson;
import static cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType.RAW_DATA;
import static java.time.LocalTime.ofSecondOfDay;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

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
        client.verifyResult(webJson(1, "Product with passed id 1333 not exists."));
    }

    @Test
    public void printProduct() throws Exception {
        Product product = new Product();
        product.id = 1;
        product.name = "My Product";
        product.boardType = "Arduino UNO";
        product.connectionType = ConnectionType.WI_FI;
        product.description = "Description";
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                createTextMeta(1, "My Farm", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", new int[] {1}, false, false, false, null, ofSecondOfDay(60),  ofSecondOfDay(120)),
                createNumberMeta(3, "Farm of Smith", 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", new int[] {1}, false, false, false, null, MeasurementUnit.Celsius, 36, 0, 100, 1),
                new CostMetaField(5, "Farm of Smith", new int[] {1}, false, false, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", new int[] {1}, false, false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false, "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", new int[] {1}, false, false, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", new int[] {1}, false, false, false, null, 22.222, 23.333),
                new TimeMetaField(9,"Some Time", new int[] {1}, false, false, false, null, new Date().getTime()),
                createDeviceNameMeta(10, "DEvice Name", "123", true),
                createDeviceOwnerMeta(11, "Owner", "123", true)
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        EventReceiver eventReceiver = new EventReceiver(1, MetadataType.Contact, "Farm Owner");

        OnlineEvent onlineEvent = new OnlineEvent(
                1, "Your device is online.", null, false,
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver}
        );


        OfflineEvent offlineEvent = new OfflineEvent(
                2, "Your device is offline.", null , false,
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver},
                1000
        );

        InformationEvent infoEvent = new InformationEvent(
                3, "Door is opened", "Kitchen door is opened.", false, "door_opened",
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver}
        );

        WarningEvent warningEvent = new WarningEvent(
                4, "Temperature is high!", "Room temp is high", false, "temp_is_high",
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver}
        );

        CriticalEvent criticalEvent = new CriticalEvent(
                5, "Temperature is super high!", "Room temp is super high", false, "temp_is_super_high",
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver},
                new EventReceiver[]{eventReceiver}
        );

        product.events = new Event[] {
                onlineEvent,
                offlineEvent,
                infoEvent,
                warningEvent,
                criticalEvent
        };

        System.out.println(JsonParser.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(product));
    }

    @Test
    public void getProductById() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "getProductById";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        client.getProduct(fromApiProduct.id);
        fromApiProduct = client.parseProductDTO(2);
        assertNotNull(fromApiProduct);
    }

    @Test
    public void createProductWithListMetaAndValuesAreTrimmed() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "123";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true),
                new ListMetaField(3, "!@3", null, false, false, false, null, new String[] {" 123", "124 "}, null)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct= client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertEquals(3, fromApiProduct.metaFields.length);
        ListMetaField listMetaField = (ListMetaField) fromApiProduct.metaFields[2];
        assertArrayEquals(new String[] {"123", "124"}, listMetaField.options);
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
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        client.verifyResult(webJson(1, "Product name is empty."));
    }

    @Test
    public void updateProductWithNoName() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "updateProductWithNoName";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct= client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertEquals(product.boardType, fromApiProduct.boardType);
        assertEquals(product.connectionType, fromApiProduct.connectionType);
        assertEquals(0, fromApiProduct.version);

        product.id = 1;
        product.name = "";
        client.updateProduct(orgId, product);
        client.verifyResult(webJson(2, "Product name is empty."));
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
                createTextMeta(1, "My Farm", "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", new int[] {1}, false, false, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", new int[] {1}, false, false, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                createNumberMeta(3, "Farm of Smith", 10.222),
                new MeasurementUnitMetaField(4, "Farm of Smith", new int[] {1}, false, false, false, null, MeasurementUnit.Celsius, 36, 0, 100, 1),
                new CostMetaField(5, "Farm of Smith", new int[] {1}, false, false, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", new int[] {1},  false,false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", new int[] {1}, false, false, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", new int[] {1}, false, false, false, null, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", new int[] {1}, false, false, false, null, new Date().getTime()),
                new MeasurementUnitMetaField(10, "None Unit", new int[] {1}, false, false, false, null, MeasurementUnit.None, 36, 0, 100, 1),
                createDeviceNameMeta(10, "DEvice Name", "123", true),
                createDeviceOwnerMeta(11, "Owner", "123", true)
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertEquals(product.boardType, fromApiProduct.boardType);
        assertEquals(product.connectionType, fromApiProduct.connectionType);
        assertEquals(product.logoUrl, fromApiProduct.logoUrl);
        assertEquals(0, fromApiProduct.version);
        assertNotEquals(0, fromApiProduct.lastModifiedTs);
        assertNotNull(fromApiProduct.dataStreams);
        assertNotNull(fromApiProduct.metaFields);
        assertEquals(13, fromApiProduct.metaFields.length);
    }

    @Test
    public void createProductWithWrongMetafields() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductWithMetafields2";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                createTextMeta(1, "My Farm", "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", new int[] {1}, false, false, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", new int[] {1}, false, false, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                createNumberMeta(3, "Farm of Smith", 10.222),
                createMeasurementMeta(4, "Farm of Smith", 1, null),
                new CostMetaField(5, "Farm of Smith", new int[] {1}, false, false, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100)
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        client.createProduct(product);
        client.verifyResult(webJson(1, "Metafield is not valid. Units field is empty."));

        product.metaFields = new MetaField[] {
                new TextMetaField(1, null, new int[] {1}, false, false, false, null, "Farm of Smith"),
        };

        client.createProduct(product);
        client.verifyResult(webJson(2, "Metafield is not valid. Name is empty."));
    }

    @Test
    public void createProductWithWrongDatastreams() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductWithMetafields2";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                createDeviceNameMeta(10, "DEvice Name", "123", true),
                createDeviceOwnerMeta(11, "Owner", "123", true)
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (short) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius),
                new DataStream(1, (short) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        client.createProduct(product);
        client.verifyResult(webJson(1, "Product has more than 1 Datastream on the same pin."));
    }

    @Test
    public void createProductWithDuplicatedTemplateIdMetafields() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductWithMetafields2";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.logoUrl = "/static/logo.png";

        product.metaFields = new MetaField[] {
                createTextMeta(1, "My Farm", "Farm of Smith"),
                createTemplateIdMeta(2, "TemplateId", "temp"),
                createTemplateIdMeta(3, "TemplateId2", "temp2")
        };

        client.createProduct(product);
        client.verifyResult(webJson(1, "Product has more than 1 TemplateId metafield."));
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
                createTextMeta(1, "My Farm", "Farm of Smith"),
                new SwitchMetaField(1, "My Farm", new int[] {1}, false, false, false, null, "0", "1", "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", new int[] {1}, false, false, false, null, ofSecondOfDay(60), ofSecondOfDay(120)),
                createNumberMeta(3, "Farm of Smith", 10.222),
                createMeasurementMeta(4, "Farm of Smith", 100, MeasurementUnit.Celsius),
                createContactMeta(5, "Farm of Smith"),
                new ContactMetaField(6, "Farm of Smith", new int[] {1}, false, false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false,
                        "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", new int[] {1}, false, false, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", new int[] {1}, false, false, false, null, 22.222, 23.333),
                new TimeMetaField(9, "Some Time", new int[] {1}, false, false, false, null, new Date().getTime()),
                createDeviceNameMeta(10, "DEvice Name", "123", true),
                createDeviceOwnerMeta(11, "Owner", "123", true)
        };

        product.dataStreams = new DataStream[] {
                new DataStream(0, (byte) 0, false, false, PinType.VIRTUAL, null, 0, 50, "Temperature", MeasurementUnit.Celsius)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                createWebLabelWidget(1, "123"),
                createWebSwitchWidget(2, "onLabel", 1),
                createWebSliderWidget(3, "Slider", 1)
        });


        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertEquals(product.boardType, fromApiProduct.boardType);
        assertEquals(product.connectionType, fromApiProduct.connectionType);
        assertEquals(product.logoUrl, fromApiProduct.logoUrl);
        assertEquals(0, fromApiProduct.version);
        assertNotEquals(0, fromApiProduct.lastModifiedTs);
        assertNotNull(fromApiProduct.dataStreams);
        assertNotNull(fromApiProduct.metaFields);
        assertEquals(12, fromApiProduct.metaFields.length);
        assertNotNull(fromApiProduct.webDashboard);
        assertEquals(3, fromApiProduct.webDashboard.widgets.length);

        WebLabel webLabel = (WebLabel) fromApiProduct.webDashboard.widgets[0];
        assertEquals("123", webLabel.label);
        assertEquals(1, webLabel.id);
        assertEquals(1, webLabel.x);
        assertEquals(2, webLabel.y);
        assertEquals(10, webLabel.height);
        assertEquals(20, webLabel.width);

        WebSwitch webSwitch = (WebSwitch) fromApiProduct.webDashboard.widgets[1];
        assertEquals("onLabel", webSwitch.onLabel);
        assertEquals(2, webSwitch.id);
        assertEquals(3, webSwitch.x);
        assertEquals(4, webSwitch.y);
        assertEquals(50, webSwitch.height);
        assertEquals(60, webSwitch.width);

        WebSlider webSlider = (WebSlider) fromApiProduct.webDashboard.widgets[2];
        assertEquals("Slider", webSlider.label);
        assertEquals(3, webSlider.id);
        assertEquals(3, webSlider.x);
        assertEquals(4, webSlider.y);
        assertEquals(50, webSlider.height);
        assertEquals(60, webSlider.width);

        product.id = fromApiProduct.id;
        product.description = "Description2";

        product.webDashboard = new WebDashboard(new Widget[] {
                createWebLabelWidget(1, "updated")
        });

        client.updateProduct(orgId, product);
        fromApiProduct = client.parseProductDTO(2);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertNotNull(fromApiProduct.webDashboard);
        assertEquals(1, fromApiProduct.webDashboard.widgets.length);

        webLabel = (WebLabel) fromApiProduct.webDashboard.widgets[0];
        assertEquals("updated", webLabel.label);
        assertEquals(1, webLabel.id);
        assertEquals(1, webLabel.x);
        assertEquals(2, webLabel.y);
        assertEquals(10, webLabel.height);
        assertEquals(20, webLabel.width);
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
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);

        Product product2 = new Product();
        product2.name = "create2ProductsWithSameName";
        product2.description = "Description";
        product2.boardType = "ESP8266";
        product2.connectionType = ConnectionType.WI_FI;
        product2.logoUrl = "/static/logo.png";

        client.createProduct(product);
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
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertEquals(product.boardType, fromApiProduct.boardType);
        assertEquals(product.connectionType, fromApiProduct.connectionType);
        assertEquals(0, fromApiProduct.version);

        product.id = fromApiProduct.id;
        product.name = "Updated Name";
        product.description = "Description2";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.updateProduct(orgId, product);
        fromApiProduct = client.parseProductDTO(2);
        assertNotNull(fromApiProduct);
        assertEquals(product.name, fromApiProduct.name);
        assertEquals(product.description, fromApiProduct.description);
        assertEquals("Updated Name", fromApiProduct.name);
        assertEquals("Description2", fromApiProduct.description);
    }

    @Test
    public void getEmptyListOfProducts() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");
        client.getProducts(orgId);
        ProductDTO[] fromApiProduct = client.parseProductDTOs(1);
        assertNotNull(fromApiProduct);
        assertEquals(1, fromApiProduct.length);
    }

    @Test
    public void getListOfProducts() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "getListOfProducts";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        client.getProducts(orgId);
        ProductDTO[] fromApiProducts = client.parseProductDTOs(2);
        assertNotNull(fromApiProducts);
        assertEquals(2, fromApiProducts.length);

        Product product2 = new Product();
        product2.name = "getListOfProducts2";
        product2.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product2);
        fromApiProduct = client.parseProductDTO(3);
        assertNotNull(fromApiProduct);

        client.getProducts(orgId);
        fromApiProducts = client.parseProductDTOs(4);
        assertNotNull(fromApiProducts);
        assertEquals(3, fromApiProducts.length);
    }

    @Test
    public void createProductAndDelete() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductAndDelete";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        client.getProducts(orgId);
        ProductDTO[] fromApiProducts = client.parseProductDTOs(2);
        assertNotNull(fromApiProducts);
        assertEquals(2, fromApiProducts.length);

        client.deleteProduct(fromApiProduct.id);
        client.verifyResult(ok(3));

        client.getProducts(orgId);
        fromApiProducts = client.parseProductDTOs(4);
        assertNotNull(fromApiProducts);
        assertEquals(1, fromApiProducts.length);
    }

    @Test
    public void canDeleteProductWithDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductAndDelete";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(3);
        assertNotNull(devices);
        assertEquals(2, devices.length);

        client.deleteProduct(fromApiProduct.id);
        client.verifyResult(ok(4));

        client.getDevices(orgId);
        devices = client.parseDevicesDTO(5);
        assertNotNull(devices);
        assertEquals(1, devices.length);
    }

    @Test
    public void testDevicesForProductNotReturned() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProduct";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.getProduct(fromApiProduct.id);
        fromApiProduct = client.parseProductDTO(3);
        assertNotNull(fromApiProduct);
        assertEquals(1, fromApiProduct.deviceCount);
        //todo check actual json
    }


    @Test
    public void canDeleteProductWithDevices2() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "createProductAndDelete";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(2);
        assertNotNull(createdDevice);

        client.getDevices(orgId);
        DeviceDTO[] devices = client.parseDevicesDTO(3);
        assertNotNull(devices);
        assertEquals(2, devices.length);

        client.deleteDevice(orgId, createdDevice.id);
        client.verifyResult(ok(4));

        client.getProduct(fromApiProduct.id);
        fromApiProduct = client.parseProductDTO(5);
        assertNotNull(fromApiProduct);
        assertNotNull(product.devices);
        assertEquals(0, product.devices.length);

        client.getDevices(orgId);
        devices = client.parseDevicesDTO(6);
        assertNotNull(devices);
        assertEquals(1, devices.length);
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
                createDeviceNameMeta(1, "Device name", "Default name", true),
                createDeviceOwnerMeta(2, "Device owner", "admin@blynk.cc", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001"),
                createContactMeta(4, "Contact", "icon")
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(4, newDevice.metaFields.length);

        DeviceNameMetaField deviceNameMetaField = (DeviceNameMetaField) newDevice.metaFields[0];
        assertEquals(1, deviceNameMetaField.id);
        assertEquals("Device name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals(newDevice.name, deviceNameMetaField.value);

        DeviceOwnerMetaField deviceOwnerMetaField = (DeviceOwnerMetaField) newDevice.metaFields[1];
        assertEquals(2, deviceOwnerMetaField.id);
        assertEquals("Device owner", deviceOwnerMetaField.name);
        assertEquals(1, deviceOwnerMetaField.roleIds[0]);
        assertEquals(getUserName(), deviceOwnerMetaField.value);

        TemplateIdMetaField templateIdMetaField = (TemplateIdMetaField) newDevice.metaFields[2];
        assertEquals(3, templateIdMetaField.id);
        assertEquals("Template Id", templateIdMetaField.name);
        assertEquals(1, templateIdMetaField.roleIds[0]);
        assertEquals("TMPL0001", templateIdMetaField.options[0]);

        ContactMetaField contactMetaField = (ContactMetaField) newDevice.metaFields[3];
        assertEquals(4, contactMetaField.id);
        assertEquals("Contact", contactMetaField.name);
        assertEquals(1, contactMetaField.roleIds[0]);
        assertEquals("icon", contactMetaField.icon);

        newDevice.metaFields[0] = createDeviceNameMeta(1, "Device name 2", "Updated device name", true);

        client.updateDevice(orgId, newDevice);
        newDevice = client.parseDevice(3);
        assertNotNull(newDevice);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(4, newDevice.metaFields.length);
        deviceNameMetaField = (DeviceNameMetaField) newDevice.metaFields[0];
        assertEquals(1, deviceNameMetaField.id);
        assertEquals("Device name", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals(newDevice.name, deviceNameMetaField.value);

        fromApiProduct = updateProductMetafields(fromApiProduct,
                createDeviceNameMeta(1, "Device name 2", "Updated device name", true),
                createDeviceOwnerMeta(2, "Device owner", "admin@blynk.cc", true),
                createTemplateIdMeta(3, "Template Id", "TMPL0001"),
                createContactMeta(4, "Contact", "icon"),
                createNumberMeta(5, "New metafield", 123)
        );

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(4);
        assertEquals(5, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        newDevice = client.parseDevice(5);

        deviceNameMetaField = (DeviceNameMetaField) newDevice.metaFields[0];
        assertEquals(1, deviceNameMetaField.id);
        assertEquals("Device name 2", deviceNameMetaField.name);
        assertEquals(1, deviceNameMetaField.roleIds[0]);
        assertEquals("My New Device", deviceNameMetaField.value);

        deviceOwnerMetaField = (DeviceOwnerMetaField) newDevice.metaFields[1];
        assertEquals(2, deviceOwnerMetaField.id);
        assertEquals("Device owner", deviceOwnerMetaField.name);
        assertEquals(1, deviceOwnerMetaField.roleIds[0]);
        assertEquals(getUserName(), deviceOwnerMetaField.value);

        templateIdMetaField = (TemplateIdMetaField) newDevice.metaFields[2];
        assertEquals(3, templateIdMetaField.id);
        assertEquals("Template Id", templateIdMetaField.name);
        assertEquals(1, templateIdMetaField.roleIds[0]);
        assertEquals("TMPL0001", templateIdMetaField.options[0]);

        contactMetaField = (ContactMetaField) newDevice.metaFields[3];
        assertEquals(4, contactMetaField.id);
        assertEquals("Contact", contactMetaField.name);
        assertEquals(1, contactMetaField.roleIds[0]);
        assertEquals("icon", contactMetaField.icon);

        NumberMetaField numberMetaField = (NumberMetaField) newDevice.metaFields[4];
        assertEquals(5, numberMetaField.id);
        assertEquals("New metafield", numberMetaField.name);
        assertEquals(1, numberMetaField.roleIds[0]);
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
                createTextMeta(1, "My test metafield", "Default Device"),
                new LocationMetaField(2, "Device Location", new int[] {1}, false, false, false, "icon",
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
                        0,
                        null),
                createContactMeta(3, "Contact", "contact_icon"),
                createDeviceNameMeta(4, "Device name", "Default name", true),
                createDeviceOwnerMeta(5, "Device owner", "admin@blynk.cc", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);

        fromApiProduct.metaFields[0] = createTextMeta(1, "Me updated test metafield", "Default Device");
        fromApiProduct.metaFields[1] = new LocationMetaField(2, "Device Location", new int[] {1}, false, false, false, "icon2",
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
                0,
                null);
        fromApiProduct.metaFields[2] = createContactMeta(3, "Contact", "contact_icon_2");

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(3);
        assertNotNull(fromApiProduct);
        assertEquals(5, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        Device device = client.parseDevice(4);
        assertNotNull(newDevice);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(5, device.metaFields.length);

        TextMetaField textMetaField = (TextMetaField) device.metaFields[0];
        assertEquals(1, textMetaField.id);
        assertEquals("Me updated test metafield", textMetaField.name);
        assertEquals("Default Device", textMetaField.value);

        LocationMetaField locationMetaField = (LocationMetaField) device.metaFields[1];
        assertEquals(2, locationMetaField.id);
        assertEquals("Device Location", locationMetaField.name);
        assertEquals("icon2", locationMetaField.icon);

        ContactMetaField contactMetaField = (ContactMetaField) device.metaFields[2];
        assertEquals(3, contactMetaField.id);
        assertEquals("Contact", contactMetaField.name);
        assertEquals("contact_icon_2", contactMetaField.icon);
    }

    @Test
    public void testDeleteMetaDataFieldInChildDevices() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                createTextMeta(1, "My test metafield", "Default Device"),
                new LocationMetaField(2, "Device Location", new int[] {1}, false, false, false, "icon",
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
                        0,
                        null),
                createNumberMeta(3, "floor", 5),
                createContactMeta(4, "Contact", "contact_icon"),
                createDeviceNameMeta(10, "DEvice Name", "123", true),
                createDeviceOwnerMeta(11, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);
        assertEquals(6, newDevice.metaFields.length);
        client.updateDeviceMetafield(newDevice.id,
                new LocationMetaField(2, "Device Location2", new int[]{1}, false, false, false, "icon2",
                "Updated Site NAme",
                true, "Baklazhana street 152",
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
                        0,
                null));
        client.verifyResult(ok(3));

        fromApiProduct = updateProductMetafields(fromApiProduct,
                createTextMeta(1, "My test metafield", "Default Device"),
                new LocationMetaField(2, "Device Location", new int[]{1}, false, false, false, "icon",
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
                        0,
                        null),
                createContactMeta(4, "Contact", "contact_icon"),
                createDeviceNameMeta(5, "Device name", "Default name", true),
                createDeviceOwnerMeta(6, "Device owner", "admin@blynk.cc", true)
        );

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(4);
        assertNotNull(fromApiProduct);
        assertEquals(5, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        Device device = client.parseDevice(5);
        assertNotNull(newDevice);
        assertEquals("My New Device", device.name);
        assertNotNull(device.metaFields);
        assertEquals(5, device.metaFields.length);

        TextMetaField textMetaField = (TextMetaField) device.metaFields[0];
        assertEquals(1, textMetaField.id);
        assertEquals("My test metafield", textMetaField.name);
        assertEquals("Default Device", textMetaField.value);

        LocationMetaField locationMetaField = (LocationMetaField) device.metaFields[1];
        assertEquals(2, locationMetaField.id);
        assertEquals("Device Location", locationMetaField.name);
        assertEquals("icon", locationMetaField.icon);
        //assertEquals("Updated Site NAme", locationMetaField.siteName);
        assertEquals("Baklazhana street 152", locationMetaField.streetAddress);

        ContactMetaField contactMetaField = (ContactMetaField) device.metaFields[2];
        assertEquals(4, contactMetaField.id);
        assertEquals("Contact", contactMetaField.name);
        assertEquals("contact_icon", contactMetaField.icon);
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
                new ContactMetaField(1, "Farm of Smith", new int[] {1}, false, false, false, "Tech Support",
                        "Dmitriy", true, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false, "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                createDeviceNameMeta(2, "Device name", "Default name", true),
                createDeviceOwnerMeta(3, "Device owner", "admin@blynk.cc", true),
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;

        client.createDevice(orgId, newDevice);
        newDevice = client.parseDevice(2);
        assertNotNull(newDevice);
        assertEquals("My New Device", newDevice.name);
        assertNotNull(newDevice.metaFields);
        assertEquals(3, newDevice.metaFields.length);
        ContactMetaField contactMetaField = (ContactMetaField) newDevice.metaFields[0];
        assertEquals(1, contactMetaField.id);
        assertEquals("Farm of Smith", contactMetaField.name);
        assertEquals(1, contactMetaField.roleIds[0]);
        assertTrue(contactMetaField.isFirstNameEnabled);
        assertFalse(contactMetaField.isLastNameEnabled);

        fromApiProduct.metaFields[0] = new ContactMetaField(1, "Farm of Smith", new int[] {1}, false, false, false, "Tech Support",
                "Dmitriy", true, "Dumanskiy", true, "dmitriy@blynk.cc", false,
                "+38063673333",  false, "My street", false, "Ukraine", false,
                "Kyiv", false, "Ukraine", false, "03322", false, false);

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(3);
        assertNotNull(fromApiProduct);
        assertEquals(3, fromApiProduct.metaFields.length);

        client.getDevice(orgId, newDevice.id);
        Device device = client.parseDevice(4);
        assertNotNull(newDevice);
        assertNotNull(device.metaFields);
        assertEquals(3, device.metaFields.length);
        contactMetaField = (ContactMetaField) device.metaFields[0];
        assertTrue(contactMetaField.isFirstNameEnabled);
        assertTrue(contactMetaField.isLastNameEnabled);
    }

    @Test
    public void canDeleteProductRequest() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        Product product = new Product();
        product.name = "My new product";
        product.description = "Description";
        product.boardType = "ESP8266";
        product.connectionType = ConnectionType.WI_FI;
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
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
        client.verifyResult(ok(4));
    }

    @Test
    public void createProductForSubOrgAndCannotUpdateItDirectly() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("Sub Org", "Some TimeZone", "/static/logo.png", false, -1);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductOrg);
        assertEquals(orgId, fromApiProductOrg.parentId);
        assertEquals(organization.name, fromApiProductOrg.name);
        assertEquals(organization.tzName, fromApiProductOrg.tzName);
        assertNotNull(fromApiProductOrg.products);
        assertEquals(1, fromApiProductOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiProductOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg.products[0].parentId);

        client.updateProduct(fromApiProductOrg.id, fromApiProductOrg.products[0]);
        client.verifyResult(webJson(3, "Sub Org can't do anything with the Product Templates created by Meta Org."));

        client.deleteProduct(fromApiProductOrg.products[0].id);
        client.verifyResult(webJson(4, "Sub Org can't do anything with the Product Templates created by Meta Org."));
    }

    @Test
    public void createProductForSubOrgAndUpdateItViaParentProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("New Sub Org", "Some TimeZone", "/static/logo.png", false, -1);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductOrg);
        assertEquals(orgId, fromApiProductOrg.parentId);
        assertEquals(organization.name, fromApiProductOrg.name);
        assertEquals(organization.tzName, fromApiProductOrg.tzName);
        assertNotNull(fromApiProductOrg.products);
        assertEquals(1, fromApiProductOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiProductOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg.products[0].parentId);

        fromApiProduct = updateProductName(fromApiProduct, "Updated Name");
        webLabel = new WebLabel();
        webLabel.label = "4444";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        fromApiProduct = updateProductWebDash(fromApiProduct, webLabel);

        client.updateProduct(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(3);
        assertNotNull(fromApiProduct);
        assertEquals("Updated Name", fromApiProduct.name);
        assertNotNull(fromApiProduct.webDashboard.widgets[0]);
        assertEquals("4444", fromApiProduct.webDashboard.widgets[0].label);

        client.trackOrg(fromApiProductOrg.id);
        client.verifyResult(ok(4));
        client.getProduct(fromApiProductOrg.products[0].id);
        ProductDTO subProduct = client.parseProductDTO(5);
        assertNotNull(subProduct);
        assertEquals("Updated Name", subProduct.name);
        assertEquals(fromApiProduct.id, subProduct.parentId);
        assertNotNull(subProduct.webDashboard.widgets[0]);
        assertEquals("4444", subProduct.webDashboard.widgets[0].label);
    }

    @Test
    public void createProductForSubOrgAndUpdateItAndItDevicesViaParentProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "Parent product";
        product.metaFields = new MetaField[] {
                createTextMeta(1, "My test metafield", "Default Device"),
                createDeviceNameMeta(2, "Device Name", "123", true),
                createDeviceOwnerMeta(3, "Owner", "123", true)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("Sub Org for test 2", "Some TimeZone", "/static/logo.png", false, -1);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductSubOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductSubOrg);
        assertEquals(orgId, fromApiProductSubOrg.parentId);
        assertEquals(organization.name, fromApiProductSubOrg.name);
        assertEquals(organization.tzName, fromApiProductSubOrg.tzName);
        assertNotNull(fromApiProductSubOrg.products);
        assertEquals(1, fromApiProductSubOrg.products.length);
        ProductDTO productInResponse = fromApiProductSubOrg.products[0];
        assertEquals(fromApiProduct.id + 1, productInResponse.id);
        assertEquals(fromApiProduct.id, productInResponse.parentId);
        assertNotNull(productInResponse.metaFields);
        assertEquals(3, productInResponse.metaFields.length);

        Device newDevice = new Device();
        newDevice.name = "My New Device for subproduct";
        newDevice.productId = productInResponse.id;

        client.trackOrg(fromApiProductSubOrg.id);
        client.verifyResult(ok(3));
        client.createDevice(fromApiProductSubOrg.id, newDevice);
        Device createdSubDevice = client.parseDevice(4);
        assertNotNull(createdSubDevice);
        assertNotNull(createdSubDevice.metaFields);
        assertEquals(3, createdSubDevice.metaFields.length);

        fromApiProduct = updateProductName(fromApiProduct, "Updated Name");
        webLabel = new WebLabel();
        webLabel.label = "4444";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };
        fromApiProduct = updateProductWebDash(fromApiProduct, webLabel);
        fromApiProduct = updateProductMetafields(fromApiProduct,
                createTextMeta(1, "My test metafield 2", "Default Device"),
                createDeviceNameMeta(2, "Device Name", "123", true),
                createDeviceOwnerMeta(3, "Owner", "123", true)
        );

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(5);
        assertNotNull(fromApiProduct);
        assertEquals("Updated Name", fromApiProduct.name);
        assertNotNull(fromApiProduct.webDashboard.widgets[0]);
        assertEquals("4444", fromApiProduct.webDashboard.widgets[0].label);
        assertNotNull(fromApiProduct.metaFields);
        assertEquals("My test metafield 2", fromApiProduct.metaFields[0].name);

        client.trackOrg(fromApiProductSubOrg.id);
        client.verifyResult(ok(6));
        client.getProduct(fromApiProductSubOrg.products[0].id);
        ProductDTO subProduct = client.parseProductDTO(7);
        assertNotNull(subProduct);
        assertEquals("Updated Name", subProduct.name);
        assertEquals(fromApiProduct.id, subProduct.parentId);
        assertNotNull(subProduct.webDashboard.widgets[0]);
        assertEquals("4444", subProduct.webDashboard.widgets[0].label);
        assertNotNull(fromApiProduct.metaFields);
        assertEquals("My test metafield 2", fromApiProduct.metaFields[0].name);

        client.getDevice(fromApiProductSubOrg.id, createdSubDevice.id);
        createdSubDevice = client.parseDevice(8);
        assertNotNull(createdSubDevice);
        assertNotNull(createdSubDevice.metaFields);
        assertEquals(3, createdSubDevice.metaFields.length);
        assertNotNull(createdSubDevice.metaFields);
        assertEquals("My test metafield 2", createdSubDevice.metaFields[0].name);
    }

    @Test
    public void createProductForSubSubOrgAndUpdateItViaParentProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.parentId = 0;
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("createProductForSubSubOrgAndUpdateItViaParentProduct",
                "Some TimeZone", "/static/logo.png", false, -1);
        organization.canCreateOrgs = true;
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductOrg);
        assertEquals(orgId, fromApiProductOrg.parentId);
        assertEquals(organization.name, fromApiProductOrg.name);
        assertEquals(organization.tzName, fromApiProductOrg.tzName);
        assertNotNull(fromApiProductOrg.products);
        assertEquals(1, fromApiProductOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiProductOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg.products[0].parentId);

        client.trackOrg(fromApiProductOrg.id);
        client.verifyResult(ok(3));

        Organization organization2 = new Organization("New Sub Sub Org", "Some TimeZone", "/static/logo.png", false, -1);
        organization2.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization2);
        OrganizationDTO fromApiProductOrg2 = client.parseOrganizationDTO(4);
        assertNotNull(fromApiProductOrg2);
        assertEquals(fromApiProductOrg.id, fromApiProductOrg2.parentId);
        assertEquals(organization2.name, fromApiProductOrg2.name);
        assertEquals(organization2.tzName, fromApiProductOrg2.tzName);
        assertNotNull(fromApiProductOrg2.products);
        assertEquals(1, fromApiProductOrg2.products.length);
        assertEquals(fromApiProduct.id + 2, fromApiProductOrg2.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg2.products[0].parentId);

        client.trackOrg(fromApiProductOrg2.id);
        client.verifyResult(ok(5));

        Device newDevice = new Device();
        newDevice.name = "My New Device for subsubproduct";
        newDevice.productId = fromApiProductOrg2.products[0].id;
        client.createDevice(fromApiProductOrg2.id, newDevice);
        Device createdSubDevice = client.parseDevice(6);
        assertNotNull(createdSubDevice);
        assertNotNull(createdSubDevice.metaFields);
        assertEquals(2, createdSubDevice.metaFields.length);

        client.trackOrg(orgId);
        client.verifyResult(ok(7));

        fromApiProduct = updateProductName(fromApiProduct, "Updated Name");
        webLabel = new WebLabel();
        webLabel.label = "4444";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };
        fromApiProduct = updateProductWebDash(fromApiProduct, webLabel);
        fromApiProduct = updateProductMetafields(fromApiProduct,
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true),
                createTextMeta(3, "My test metafield 2", "Default Device")
        );

        client.updateDevicesMeta(orgId, fromApiProduct);
        fromApiProduct = client.parseProductDTO(8);
        assertNotNull(fromApiProduct);
        assertEquals("Updated Name", fromApiProduct.name);
        assertNotNull(fromApiProduct.webDashboard.widgets[0]);
        assertEquals("4444", fromApiProduct.webDashboard.widgets[0].label);
        assertNotNull(fromApiProduct.metaFields);
        assertEquals("My test metafield 2", fromApiProduct.metaFields[2].name);

        client.getDevice(-1, createdSubDevice.id);
        createdSubDevice = client.parseDevice(9);
        assertNotNull(createdSubDevice);
        assertNotNull(createdSubDevice.metaFields);
        assertEquals(3, createdSubDevice.metaFields.length);
        assertNotNull(createdSubDevice.metaFields);
        assertEquals("My test metafield 2", createdSubDevice.metaFields[2].name);
    }

    @Test
    public void doNotSelectAnyProductsForTheSubOrg() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("doNotSelectAnyProductsForTheOrg",
                "Some TimeZone", "/static/logo.png", false, -1);
        organization.canCreateOrgs = true;
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductOrg);
        assertEquals(orgId, fromApiProductOrg.parentId);
        assertEquals(organization.name, fromApiProductOrg.name);
        assertEquals(organization.tzName, fromApiProductOrg.tzName);
        assertNotNull(fromApiProductOrg.products);
        assertEquals(1, fromApiProductOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiProductOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg.products[0].parentId);

        client.trackOrg(fromApiProductOrg.id);
        client.verifyResult(ok(3));

        Organization organization2 = new Organization("doNotSelectAnyProductsForTheSubOrg2",
                "Some TimeZone", "/static/logo.png", false, -1);

        client.createOrganization(organization2);
        OrganizationDTO fromApiProductOrg2 = client.parseOrganizationDTO(4);
        assertNotNull(fromApiProductOrg2);
        assertEquals(fromApiProductOrg.id, fromApiProductOrg2.parentId);
        assertEquals(organization2.name, fromApiProductOrg2.name);
        assertEquals(organization2.tzName, fromApiProductOrg2.tzName);
        assertNull(fromApiProductOrg2.products);
    }

    @Test
    public void deviceOwnerErasedAfterUserIsRemoved() throws Exception {
        AppWebSocketClient client = loggedDefaultClient(getUserName(), "1");

        //create product
        Product product = new Product();
        product.name = "createProduct";
        product.metaFields = new MetaField[] {
                createDeviceOwnerMeta(1, "Device Owner", "fake@blynk.cc", true),
                createDeviceNameMeta(2, "Device Name", "123", true)
        };
        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        //invite and login new user
        String invitedUser = "invited@blynk.cc";
        client.inviteUser(orgId, "invited@blynk.cc", "Dmitriy", 3);
        client.verifyResult(ok(2));
        ArgumentCaptor<String> bodyArgumentCapture = ArgumentCaptor.forClass(String.class);
        verify(holder.mailWrapper, timeout(1000).times(1)).sendHtml(eq(invitedUser),
                eq("Invitation to Blynk Inc. dashboard."), bodyArgumentCapture.capture());
        String body = bodyArgumentCapture.getValue();
        String token = body.substring(body.indexOf("token=") + 6, body.indexOf("&"));
        String passHash = SHA256Util.makeHash("123", invitedUser);
        AppWebSocketClient client2 = defaultClient();
        client2.start();
        client2.loginViaInvite(token, passHash);
        client2.verifyResult(ok(1));

        //create new device
        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.productId = fromApiProduct.id;
        client.createDevice(orgId, newDevice);
        Device createdDevice = client.parseDevice(3);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);

        //update owner
        client.updateDeviceMetafield(createdDevice.id, createDeviceOwnerMeta(1, "Device Owner", invitedUser, true));
        client.verifyResult(ok(4));

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(5);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        assertEquals(invitedUser, ((DeviceOwnerMetaField) createdDevice.metaFields[0]).value);

        client.deleteUser(orgId, invitedUser);
        client.verifyResult(ok(6));

        client.getDevice(orgId, createdDevice.id);
        createdDevice = client.parseDevice(7);
        assertNotNull(createdDevice);
        assertNotNull(createdDevice.metaFields);
        assertEquals(2, createdDevice.metaFields.length);
        assertNull(((DeviceOwnerMetaField) createdDevice.metaFields[0]).value);
    }

    @Test
    public void doNotAllowProductRemovalIfItHasSubProducts() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("New Sub Org111", "Some TimeZone", "/static/logo.png", false, -1);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductOrg);
        assertEquals(orgId, fromApiProductOrg.parentId);
        assertEquals(organization.name, fromApiProductOrg.name);
        assertEquals(organization.tzName, fromApiProductOrg.tzName);
        assertNotNull(fromApiProductOrg.products);
        assertEquals(1, fromApiProductOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiProductOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg.products[0].parentId);

        client.canDeleteProduct(fromApiProduct.id);
        client.verifyResult(webJson(3, "You can't delete product that is used in sub organizations."));

        client.canDeleteProduct(fromApiProduct.id);
        client.verifyResult(webJson(4, "You can't delete product that is used in sub organizations."));

        client.canDeleteProduct(fromApiProduct.id);
        client.verifyResult(webJson(5, "You can't delete product that is used in sub organizations."));

        client.deleteProduct(fromApiProduct.id);
        client.verifyResult(webJson(6, "You can't delete product that is used in sub organizations."));
    }

    @Test
    public void userCanUnselectProduct() throws Exception {
        AppWebSocketClient client = loggedDefaultClient("super@blynk.cc", "1");

        Product product = new Product();
        product.name = "My product";
        product.metaFields = new MetaField[] {
                createDeviceNameMeta(1, "Device Name", "123", true),
                createDeviceOwnerMeta(2, "Owner", "123", true)
        };

        WebLabel webLabel = new WebLabel();
        webLabel.label = "123";
        webLabel.id = 2;
        webLabel.x = 4;
        webLabel.y = 2;
        webLabel.height = 10;
        webLabel.width = 20;
        webLabel.sources = new WebSource[] {
                new WebSource("some Label", "#334455",
                        false, RAW_DATA, new DataStream((byte) 2, PinType.VIRTUAL),
                        null,
                        null,
                        null, SortOrder.ASC, 10, false, null, false)
        };

        product.webDashboard = new WebDashboard(new Widget[] {
                webLabel
        });

        client.createProduct(product);
        ProductDTO fromApiProduct = client.parseProductDTO(1);
        assertNotNull(fromApiProduct);

        Organization organization = new Organization("userCanUnselectProduct", "Some TimeZone", "/static/logo.png", false, -1);
        organization.selectedProducts = new int[] {fromApiProduct.id};

        client.createOrganization(organization);
        OrganizationDTO fromApiProductOrg = client.parseOrganizationDTO(2);
        assertNotNull(fromApiProductOrg);
        assertEquals(orgId, fromApiProductOrg.parentId);
        assertEquals(organization.name, fromApiProductOrg.name);
        assertEquals(organization.tzName, fromApiProductOrg.tzName);
        assertNotNull(fromApiProductOrg.products);
        assertEquals(1, fromApiProductOrg.products.length);
        assertEquals(fromApiProduct.id + 1, fromApiProductOrg.products[0].id);
        assertEquals(fromApiProduct.id, fromApiProductOrg.products[0].parentId);

        client.canDeleteProduct(fromApiProductOrg.products[0].id);
        client.verifyResult(ok(3));
    }
}
