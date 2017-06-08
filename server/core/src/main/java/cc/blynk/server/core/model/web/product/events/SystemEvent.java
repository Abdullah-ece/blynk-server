package cc.blynk.server.core.model.web.product.events;

import cc.blynk.server.core.model.web.product.Event;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 08.06.17.
 */
public abstract class SystemEvent extends Event {

    @Override
    public boolean isSame(int hashcode) {
        return false;
    }

}
