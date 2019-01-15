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
public final class OnlineEvent extends SystemEvent {

    @Override
    public EventType getType() {
        return EventType.ONLINE;
    }

    @JsonCreator
    public OnlineEvent(@JsonProperty("id")int id,
                       @JsonProperty("name") String name,
                       @JsonProperty("description") String description,
                       @JsonProperty("isNotificationsEnabled") boolean isNotificationsEnabled,
                       @JsonProperty("emailNotifications") EventReceiver[] emailNotifications,
                       @JsonProperty("pushNotifications") EventReceiver[] pushNotifications,
                       @JsonProperty("smsNotifications") EventReceiver[] smsNotifications) {
        super(id, name, description, isNotificationsEnabled, EventType.ONLINE.name(),
                emailNotifications, pushNotifications, smsNotifications);
    }

    @Override
    public Event copy() {
        return new OnlineEvent(id, name, description, isNotificationsEnabled,
                emailNotifications, pushNotifications, smsNotifications);
    }
}
