package cc.blynk.server.core.model.device.ota;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.08.17.
 */
public class DeviceOtaInfo {

    public final String otaInitiatedBy;

    public final long otaInitiatedAt;

    public final long requestSentAt;

    public final long finishedAt;

    public final String pathToFirmware;

    public final String buildDate;

    public volatile OTAStatus otaStatus;

    @JsonCreator
    public DeviceOtaInfo(@JsonProperty("otaInitiatedBy") String otaInitiatedBy,
                         @JsonProperty("otaInitiatedAt") long otaInitiatedAt,
                         @JsonProperty("requestSentAt") long requestSentAt,
                         @JsonProperty("finishedAt") long finishedAt,
                         @JsonProperty("pathToFirmware") String pathToFirmware,
                         @JsonProperty("buildDate") String buildDate,
                         @JsonProperty("otaStatus") OTAStatus otaStatus) {
        this.otaInitiatedBy = otaInitiatedBy;
        this.otaInitiatedAt = otaInitiatedAt;
        this.requestSentAt = requestSentAt;
        this.finishedAt = finishedAt;
        this.pathToFirmware = pathToFirmware;
        this.buildDate = buildDate;
        this.otaStatus = otaStatus;
    }

}
