package cc.blynk.server.core.model.device.ota;

public enum ShipmentDeviceStatus {

    STARTED,
    REQUEST_SENT,
    FIRMWARE_REQUESTED,
    FIRMWARE_UPLOADED,
    SUCCESS,
    FAILURE,
    DOWNLOAD_LIMIT_REACHED;

    public boolean isNotFailure() {
        return this != FAILURE && this != DOWNLOAD_LIMIT_REACHED;
    }

    public boolean inProgress() {
        return this == STARTED || this == REQUEST_SENT
                || this == FIRMWARE_REQUESTED || this == FIRMWARE_UPLOADED;
    }

}
