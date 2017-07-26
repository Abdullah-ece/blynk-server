package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
public class OnlineEvent extends SystemEvent {

    @Override
    public EventType getType() {
        return EventType.ONLINE;
    }

    public OnlineEvent() {
    }

    public OnlineEvent(String name, String description, boolean isNotificationsEnabled, EventReceiver[] emailNotifications, EventReceiver[] pushNotifications, EventReceiver[] smsNotifications) {
        super(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
    }

    @Override
    public Event copy() {
        return new OnlineEvent(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
    }
}
