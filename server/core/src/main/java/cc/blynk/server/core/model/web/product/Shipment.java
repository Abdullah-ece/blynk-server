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
                    @JsonProperty("isSecure") boolean isSecure) {
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
                otaDTO.isSecure
        );
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
