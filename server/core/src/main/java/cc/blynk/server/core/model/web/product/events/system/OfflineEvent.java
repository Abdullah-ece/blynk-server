package cc.blynk.server.core.model.web.product.events.system;

import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.events.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
public final class OfflineEvent extends SystemEvent {

    public final int ignorePeriod;

    @Override
    public EventType getType() {
        return EventType.OFFLINE;
    }

    @JsonCreator
    public OfflineEvent(@JsonProperty("id")int id,
                        @JsonProperty("name") String name,
                        @JsonProperty("description") String description,
                        @JsonProperty("isNotificationsEnabled") boolean isNotificationsEnabled,
                        @JsonProperty("emailNotifications") EventReceiver[] emailNotifications,
                        @JsonProperty("pushNotifications") EventReceiver[] pushNotifications,
                        @JsonProperty("smsNotifications") EventReceiver[] smsNotifications,
                        @JsonProperty("ignorePeriod") int ignorePeriod) {
        super(id, name, description, isNotificationsEnabled, EventType.OFFLINE.name(),
                emailNotifications, pushNotifications, smsNotifications);
        this.ignorePeriod = ignorePeriod;
    }

    @Override
    public Event copy() {
        return new OfflineEvent(id, name, description, isNotificationsEnabled,
                emailNotifications, pushNotifications, smsNotifications, ignorePeriod);
    }
}
