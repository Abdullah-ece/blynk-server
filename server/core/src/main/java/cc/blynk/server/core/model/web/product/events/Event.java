package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.EventReceiver;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.widgets.CopyObject;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static cc.blynk.utils.ArrayUtil.EMPTY_RECEIVERS;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({

        @JsonSubTypes.Type(value = OnlineEvent.class, name = "ONLINE"),
        @JsonSubTypes.Type(value = OfflineEvent.class, name = "OFFLINE"),
        @JsonSubTypes.Type(value = InformationEvent.class, name = "INFORMATION"),
        @JsonSubTypes.Type(value = WarningEvent.class, name = "WARNING"),
        @JsonSubTypes.Type(value = CriticalEvent.class, name = "CRITICAL")

})
public abstract class Event implements CopyObject<Event> {

    public final int id;

    public final String name;

    public final String description;

    public final boolean isNotificationsEnabled;

    public final String eventCode;

    public final EventReceiver[] emailNotifications;

    public final EventReceiver[] pushNotifications;

    public final EventReceiver[] smsNotifications;

    public abstract EventType getType();

    public Event(int id, String name, String description,
                 boolean isNotificationsEnabled, String eventCode,
                 EventReceiver[] emailNotifications,
                 EventReceiver[] pushNotifications,
                 EventReceiver[] smsNotifications) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isNotificationsEnabled = isNotificationsEnabled;
        this.eventCode = eventCode;
        if (emailNotifications == null || emailNotifications.length == 0) {
            this.emailNotifications = EMPTY_RECEIVERS;
        } else {
            this.emailNotifications = ArrayUtil.copy(emailNotifications, EventReceiver.class);
        }
        if (pushNotifications == null || pushNotifications.length == 0) {
            this.pushNotifications = EMPTY_RECEIVERS;
        } else {
            this.pushNotifications = ArrayUtil.copy(pushNotifications, EventReceiver.class);
        }
        if (smsNotifications == null || smsNotifications.length == 0) {
            this.smsNotifications = EMPTY_RECEIVERS;
        } else {
            this.smsNotifications = ArrayUtil.copy(smsNotifications, EventReceiver.class);
        }
    }

    public boolean isSame(int hashcode) {
        return eventCode.hashCode() == hashcode;
    }

    public int hashCode() {
        return eventCode == null ? 0 : eventCode.hashCode();
    }

}
