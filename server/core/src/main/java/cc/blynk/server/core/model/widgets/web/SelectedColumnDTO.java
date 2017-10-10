package cc.blynk.server.core.model.widgets.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.10.17.
 */
public class SelectedColumnDTO {

    public final String name;
    public final String label;
    public final FieldType type;

    @JsonCreator
    public SelectedColumnDTO(@JsonProperty("name") String name,
                             @JsonProperty("label") String label,
                             @JsonProperty("type") FieldType fieldType) {
        this.name = name;
        this.label = label;
        this.type = fieldType;
    }

}
