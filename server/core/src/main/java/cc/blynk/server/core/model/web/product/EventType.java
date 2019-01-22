package cc.blynk.server.core.model.web.product;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.06.17.
 */
public enum EventType {

    ONLINE,
    OFFLINE,
    INFORMATION,
    WARNING,
    CRITICAL;

    //cached value for values field to avoid allocations
    private static final EventType[] values = values();

    public static EventType[] getValues() {
        return values;
    }
}
