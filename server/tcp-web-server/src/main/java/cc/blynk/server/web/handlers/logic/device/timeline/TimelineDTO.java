package cc.blynk.server.web.handlers.logic.device.timeline;

import cc.blynk.server.core.model.web.product.EventType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TimelineDTO {

    public final int orgId;
    public final int deviceId;
    public final EventType eventType;
    public final Boolean isResolved;
    public final long from;
    public final long to;
    public final int offset;
    public final int limit;

    @JsonCreator
    public TimelineDTO(@JsonProperty("orgId") int orgId,
                       @JsonProperty("deviceId") int deviceId,
                       @JsonProperty("eventType") EventType eventType,
                       @JsonProperty("isResolved") Boolean isResolved,
                       @JsonProperty("from") long from,
                       @JsonProperty("to") long to,
                       @JsonProperty("offset") int offset,
                       @JsonProperty("limit") int limit) {
        this.orgId = orgId;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.isResolved = isResolved;
        this.from = from;
        this.to = to;
        this.offset = offset;
        this.limit = limit;
    }
}
