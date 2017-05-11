package cc.blynk.server.http.web;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.DataStream;
import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.web.product.events.CriticalEvent;
import cc.blynk.server.core.model.web.product.events.InformationEvent;
import cc.blynk.server.core.model.web.product.events.OnlineEvent;
import cc.blynk.server.core.model.web.product.events.WarningEvent;
import cc.blynk.server.core.model.web.product.metafields.*;
import cc.blynk.utils.JsonParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Date;

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
                new TextMetaField("My Farm", Role.ADMIN, "Farm of Smith"),
                new RangeMetaField("Farm of Smith", Role.ADMIN, 60, 120),
                new NumberMetaField("Farm of Smith", Role.ADMIN, 10.222),
                new MeasurementUnitMetaField("Farm of Smith", Role.ADMIN, MeasurementUnit.Celsius, "36"),
                new CostMetaField("Farm of Smith", Role.ADMIN, Currency.getInstance("USD"), 9.99, 1, MeasurementUnit.Gallon),
                new ContactMetaField("Farm of Smith", Role.ADMIN, "Tech Support",
                        "Dmitriy", "Dumanskiy", "dmitriy@blynk.cc", "+38063673333", "My street",
                        "Kyiv", "Ukraine", "03322"),
                new AddressMetaField("Farm of Smith", Role.ADMIN, "My street",
                        "San Diego", "CA", "03322", "US"),
                new CoordinatesMetaField("Farm Location", Role.ADMIN, 22.222, 23.333),
                new TimeMetaField("Some Time", Role.ADMIN, new Date())
        };

        product.dataStreams = new DataStream[] {
                new DataStream("Temperature", MeasurementUnit.Celsius, 0, 50, (byte) 0)
        };

        OnlineEvent onlineEvent = new OnlineEvent();
        onlineEvent.name = "Your device is online.";
        onlineEvent.emailNotifications = new int[]{1};
        onlineEvent.pushNotifications = new int[]{1};
        onlineEvent.smsNotifications = new int[]{1};

        OnlineEvent offlineEvent = new OnlineEvent();
        offlineEvent.name = "Your device is offline.";
        offlineEvent.emailNotifications = new int[]{1};
        offlineEvent.pushNotifications = new int[]{1};
        offlineEvent.smsNotifications = new int[]{1};

        InformationEvent infoEvent = new InformationEvent();
        infoEvent.name = "Door is opened";
        infoEvent.eventCode = "door_opened";
        infoEvent.description = "Kitchen door is opened.";
        infoEvent.emailNotifications = new int[]{1};
        infoEvent.pushNotifications = new int[]{1};
        infoEvent.smsNotifications = new int[]{1};

        WarningEvent warningEvent = new WarningEvent();
        warningEvent.name = "Temperature is high!";
        warningEvent.eventCode = "temp_is_high";
        warningEvent.description = "Room temp is high";
        warningEvent.emailNotifications = new int[]{1};
        warningEvent.pushNotifications = new int[]{1};
        warningEvent.smsNotifications = new int[]{1};

        CriticalEvent criticalEvent = new CriticalEvent();
        criticalEvent.name = "Temperature is super high!";
        criticalEvent.eventCode = "temp_is_super_high";
        criticalEvent.description = "Room temp is super high";
        criticalEvent.emailNotifications = new int[]{1};
        criticalEvent.pushNotifications = new int[]{1};
        criticalEvent.smsNotifications = new int[]{1};


        product.events = new Event[] {
                onlineEvent,
                offlineEvent,
                infoEvent,
                warningEvent,
                criticalEvent
        };

        System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product));
    }

}
