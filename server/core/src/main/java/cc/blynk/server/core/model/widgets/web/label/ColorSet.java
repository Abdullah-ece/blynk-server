package cc.blynk.server.core.model.widgets.web.label;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.12.17.
 */
public class ColorSet {

    public final int min;

    public final int max;

    public final String backgroundColor;

    public final String textColor;

    public final String customText;

    @JsonCreator
    public ColorSet(@JsonProperty("min") int min,
                    @JsonProperty("max") int max,
                    @JsonProperty("backgroundColor") String backgroundColor,
                    @JsonProperty("textColor") String textColor,
                    @JsonProperty("customText") String customText) {
        this.min = min;
        this.max = max;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.customText = customText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColorSet)) return false;

        ColorSet colorSet = (ColorSet) o;

        if (min != colorSet.min) return false;
        if (max != colorSet.max) return false;
        if (backgroundColor != null ? !backgroundColor.equals(colorSet.backgroundColor) : colorSet.backgroundColor != null)
            return false;
        if (textColor != null ? !textColor.equals(colorSet.textColor) : colorSet.textColor != null) return false;
        return customText != null ? customText.equals(colorSet.customText) : colorSet.customText == null;
    }

    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + max;
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + (textColor != null ? textColor.hashCode() : 0);
        result = 31 * result + (customText != null ? customText.hashCode() : 0);
        return result;
    }
}
