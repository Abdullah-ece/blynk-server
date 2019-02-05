package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;

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

        if (isSwitchLabelsEnabled != webSwitch.isSwitchLabelsEnabled) {
            return false;
        }
        if (isWidgetNameHidden != webSwitch.isWidgetNameHidden) {
            return false;
        }
        if (onValue != null ? !onValue.equals(webSwitch.onValue) : webSwitch.onValue != null) {
            return false;
        }
        if (offValue != null ? !offValue.equals(webSwitch.offValue) : webSwitch.offValue != null) {
            return false;
        }
        if (onLabel != null ? !onLabel.equals(webSwitch.onLabel) : webSwitch.onLabel != null) {
            return false;
        }
        if (offLabel != null ? !offLabel.equals(webSwitch.offLabel) : webSwitch.offLabel != null) {
            return false;
        }
        if (alignment != webSwitch.alignment) {
            return false;
        }
        if (labelPosition != webSwitch.labelPosition) {
            return false;
        }
        return color != null ? color.equals(webSwitch.color) : webSwitch.color == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (onValue != null ? onValue.hashCode() : 0);
        result = 31 * result + (offValue != null ? offValue.hashCode() : 0);
        result = 31 * result + (onLabel != null ? onLabel.hashCode() : 0);
        result = 31 * result + (offLabel != null ? offLabel.hashCode() : 0);
        result = 31 * result + (alignment != null ? alignment.hashCode() : 0);
        result = 31 * result + (labelPosition != null ? labelPosition.hashCode() : 0);
        result = 31 * result + (isSwitchLabelsEnabled ? 1 : 0);
        result = 31 * result + (isWidgetNameHidden ? 1 : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
