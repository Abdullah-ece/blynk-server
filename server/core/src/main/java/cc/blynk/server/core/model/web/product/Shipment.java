package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.dto.OtaDTO;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Shipment {

    public static final Shipment[] EMPTY_SHIPMENTS = {};

    public final int id;

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

    public Shipment(OtaDTO otaDTO, long now) {
        this(otaDTO.id,
                otaDTO.title,
                otaDTO.pathToFirmware,
                otaDTO.firmwareOriginalFileName,
                now,
                -1,
                otaDTO.deviceIds,
                otaDTO.firmwareInfo,
                otaDTO.attemptsLimit,
                otaDTO.isSecure,
                ShipmentStatus.RUN
        );
    }

    public Shipment(Shipment shipment, ShipmentStatus status) {
        this(shipment.id,
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
