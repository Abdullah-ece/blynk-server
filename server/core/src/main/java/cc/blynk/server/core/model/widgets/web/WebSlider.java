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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        WebSlider webSlider = (WebSlider) o;

        if (sendOnReleaseOn != webSlider.sendOnReleaseOn) {
            return false;
        }
        if (Float.compare(webSlider.step, step) != 0) {
            return false;
        }
        if (fineControlEnabled != webSlider.fineControlEnabled) {
            return false;
        }
        if (Float.compare(webSlider.fineControlStep, fineControlStep) != 0) {
            return false;
        }
        if (Float.compare(webSlider.minValue, minValue) != 0) {
            return false;
        }
        if (Float.compare(webSlider.maxValue, maxValue) != 0) {
            return false;
        }
        if (valuePosition != webSlider.valuePosition) {
            return false;
        }
        if (decimalFormat != null ? !decimalFormat.equals(webSlider.decimalFormat) : webSlider.decimalFormat != null) {
            return false;
        }
        if (valueSuffix != null ? !valueSuffix.equals(webSlider.valueSuffix) : webSlider.valueSuffix != null) {
            return false;
        }
        return color != null ? color.equals(webSlider.color) : webSlider.color == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sendOnReleaseOn ? 1 : 0);
        result = 31 * result + (step != +0.0f ? Float.floatToIntBits(step) : 0);
        result = 31 * result + (fineControlEnabled ? 1 : 0);
        result = 31 * result + (fineControlStep != +0.0f ? Float.floatToIntBits(fineControlStep) : 0);
        result = 31 * result + (valuePosition != null ? valuePosition.hashCode() : 0);
        result = 31 * result + (minValue != +0.0f ? Float.floatToIntBits(minValue) : 0);
        result = 31 * result + (maxValue != +0.0f ? Float.floatToIntBits(maxValue) : 0);
        result = 31 * result + (decimalFormat != null ? decimalFormat.hashCode() : 0);
        result = 31 * result + (valueSuffix != null ? valueSuffix.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
