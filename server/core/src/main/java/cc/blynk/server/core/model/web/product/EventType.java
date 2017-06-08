package cc.blynk.server.core.model.web.product;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.06.17.
 */
public enum EventType {

    ONLINE(false),
    OFFLINE(false),
    INFORMATION(true),
    WARNING(true),
    CRITICAL(true);

    EventType(boolean isUserEvent) {
        this.isUserEvent = isUserEvent;
    }

    public final boolean isUserEvent;

}
