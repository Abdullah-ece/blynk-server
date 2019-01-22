package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.events.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 11.01.19.
 */
public class LogEventDTO {

    public final long id;

    public final int deviceId;

    public final EventType eventType;

    public final long ts;

    public final int eventHashcode;

    public final boolean isResolved;

    public final String resolvedBy;

    public final long resolvedAt;

    public final String resolvedComment;

    public String name;

    public String description;

    public LogEventDTO(long id, int deviceId, int eventType, long ts,
                       int eventHashcode, String description, boolean isResolved,
                       String resolvedBy, long resolvedAt, String resolvedComment) {
        this.id = id;
        this.deviceId = deviceId;
        this.eventType = EventType.values()[eventType];
        this.ts = ts;
        this.eventHashcode = eventHashcode;
        this.description = description;
        this.isResolved = isResolved;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
        this.resolvedComment = resolvedComment;
    }

    @JsonCreator
    public LogEventDTO(@JsonProperty("id") long id,
                       @JsonProperty("deviceId") int deviceId,
                       @JsonProperty("eventType") EventType eventType,
                       @JsonProperty("ts") long ts,
                       @JsonProperty("eventHashcode") int eventHashcode,
                       @JsonProperty("description") String description,
                       @JsonProperty("isResolved") boolean isResolved,
                       @JsonProperty("resolvedBy") String resolvedBy,
                       @JsonProperty("resolvedAt") long resolvedAt,
                       @JsonProperty("resolvedComment") String resolvedComment) {
        this.id = id;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.ts = ts;
        this.eventHashcode = eventHashcode;
        this.description = description;
        this.isResolved = isResolved;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
        this.resolvedComment = resolvedComment;
    }

    public void update(Event event) {
        if (description == null || description.isEmpty()) {
            this.description = event.description;
        }
        this.name = event.name;
    }

    @Override
    public String toString() {
        return "LogEvent{"
                + "id=" + id
                + ", deviceId=" + deviceId
                + ", eventType=" + eventType
                + ", ts=" + ts
                + ", eventHashcode=" + eventHashcode
                + ", isResolved=" + isResolved
                + ", resolvedBy='" + resolvedBy + '\''
                + ", resolvedAt=" + resolvedAt
                + ", resolvedComment='" + resolvedComment + '\''
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + '}';
    }
}
