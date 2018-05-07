package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.05.18.
 */
public class StartOtaDTO {

    public final int productId;

    public final String pathToFirmware;

    public final int[] deviceIds;

    @JsonCreator
    public StartOtaDTO(@JsonProperty("productId") int productId,
                       @JsonProperty("pathToFirmware") String pathToFirmware,
                       @JsonProperty("deviceIds") int[] deviceIds) {
        this.productId = productId;
        this.pathToFirmware = pathToFirmware;
        this.deviceIds = deviceIds;
    }

    public boolean isNotValid() {
        return pathToFirmware == null || pathToFirmware.isEmpty() || deviceIds == null || deviceIds.length == 0;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
