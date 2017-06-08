package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.web.product.events.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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

    public String name;

    public String description;

    public boolean isNotificationsEnabled;

    public EventReceiver[] emailNotifications;

    public EventReceiver[] pushNotifications;

    public EventReceiver[] smsNotifications;

    public abstract boolean isSame(String eventCode);

    public abstract EventType getType();

}
