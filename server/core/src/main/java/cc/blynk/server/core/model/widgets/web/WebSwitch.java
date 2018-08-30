package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;

import java.util.Objects;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.02.18.
 */
public class WebSwitch extends WebWidget {

    public String onValue;

    public String offValue;

    public String onLabel;

    public String offLabel;

    public TextAlignment alignment;

    public TextAlignment labelPosition;

    public boolean isSwitchLabelsEnabled;

    public boolean isWidgetNameHidden;

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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        WebSwitch webSwitch = (WebSwitch) o;
        return isSwitchLabelsEnabled == webSwitch.isSwitchLabelsEnabled
                && isWidgetNameHidden == webSwitch.isWidgetNameHidden
                && Objects.equals(onValue, webSwitch.onValue)
                && Objects.equals(offValue, webSwitch.offValue)
                && Objects.equals(onLabel, webSwitch.onLabel)
                && Objects.equals(offLabel, webSwitch.offLabel)
                && alignment == webSwitch.alignment
                && labelPosition == webSwitch.labelPosition
                && Objects.equals(color, webSwitch.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), onValue, offValue, onLabel,
                offLabel, alignment, labelPosition,
                isSwitchLabelsEnabled, isWidgetNameHidden, color);
    }
}
