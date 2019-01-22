package cc.blynk.server.core.model.web.product.events.user;

import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.events.Event;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 08.06.17.
 */
abstract class UserEvent extends Event {

    UserEvent(int id, String name, String description,
              boolean isNotificationsEnabled, String eventCode,
              EventReceiver[] emailNotifications,
              EventReceiver[] pushNotifications,
              EventReceiver[] smsNotifications) {
        super(id, name, description, isNotificationsEnabled, eventCode,
                emailNotifications, pushNotifications, smsNotifications);
    }

}
