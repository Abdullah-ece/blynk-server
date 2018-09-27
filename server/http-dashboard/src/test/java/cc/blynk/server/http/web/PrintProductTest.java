package cc.blynk.server.http.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.MetadataType;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.CriticalEvent;
import cc.blynk.server.core.model.web.product.events.Event;
import cc.blynk.server.core.model.web.product.events.InformationEvent;
import cc.blynk.server.core.model.web.product.events.OfflineEvent;
import cc.blynk.server.core.model.web.product.events.OnlineEvent;
import cc.blynk.server.core.model.web.product.events.WarningEvent;
import cc.blynk.server.core.model.web.product.metafields.AddressMetaField;
import cc.blynk.server.core.model.web.product.metafields.ContactMetaField;
import cc.blynk.server.core.model.web.product.metafields.CoordinatesMetaField;
import cc.blynk.server.core.model.web.product.metafields.CostMetaField;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnitMetaField;
import cc.blynk.server.core.model.web.product.metafields.NumberMetaField;
import cc.blynk.server.core.model.web.product.metafields.RangeTimeMetaField;
import cc.blynk.server.core.model.web.product.metafields.TextMetaField;
import cc.blynk.server.core.model.web.product.metafields.TimeMetaField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Date;

import static java.time.LocalTime.ofSecondOfDay;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 5/8/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PrintProductTest {

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
                new TextMetaField(1, "My Farm", Role.ADMIN, false, false, false, null, "Farm of Smith"),
                new RangeTimeMetaField(2, "Farm of Smith", Role.ADMIN, false, false, false, null, ofSecondOfDay(60),  ofSecondOfDay(120)),
                new NumberMetaField(3, "Farm of Smith", Role.ADMIN, false, false, false, null, 0, 100, 10.222, 1),
                new MeasurementUnitMetaField(4, "Farm of Smith", Role.ADMIN, false, false, false, null, MeasurementUnit.Celsius, 36, 0, 100),
                new CostMetaField(5, "Farm of Smith", Role.ADMIN, false, false, false, null, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon, 0, 100),
                new ContactMetaField(6, "Farm of Smith", Role.ADMIN, false, false, false, "Tech Support",
                        "Dmitriy", false, "Dumanskiy", false, "dmitriy@blynk.cc", false,
                        "+38063673333",  false, "My street", false, "Ukraine", false,
                        "Kyiv", false, "Ukraine", false, "03322", false, false),
                new AddressMetaField(7, "Farm of Smith", Role.ADMIN, false, false, false, null, "My street", false,
                        "San Diego", false, "CA", false, "03322", false, "US", false, false),
                new CoordinatesMetaField(8, "Farm Location", Role.ADMIN, false, false, false, null, 22.222, 23.333),
                new TimeMetaField(9,"Some Time", Role.ADMIN, false, false, false, null, new Date().getTime())
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

}
