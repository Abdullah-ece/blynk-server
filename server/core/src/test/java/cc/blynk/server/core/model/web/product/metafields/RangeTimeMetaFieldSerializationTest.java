package cc.blynk.server.core.model.web.product.metafields;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Role;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class RangeTimeMetaFieldSerializationTest {

    @Test
    public void testSerialization() throws Exception {
        RangeTimeMetaField rangeTimeMetaField = new RangeTimeMetaField(1, "name",
                Role.ADMIN, false,  null, LocalTime.ofSecondOfDay(1), LocalTime.ofSecondOfDay(100));

        String s = JsonParser.MAPPER.writeValueAsString(rangeTimeMetaField);
        assertNotNull(s);
        assertEquals("{\"type\":\"Range\",\"id\":1,\"name\":\"name\","
                + "\"role\":\"ADMIN\",\"isDefault\":false,\"from\":1,\"to\":100}", s);
        RangeTimeMetaField result = JsonParser.MAPPER.readValue(s, RangeTimeMetaField.class);
        assertNotNull(result);
        assertEquals(LocalTime.ofSecondOfDay(1), result.from);
        assertEquals(LocalTime.ofSecondOfDay(100), result.to);
    }

}
