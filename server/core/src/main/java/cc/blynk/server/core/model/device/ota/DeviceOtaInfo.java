package cc.blynk.server.core.model.device.ota;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.08.17.
 */
public final class DeviceOtaInfo {

    public final int shipmentId;

    public final String otaStartedBy;

    public final long otaStartedAt;

    public final long requestSentAt;

    public final long firmwareRequestedAt;

    public final long firmwareUploadedAt;

    public final long finishedAt;

    public final String pathToFirmware;

    public final String buildDate;

    public final OTADeviceStatus status;

    public final int attempts;

    public final int attemptsLimit;

    @JsonCreator
    public DeviceOtaInfo(@JsonProperty("shipmentId") int shipmentId,
                         @JsonProperty("otaStartedBy") String otaStartedBy,
                         @JsonProperty("otaStartedAt") long otaStartedAt,
                         @JsonProperty("requestSentAt") long requestSentAt,
                         @JsonProperty("firmwareRequestedAt") long firmwareRequestedAt,
                         @JsonProperty("firmwareUploadedAt") long firmwareUploadedAt,
                         @JsonProperty("finishedAt") long finishedAt,
                         @JsonProperty("pathToFirmware") String pathToFirmware,
                         @JsonProperty("buildDate") String buildDate,
                         @JsonProperty("status") OTADeviceStatus status,
                         @JsonProperty("attempts") int attempts,
                         @JsonProperty("attemptsLimit") int attemptsLimit) {
        this.shipmentId = shipmentId;
        this.otaStartedBy = otaStartedBy;
        this.otaStartedAt = otaStartedAt;
        this.requestSentAt = requestSentAt;
        this.firmwareRequestedAt = firmwareRequestedAt;
        this.firmwareUploadedAt = firmwareUploadedAt;
        this.finishedAt = finishedAt;
        this.pathToFirmware = pathToFirmware;
        this.buildDate = buildDate;
        this.status = status;
        this.attempts = attempts;
        this.attemptsLimit = attemptsLimit;
    }

    public DeviceOtaInfo(DeviceOtaInfo prev,
                         long finishedAt,
                         OTADeviceStatus status) {
        this(prev.shipmentId, prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, prev.firmwareRequestedAt, prev.firmwareUploadedAt, finishedAt,
                prev.pathToFirmware, prev.buildDate,
                status,
                prev.attempts, prev.attemptsLimit);

    }

    public DeviceOtaInfo(DeviceOtaInfo prev,
                         long firmwareRequestedAt,
                         long firmwareUploadedAt,
                         long finishedAt,
                         OTADeviceStatus status,
                         int attempts) {
        this(prev.shipmentId, prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, firmwareRequestedAt, firmwareUploadedAt, finishedAt,
                prev.pathToFirmware, prev.buildDate,
                status, attempts, prev.attemptsLimit);

    }

    public DeviceOtaInfo(DeviceOtaInfo prev,
                         long firmwareUploadedAt,
                         long finishedAt,
                         OTADeviceStatus status) {
        this(prev.shipmentId, prev.otaStartedBy, prev.otaStartedAt,
                prev.requestSentAt, prev.firmwareRequestedAt, firmwareUploadedAt, finishedAt,
                prev.pathToFirmware, prev.buildDate,
                status, prev.attempts, prev.attemptsLimit);
    }

    public boolean isLimitReached() {
        return attempts >= attemptsLimit;
    }

}
