package cc.blynk.server.core.model.widgets.web.label;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.12.17.
 */
public class Level {

    public final int min;

    public final int max;

    public final Position position;

    public final String color;

    @JsonCreator
    public Level(@JsonProperty("min") int min,
                 @JsonProperty("max") int max,
                 @JsonProperty("position") Position position,
                 @JsonProperty("color") String color) {
        this.min = min;
        this.max = max;
        this.position = position;
        this.color = color;
    }
}
