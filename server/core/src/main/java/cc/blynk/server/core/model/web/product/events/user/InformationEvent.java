package cc.blynk.server.core.model.web.product.events.user;

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
public final class InformationEvent extends UserEvent {

    @Override
    public EventType getType() {
        return EventType.INFORMATION;
    }

    @JsonCreator
    public InformationEvent(@JsonProperty("id")int id,
                            @JsonProperty("name") String name,
                            @JsonProperty("description") String description,
                            @JsonProperty("isNotificationsEnabled") boolean isNotificationsEnabled,
                            @JsonProperty("eventCode") String eventCode,
                            @JsonProperty("emailNotifications") EventReceiver[] emailNotifications,
                            @JsonProperty("pushNotifications") EventReceiver[] pushNotifications,
                            @JsonProperty("smsNotifications") EventReceiver[] smsNotifications) {
        super(id, name, description, isNotificationsEnabled, eventCode,
                emailNotifications, pushNotifications, smsNotifications);
    }

    @Override
    public Event copy() {
        return new InformationEvent(id, name, description, isNotificationsEnabled, eventCode,
                emailNotifications, pushNotifications, smsNotifications);
    }

}
