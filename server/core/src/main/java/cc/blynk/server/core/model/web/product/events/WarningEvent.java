package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
public class WarningEvent extends UserEvent {

    @Override
    public EventType getType() {
        return EventType.WARNING;
    }

    public WarningEvent() {
    }

    public WarningEvent(String name, String description,
                        boolean isNotificationsEnabled, EventReceiver[] emailNotifications,
                        EventReceiver[] pushNotifications, EventReceiver[] smsNotifications, String eventCode) {
        super(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications, eventCode);
    }

    @Override
    public Event copy() {
        return new WarningEvent(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications, eventCode);
    }

}
