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

    public final long requestSentAt;

    public final long firmwareRequestedAt;

    public final long firmwareUploadedAt;

    public final long finishedAt;

    public final String pathToFirmware;

    public final OTADeviceStatus status;

    public final int attempts;

    @JsonCreator
    public DeviceOtaInfo(@JsonProperty("shipmentId") int shipmentId,
                         @JsonProperty("requestSentAt") long requestSentAt,
                         @JsonProperty("firmwareRequestedAt") long firmwareRequestedAt,
                         @JsonProperty("firmwareUploadedAt") long firmwareUploadedAt,
                         @JsonProperty("finishedAt") long finishedAt,
                         @JsonProperty("pathToFirmware") String pathToFirmware,
                         @JsonProperty("status") OTADeviceStatus status,
                         @JsonProperty("attempts") int attempts) {
        this.shipmentId = shipmentId;
        this.requestSentAt = requestSentAt;
        this.firmwareRequestedAt = firmwareRequestedAt;
        this.firmwareUploadedAt = firmwareUploadedAt;
        this.finishedAt = finishedAt;
        this.pathToFirmware = pathToFirmware;
        this.status = status;
        this.attempts = attempts;
    }

    public DeviceOtaInfo(DeviceOtaInfo prev,
                         long finishedAt,
                         OTADeviceStatus status) {
        this(prev.shipmentId,
                prev.requestSentAt, prev.firmwareRequestedAt, prev.firmwareUploadedAt, finishedAt,
                prev.pathToFirmware,
                status,
                prev.attempts);

    }

    public DeviceOtaInfo(DeviceOtaInfo prev,
                         long firmwareRequestedAt,
                         long firmwareUploadedAt,
                         long finishedAt,
                         OTADeviceStatus status,
                         int attempts) {
        this(prev.shipmentId,
                prev.requestSentAt, firmwareRequestedAt, firmwareUploadedAt, finishedAt,
                prev.pathToFirmware,
                status, attempts);

    }

    public DeviceOtaInfo(DeviceOtaInfo prev,
                         long firmwareUploadedAt,
                         long finishedAt,
                         OTADeviceStatus status) {
        this(prev.shipmentId,
                prev.requestSentAt, prev.firmwareRequestedAt, firmwareUploadedAt, finishedAt,
                prev.pathToFirmware,
                status, prev.attempts);
    }

    public boolean isLimitReached(int attemptsLimit) {
        return this.attempts >= attemptsLimit;
    }

}
