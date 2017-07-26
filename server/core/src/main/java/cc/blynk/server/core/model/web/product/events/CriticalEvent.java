package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
public class CriticalEvent extends UserEvent {

    @Override
    public EventType getType() {
        return EventType.CRITICAL;
    }

    public CriticalEvent() {
    }

    public CriticalEvent(String name, String description,
                         boolean isNotificationsEnabled, EventReceiver[] emailNotifications,
                         EventReceiver[] pushNotifications, EventReceiver[] smsNotifications, String eventCode) {
        super(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications, eventCode);
    }

    @Override
    public Event copy() {
        return new CriticalEvent(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications, eventCode);
    }
}
