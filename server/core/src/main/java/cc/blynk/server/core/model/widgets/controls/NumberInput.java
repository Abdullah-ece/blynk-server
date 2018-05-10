package cc.blynk.server.core.model.widgets.controls;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 21.03.15.
 */
public class NumberInput extends OnePinWidget {

    public String defaultValue;

    public String hint;

    public String suffix;

    public float step;

    public int maximumFractionDigits;

    public boolean isLoopOn;

    public FontSize fontSize;

    public int buttonColor;

    @Override
    public PinMode getModeType() {
        return PinMode.out;
    }

    @Override
    public int getPrice() {
        return 400;
    }
}
