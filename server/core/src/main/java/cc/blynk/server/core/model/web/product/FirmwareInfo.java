package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static cc.blynk.utils.FileUtils.BOARD_TYPE;
import static cc.blynk.utils.FileUtils.BUILD;
import static cc.blynk.utils.FileUtils.MD5;
import static cc.blynk.utils.FileUtils.VERSION;

public final class FirmwareInfo {

    public final String version;

    public final BoardType boardType;

    public final String buildDate;

    public final String md5Hash;

    @JsonCreator
    public FirmwareInfo(@JsonProperty("version") String version,
                        @JsonProperty("boardType") BoardType boardType,
                        @JsonProperty("buildDate") String buildDate,
                        @JsonProperty("md5Hash") String md5Hash) {
        this.version = version;
        this.boardType = boardType;
        this.buildDate = buildDate;
        this.md5Hash = md5Hash;
    }

    public FirmwareInfo(Map<String, String> entires) {
        this(entires.get(VERSION),
                BoardType.fromLabel(entires.get(BOARD_TYPE)),
                entires.get(BUILD),
                entires.get(MD5));
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
