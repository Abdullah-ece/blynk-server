package cc.blynk.server.core.model.web.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class OtaProgress {

    public final String title;

    public final String pathToFirmware;

    public final String firmwareOriginalFileName;

    public final long startedAt;

    public final int[] deviceIds;

    public final Map<String, String> firmwareInfo;

    @JsonCreator
    public OtaProgress(@JsonProperty("title") String title,
                       @JsonProperty("pathToFirmware") String pathToFirmware,
                       @JsonProperty("firmwareOriginalFileName") String firmwareOriginalFileName,
                       @JsonProperty("startedAt") long startedAt,
                       @JsonProperty("deviceIds") int[] deviceIds,
                       @JsonProperty("firmwareInfo") Map<String, String> firmwareInfo) {
        this.title = title;
        this.pathToFirmware = pathToFirmware;
        this.firmwareOriginalFileName = firmwareOriginalFileName;
        this.startedAt = startedAt;
        this.deviceIds = deviceIds;
        this.firmwareInfo = firmwareInfo;
    }
}
