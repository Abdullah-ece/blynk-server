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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Level)) return false;

        Level level = (Level) o;

        if (min != level.min) return false;
        if (max != level.max) return false;
        if (position != level.position) return false;
        return color != null ? color.equals(level.color) : level.color == null;
    }

    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + max;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
