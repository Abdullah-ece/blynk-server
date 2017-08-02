package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventReceiver;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 08.06.17.
 */
public abstract class SystemEvent extends Event {

    @Override
    public boolean isSame(int hashcode) {
        return this.name.hashCode() == hashcode;
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public SystemEvent() {
    }

    public SystemEvent(int id, String name, String description, boolean isNotificationsEnabled, EventReceiver[] emailNotifications, EventReceiver[] pushNotifications, EventReceiver[] smsNotifications) {
        super(id, name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
    }

}
