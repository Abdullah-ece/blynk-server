package cc.blynk.server.core.model.device.ota;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.08.17.
 */
public class DeviceOtaInfo {

    public final String otaStartedBy;

    public final long otaStartedAt;

    public final long requestSentAt;

    public final long firmwareRequestedAt;

    public final long firmwareUploadedAt;

    public final long finishedAt;

    public final String pathToFirmware;

    public final String buildDate;

    public final OTAStatus otaStatus;

    public final int attempts;

    public final int attemptsLimit;

    public final boolean isSecure;

    @JsonCreator
    public DeviceOtaInfo(@JsonProperty("otaStartedBy") String otaStartedBy,
                         @JsonProperty("otaStartedAt") long otaStartedAt,
                         @JsonProperty("requestSentAt") long requestSentAt,
                         @JsonProperty("firmwareRequestedAt") long firmwareRequestedAt,
                         @JsonProperty("firmwareUploadedAt") long firmwareUploadedAt,
                         @JsonProperty("finishedAt") long finishedAt,
                         @JsonProperty("pathToFirmware") String pathToFirmware,
                         @JsonProperty("buildDate") String buildDate,
                         @JsonProperty("otaStatus") OTAStatus otaStatus,
                         @JsonProperty("attempts") int attempts,
                         @JsonProperty("attemptsLimit") int attemptsLimit,
                         @JsonProperty("isSecure") boolean isSecure) {
        this.otaStartedBy = otaStartedBy;
        this.otaStartedAt = otaStartedAt;
        this.requestSentAt = requestSentAt;
        this.firmwareRequestedAt = firmwareRequestedAt;
        this.firmwareUploadedAt = firmwareUploadedAt;
        this.finishedAt = finishedAt;
        this.pathToFirmware = pathToFirmware;
        this.buildDate = buildDate;
        this.otaStatus = otaStatus;
        this.attempts = attempts;
        this.attemptsLimit = attemptsLimit;
        this.isSecure = isSecure;
    }

    public boolean isLimitReached() {
        return attempts > attemptsLimit;
    }

}
