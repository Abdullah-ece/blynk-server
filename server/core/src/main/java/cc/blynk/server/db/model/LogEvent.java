package cc.blynk.server.db.model;

import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.events.UserEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.06.16.
 */
public class LogEvent {

    public final int id;

    public final int deviceId;

    public final EventType eventType;

    public final long ts;

    public final int eventHashcode;

    public final boolean isResolved;

    private final String resolvedBy;

    public String name;
    public String description;

    public LogEvent(int deviceId, EventType eventType, long ts, int eventHashCode, String description) {
        this.id = -1;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.ts = ts;
        this.eventHashcode = eventHashCode;
        this.description = description;
        this.isResolved = false;
        this.resolvedBy = null;
    }

    //for tests mostly
    @JsonCreator
    public LogEvent(@JsonProperty("id") int id,
                    @JsonProperty("deviceId") int deviceId,
                    @JsonProperty("eventType") EventType eventType,
                    @JsonProperty("ts") long ts,
                    @JsonProperty("eventHashcode") int eventHashcode,
                    @JsonProperty("description") String description,
                    @JsonProperty("isResolved") boolean isResolved,
                    @JsonProperty("resolvedBy") String resolvedBy) {
        this.id = id;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.ts = ts;
        this.eventHashcode = eventHashcode;
        this.description = description;
        this.isResolved = isResolved;
        this.resolvedBy = resolvedBy;
    }

    public void update(UserEvent event) {
        if (description == null || description.isEmpty()) {
            this.description = event.description;
        }
        this.name = event.name;
    }
}
