package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static cc.blynk.utils.StringUtils.DEVICE_SEPARATOR;

public final class Shipment {

    public static final int DEFAULT_OTA_STATUS_MSG_ID = 1;

    private static final int DEFAULT_OTA_SHIPMENT_STATUS_MSG_ID = 0;

    public static final Shipment[] EMPTY_SHIPMENTS = {};

    public final int id;

    public final int productId;

    public final String title;

    public final String pathToFirmware;

    public final String firmwareOriginalFileName;

    public final String startedBy;

    public final long startedAt;

    public final long finishedAt;

    public final int[] deviceIds;

    public final FirmwareInfo firmwareInfo;

    public final int attemptsLimit;

    public final boolean isSecure;

    public final ShipmentStatus status;

    public final ShipmentProgress shipmentProgress;

    @JsonCreator
    public Shipment(@JsonProperty("id") int id,
                    @JsonProperty("productId") int productId,
                    @JsonProperty("title") String title,
                    @JsonProperty("pathToFirmware") String pathToFirmware,
                    @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                    @JsonProperty("startedBy") String startedBy,
                    @JsonProperty("startedAt") long startedAt,
                    @JsonProperty("finishedAt") long finishedAt,
                    @JsonProperty("deviceIds") int[] deviceIds,
                    @JsonProperty("firmwareInfo") FirmwareInfo firmwareInfo,
                    @JsonProperty("attemptsLimit") int attemptsLimit,
                    @JsonProperty("isSecure") boolean isSecure,
                    @JsonProperty("status") ShipmentStatus status,
                    @JsonProperty("shipmentProgress") ShipmentProgress shipmentProgress) {
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.startedBy = startedBy;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.deviceIds = deviceIds;
        this.firmwareInfo = firmwareInfo;
        this.attemptsLimit = attemptsLimit;
        this.isSecure = isSecure;
        this.status = status == null ? ShipmentStatus.RUN : status;
        this.shipmentProgress = (shipmentProgress == null)
                ? new ShipmentProgress()
                : shipmentProgress;
    }

    public Shipment(ShipmentDTO shipmentDTO, String startedBy, long now, ShipmentProgress shipmentProgress) {
        this(shipmentDTO.id,
                shipmentDTO.productId,
                shipmentDTO.title,
                shipmentDTO.pathToFirmware,
                shipmentDTO.firmwareOriginalFileName,
                startedBy,
                now,
                -1,
                shipmentDTO.deviceIds,
                shipmentDTO.firmwareInfo,
                shipmentDTO.attemptsLimit,
                shipmentDTO.isSecure(),
                ShipmentStatus.RUN,
                (shipmentProgress == null)
                        ? new ShipmentProgress()
                        : shipmentProgress
        );
    }

    public Shipment(ShipmentDTO shipmentDTO, String startedBy, long now) {
        this(shipmentDTO.id,
                shipmentDTO.productId,
                shipmentDTO.title,
                shipmentDTO.pathToFirmware,
                shipmentDTO.firmwareOriginalFileName,
                startedBy,
                now,
                -1,
                shipmentDTO.deviceIds,
                shipmentDTO.firmwareInfo,
                shipmentDTO.attemptsLimit,
                shipmentDTO.isSecure(),
                ShipmentStatus.RUN,
                shipmentDTO.shipmentProgress
        );
    }

    public Shipment(Shipment shipment, ShipmentStatus status) {
        this(shipment.id,
                shipment.productId,
                shipment.title,
                shipment.pathToFirmware,
                shipment.firmwareOriginalFileName,
                shipment.startedBy,
                shipment.startedAt,
                shipment.finishedAt,
                shipment.deviceIds,
                shipment.firmwareInfo,
                shipment.attemptsLimit,
                shipment.isSecure,
                status,
                shipment.shipmentProgress
        );
    }

    public void success(Session session, int deviceId) {
        shipmentProgress.success.incrementAndGet();
        checkProgressAndSendMessageIfFinished(session, deviceId);
    }

    public void failure(Session session, int deviceId) {
        shipmentProgress.failure.incrementAndGet();
        checkProgressAndSendMessageIfFinished(session, deviceId);
    }

    public void downloadLimitReached(Session session, int deviceId) {
        shipmentProgress.downloadLimitReached.incrementAndGet();
        checkProgressAndSendMessageIfFinished(session, deviceId);
    }

    private void checkProgressAndSendMessageIfFinished(Session session, int deviceId) {
        if (shipmentProgress.isFinished(deviceIds.length)) {
            session.sendToUserOnWeb(DEFAULT_OTA_SHIPMENT_STATUS_MSG_ID, startedBy,
                    createOTAStatusMessage(id, deviceId, ShipmentStatus.FINISH));
        }
    }

    private static String createOTAStatusMessage(int shipmentId, int deviceId, ShipmentStatus shipmentStatus) {
        return "" + shipmentId + DEVICE_SEPARATOR + deviceId + BODY_SEPARATOR + shipmentStatus;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
