package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.web.product.events.*;
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
public abstract class Event {

    public int id;

    public String name;

    public String description;

    public boolean isNotificationsEnabled;

    public EventReceiver[] emailNotifications = EMPTY_RECEIVERS;

    public EventReceiver[] pushNotifications = EMPTY_RECEIVERS;

    public EventReceiver[] smsNotifications = EMPTY_RECEIVERS;

    public abstract boolean isSame(int hashcode);

    public abstract EventType getType();

    public abstract Event copy();

    public Event() {
    }

    public Event(int id, String name, String description, boolean isNotificationsEnabled,
                 EventReceiver[] emailNotifications,
                 EventReceiver[] pushNotifications,
                 EventReceiver[] smsNotifications) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isNotificationsEnabled = isNotificationsEnabled;
        this.emailNotifications = ArrayUtil.cloneArray(emailNotifications, EventReceiver.class);
        this.pushNotifications = ArrayUtil.cloneArray(pushNotifications, EventReceiver.class);
        this.smsNotifications = ArrayUtil.cloneArray(smsNotifications, EventReceiver.class);
    }
}
