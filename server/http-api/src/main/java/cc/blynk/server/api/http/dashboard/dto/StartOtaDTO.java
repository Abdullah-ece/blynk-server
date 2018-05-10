package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.FirmwareInfo;
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

    public final boolean checkBoardType;

    public final FirmwareInfo firmwareInfo;

    public final int attemptsLimit;

    @JsonCreator
    public StartOtaDTO(@JsonProperty("productId") int productId,
                       @JsonProperty("pathToFirmware") String pathToFirmware,
                       @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                       @JsonProperty("deviceIds") int[] deviceIds,
                       @JsonProperty("title") String title,
                       @JsonProperty("checkBoardType") boolean checkBoardType,
                       @JsonProperty("firmwareInfo") FirmwareInfo firmwareInfo,
                       @JsonProperty("attemptsLimit") int attemptsLimit) {
        this.productId = productId;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.deviceIds = deviceIds;
        this.title = title;
        this.checkBoardType = checkBoardType;
        this.firmwareInfo = firmwareInfo;
        this.attemptsLimit = attemptsLimit;
    }

    public boolean isNotValid() {
        return pathToFirmware == null || firmwareInfo == null || firmwareOriginalFileName == null
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
