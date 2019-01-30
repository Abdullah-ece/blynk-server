package cc.blynk.server.web.handlers.logic.organization.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 25.01.19.
 */
public final class DeviceCountDTO {

    public final int deviceCount;

    public final int subDeviceCount;

    @JsonCreator
    public DeviceCountDTO(@JsonProperty("deviceCount") int deviceCount,
                    @JsonProperty("subDeviceCount") int subDeviceCount) {
        this.deviceCount = deviceCount;
        this.subDeviceCount = subDeviceCount;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
