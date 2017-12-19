package cc.blynk.server.core.model.widgets.web.label;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;
import cc.blynk.server.core.model.widgets.web.WebWidget;

import java.util.Arrays;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */

public class WebLabel extends WebWidget {

    public DataType dataType;

    public String decimalFormat;

    public String valueSuffix;

    public TextAlignment alignment;

    public boolean isColorSetEnabled;

    public ColorSet[] colorsSet;

    public String backgroundColor;

    public String textColor;

    public boolean isShowLevelEnabled;

    public Level level;

    @Override
    public PinMode getModeType() {
        return PinMode.in;
    }

    @Override
    public int getPrice() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebLabel)) return false;
        if (!super.equals(o)) return false;

        WebLabel webLabel = (WebLabel) o;

        if (isColorSetEnabled != webLabel.isColorSetEnabled) return false;
        if (isShowLevelEnabled != webLabel.isShowLevelEnabled) return false;
        if (dataType != webLabel.dataType) return false;
        if (decimalFormat != null ? !decimalFormat.equals(webLabel.decimalFormat) : webLabel.decimalFormat != null)
            return false;
        if (valueSuffix != null ? !valueSuffix.equals(webLabel.valueSuffix) : webLabel.valueSuffix != null)
            return false;
        if (alignment != webLabel.alignment) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(colorsSet, webLabel.colorsSet)) return false;
        if (backgroundColor != null ? !backgroundColor.equals(webLabel.backgroundColor) : webLabel.backgroundColor != null)
            return false;
        if (textColor != null ? !textColor.equals(webLabel.textColor) : webLabel.textColor != null) return false;
        return level != null ? level.equals(webLabel.level) : webLabel.level == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (decimalFormat != null ? decimalFormat.hashCode() : 0);
        result = 31 * result + (valueSuffix != null ? valueSuffix.hashCode() : 0);
        result = 31 * result + (alignment != null ? alignment.hashCode() : 0);
        result = 31 * result + (isColorSetEnabled ? 1 : 0);
        result = 31 * result + Arrays.hashCode(colorsSet);
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + (textColor != null ? textColor.hashCode() : 0);
        result = 31 * result + (isShowLevelEnabled ? 1 : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        return result;
    }
}
