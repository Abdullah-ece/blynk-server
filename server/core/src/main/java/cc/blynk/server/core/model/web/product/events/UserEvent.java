package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;
import cc.blynk.server.core.model.web.product.EventReceiver;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 08.06.17.
 */
public abstract class UserEvent extends Event {

    public String eventCode;

    @Override
    public boolean isSame(int hashcode) {
        return this.eventCode.hashCode() == hashcode;
    }

    @Override
    public int hashCode() {
        return eventCode == null ? 0 : eventCode.hashCode();
    }

    UserEvent() {
    }

    UserEvent(String name, String description, boolean isNotificationsEnabled,
                     EventReceiver[] emailNotifications,
                     EventReceiver[] pushNotifications,
                     EventReceiver[] smsNotifications,
                     String eventCode) {
        super(name, description, isNotificationsEnabled, emailNotifications, pushNotifications, smsNotifications);
        this.eventCode = eventCode;
    }

}
