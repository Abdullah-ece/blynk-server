package cc.blynk.server.common.handlers.logic.timeline;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.12.18.
 */
public class ResolveEventDTO {

    public final int deviceId;

    public final long logEventId;

    public final String resolveComment;

    @JsonCreator
    public ResolveEventDTO(@JsonProperty("deviceId") int deviceId,
                           @JsonProperty("logEventId") long logEventId,
                           @JsonProperty("resolveComment") String resolveComment) {
        this.deviceId = deviceId;
        this.logEventId = logEventId;
        this.resolveComment = resolveComment;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
