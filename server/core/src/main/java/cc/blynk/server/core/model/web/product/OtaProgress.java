package cc.blynk.server.core.model.web.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OtaProgress {

    public final String title;

    public final String pathToFirmware;

    public final String firmwareOriginalFileName;

    public final long startedAt;

    public final long finishedAt;

    public final int[] deviceIds;

    public final FirmwareInfo firmwareInfo;

    public final int attempts;

    @JsonCreator
    public OtaProgress(@JsonProperty("title") String title,
                       @JsonProperty("pathToFirmware") String pathToFirmware,
                       @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                       @JsonProperty("startedAt") long startedAt,
                       @JsonProperty("finishedAt") long finishedAt,
                       @JsonProperty("deviceIds") int[] deviceIds,
                       @JsonProperty("firmwareInfo") FirmwareInfo firmwareInfo,
                       @JsonProperty("attempts") int attempts) {
        this.title = title;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.deviceIds = deviceIds;
        this.firmwareInfo = firmwareInfo;
        this.attempts = attempts;
    }
}
