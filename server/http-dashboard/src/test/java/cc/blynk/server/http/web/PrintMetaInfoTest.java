package cc.blynk.server.http.web;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.web.Role;
import cc.blynk.server.core.model.web.product.DataStream;
import cc.blynk.server.core.model.web.product.Product;
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
public class PrintMetaInfoTest {

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

        System.out.println(JsonParser.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product));
    }

}
