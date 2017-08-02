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

    public OnlineEvent(int id, String name, String description, boolean isNotificationsEnabled, EventReceiver[] emailNotifications, EventReceiver[] pushNotifications, EventReceiver[] smsNotifications) {
        super(id, name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
    }

    @Override
    public Event copy() {
        return new OnlineEvent(id, name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
    }
}
