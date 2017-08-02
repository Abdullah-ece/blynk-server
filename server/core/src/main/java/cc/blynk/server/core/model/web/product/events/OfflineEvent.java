package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
public class OfflineEvent extends SystemEvent {

    public int ignorePeriod;

    @Override
    public EventType getType() {
        return EventType.OFFLINE;
    }

    public OfflineEvent() {
    }

    public OfflineEvent(int id, String name, String description, boolean isNotificationsEnabled, EventReceiver[] emailNotifications, EventReceiver[] pushNotifications, EventReceiver[] smsNotifications, int ignorePeriod) {
        super(id, name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
        this.ignorePeriod = ignorePeriod;
    }

    @Override
    public Event copy() {
        return new OfflineEvent(id, name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications, ignorePeriod);
    }
}
