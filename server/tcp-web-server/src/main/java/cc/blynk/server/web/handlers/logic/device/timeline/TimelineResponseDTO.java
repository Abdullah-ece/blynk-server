package cc.blynk.server.web.handlers.logic.device.timeline;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.db.model.LogEvent;
import cc.blynk.server.db.model.LogEventCountKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class TimelineResponseDTO {

    public final int totalCritical;

    public final int totalWarning;

    public final int totalResolved;

    public final List<LogEvent> eventList;

    @JsonCreator
    public TimelineResponseDTO(@JsonProperty("totalCritical") int totalCritical,
                               @JsonProperty("totalWarning") int totalWarning,
                               @JsonProperty("totalResolved") int totalResolved,
                               @JsonProperty("eventList") List<LogEvent> eventList) {
        this.totalCritical = totalCritical;
        this.totalWarning = totalWarning;
        this.totalResolved = totalResolved;
        this.eventList = eventList;
    }

    public TimelineResponseDTO(int deviceId,
                               Map<LogEventCountKey, Integer> totalCounters,
                               List<LogEvent> eventList) {
        this(
                totalCounters.getOrDefault(new LogEventCountKey(deviceId, EventType.CRITICAL, false), 0),
                totalCounters.getOrDefault(new LogEventCountKey(deviceId, EventType.WARNING, false), 0),
                totalResolved(totalCounters),
                eventList
        );
    }

    private static int totalResolved(Map<LogEventCountKey, Integer> totalCounters) {
        int totalResolved = 0;
        for (Map.Entry<LogEventCountKey, Integer> entry : totalCounters.entrySet()) {
            if (entry.getKey().isResolved) {
                totalResolved += entry.getValue();
            }
        }
        return totalResolved;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
