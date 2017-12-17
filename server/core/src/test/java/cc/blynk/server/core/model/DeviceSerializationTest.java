package cc.blynk.server.core.model;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.06.17.
 */
public class DeviceSerializationTest {

    @Test
    public void testAddDynamicProperty() {
        Device device = new Device();

        String json = ((ObjectNode) JsonParser.MAPPER.valueToTree(device)).put("orgName", "OrgName").toString();
        assertTrue(json.contains("orgName"));
        assertTrue(json.contains("OrgName"));
    }

}
