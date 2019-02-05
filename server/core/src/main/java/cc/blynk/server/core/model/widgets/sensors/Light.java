package cc.blynk.server.core.model.widgets.sensors;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.OnePinWidget;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.09.16.
 */
public class Light extends OnePinWidget {

    private int frequency;

    @Override
    public PinMode getModeType() {
        return PinMode.out;
    }

}
