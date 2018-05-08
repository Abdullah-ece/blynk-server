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

    public final String firmwareOriginalFileName;

    public final int[] deviceIds;

    public final String title;

    @JsonCreator
    public StartOtaDTO(@JsonProperty("productId") int productId,
                       @JsonProperty("pathToFirmware") String pathToFirmware,
                       @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                       @JsonProperty("deviceIds") int[] deviceIds,
                       @JsonProperty("title") String title) {
        this.productId = productId;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.deviceIds = deviceIds;
        this.title = title;
    }

    public boolean isNotValid() {
        return pathToFirmware == null || firmwareOriginalFileName == null
                || pathToFirmware.isEmpty() || isDevicesEmpty();
    }

    public boolean isDevicesEmpty() {
        return deviceIds == null || deviceIds.length == 0;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
