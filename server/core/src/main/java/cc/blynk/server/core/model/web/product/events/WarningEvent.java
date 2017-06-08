package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.EventType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.05.17.
 */
public class WarningEvent extends UserEvent {

    @Override
    public EventType getType() {
        return EventType.WARNING;
    }

}
