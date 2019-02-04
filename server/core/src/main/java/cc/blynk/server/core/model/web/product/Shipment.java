package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.dto.ShipmentDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Shipment {

    public static final Shipment[] EMPTY_SHIPMENTS = {};

    public final int id;

    public final int productId;

    public final String title;

    public final String pathToFirmware;

    public final String firmwareOriginalFileName;

    public final long startedAt;

    public final long finishedAt;

    public final int[] deviceIds;

    public final FirmwareInfo firmwareInfo;

    public final int attempts;

    public final boolean isSecure;

    public final ShipmentStatus status;

    @JsonCreator
    public Shipment(@JsonProperty("id") int id,
                    @JsonProperty("productId") int productId,
                    @JsonProperty("title") String title,
                    @JsonProperty("pathToFirmware") String pathToFirmware,
                    @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                    @JsonProperty("startedAt") long startedAt,
                    @JsonProperty("finishedAt") long finishedAt,
                    @JsonProperty("deviceIds") int[] deviceIds,
                    @JsonProperty("firmwareInfo") FirmwareInfo firmwareInfo,
                    @JsonProperty("attempts") int attempts,
                    @JsonProperty("isSecure") boolean isSecure,
                    @JsonProperty("status") ShipmentStatus status) {
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.deviceIds = deviceIds;
        this.firmwareInfo = firmwareInfo;
        this.attempts = attempts;
        this.isSecure = isSecure;
        this.status = status == null ? ShipmentStatus.RUN : status;
    }

    public Shipment(ShipmentDTO shipmentDTO, boolean isSecure, long now) {
        this(shipmentDTO.id,
                shipmentDTO.productId,
                shipmentDTO.title,
                shipmentDTO.pathToFirmware,
                shipmentDTO.firmwareOriginalFileName,
                now,
                -1,
                shipmentDTO.deviceIds,
                shipmentDTO.firmwareInfo,
                shipmentDTO.attemptsLimit,
                isSecure,
                ShipmentStatus.RUN
        );
    }

    public Shipment(Shipment shipment, ShipmentStatus status) {
        this(shipment.id,
                shipment.productId,
                shipment.title,
                shipment.pathToFirmware,
                shipment.firmwareOriginalFileName,
                shipment.startedAt,
                shipment.finishedAt,
                shipment.deviceIds,
                shipment.firmwareInfo,
                shipment.attempts,
                shipment.isSecure,
                status
        );
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
