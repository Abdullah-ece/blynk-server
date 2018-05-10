package cc.blynk.server.core.model.web.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class FirmwareInfo {

    public final String version;

    public final String boardType;

    public final String buildDate;

    public final String md5Hash;

    @JsonCreator
    public FirmwareInfo(@JsonProperty("version") String version,
                        @JsonProperty("boardType") String boardType,
                        @JsonProperty("buildDate") String buildDate,
                        @JsonProperty("md5Hash") String md5Hash) {
        this.version = version;
        this.boardType = boardType;
        this.buildDate = buildDate;
        this.md5Hash = md5Hash;
    }

    public FirmwareInfo(Map<String, String> entires) {
        this(entires.get("ver"),
                entires.get("dev"),
                entires.get("build"),
                entires.get("MD5"));
    }
}
