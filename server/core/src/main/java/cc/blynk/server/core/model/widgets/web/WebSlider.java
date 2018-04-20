package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;

import java.util.Objects;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.02.18.
 */
public class WebSlider extends WebWidget {

    public boolean sendOnReleaseOn;

    public float step;

    public boolean fineControlEnabled;

    public float fineControlStep;

    public TextAlignment valuePosition;

    public float minValue;

    public float maxValue;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebSlider)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        WebSlider webSlider = (WebSlider) o;
        return sendOnReleaseOn == webSlider.sendOnReleaseOn
                && Float.compare(webSlider.step, step) == 0
                && fineControlEnabled == webSlider.fineControlEnabled
                && Float.compare(webSlider.fineControlStep, fineControlStep) == 0
                && Float.compare(webSlider.minValue, minValue) == 0
                && Float.compare(webSlider.maxValue, maxValue) == 0
                && valuePosition == webSlider.valuePosition
                && Objects.equals(decimalFormat, webSlider.decimalFormat)
                && Objects.equals(valueSuffix, webSlider.valueSuffix)
                && Objects.equals(color, webSlider.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sendOnReleaseOn, step,
                fineControlEnabled, fineControlStep, valuePosition,
                minValue, maxValue, decimalFormat, valueSuffix, color);
    }
}
