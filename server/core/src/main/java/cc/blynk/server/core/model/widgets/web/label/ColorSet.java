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

    @JsonCreator
    public ColorSet(@JsonProperty("min") int min,
                    @JsonProperty("max") int max,
                    @JsonProperty("backgroundColor") String backgroundColor,
                    @JsonProperty("textColor") String textColor) {
        this.min = min;
        this.max = max;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }
}
