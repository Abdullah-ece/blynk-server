package cc.blynk.server.core.model.widgets.sensors;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.OnePinWidget;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 21.03.15.
 */
public class GPSTrigger extends OnePinWidget {

    public boolean triggerOnEnter;

    public float triggerLat;

    public float triggerLon;

    public int triggerRadius;

    public int accuracy;

    @Override
    public PinMode getModeType() {
        return PinMode.out;
    }

}
