package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.02.18.
 */
public class WebSlider extends WebWidget {

    public boolean sendOnReleaseOn;

    public float step;

    public boolean fineControlEnabled;

    public boolean fineControlStep;

    public TextAlignment labelPosition;

    public String decimalFormat;

    public String valueSuffix;

    public String color;

    @Override
    public PinMode getModeType() {
        return PinMode.out;
    }

    @Override
    public int getPrice() {
        return 0;
    }


}
