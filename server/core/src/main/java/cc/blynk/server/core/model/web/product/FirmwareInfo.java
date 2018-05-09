package cc.blynk.server.core.model.web.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class FirmwareInfo {

    public final String version;

    public final String boardType;

    public final String buildDate;

    public final int bufferIn;

    public final int heartbeatInterval;

    @JsonCreator
    public FirmwareInfo(@JsonProperty("version") String version,
                        @JsonProperty("boardType") String boardType,
                        @JsonProperty("buildDate") String buildDate,
                        @JsonProperty("bufferIn") int bufferIn,
                        @JsonProperty("heartbeatInterval") int heartbeatInterval) {
        this.version = version;
        this.boardType = boardType;
        this.buildDate = buildDate;
        this.bufferIn = bufferIn;
        this.heartbeatInterval = heartbeatInterval;
    }

    public FirmwareInfo(Map<String, String> entires) {
        this(entires.get("ver"), entires.get("dev"),
                entires.get("build"),
                Integer.parseInt(entires.get("buff-in")),
                Integer.parseInt(entires.get("h-beat"))
                );
    }
}
