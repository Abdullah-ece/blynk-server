package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;

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
}
