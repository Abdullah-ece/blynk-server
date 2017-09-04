package cc.blynk.server.core.model;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.utils.JsonParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


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
        assertEquals("{\"id\":0,\"productId\":-1,\"status\":\"OFFLINE\",\"disconnectTime\":0,\"dataReceivedAt\":0,\"orgName\":\"OrgName\"}", json);
        System.out.println(json);
    }

}
