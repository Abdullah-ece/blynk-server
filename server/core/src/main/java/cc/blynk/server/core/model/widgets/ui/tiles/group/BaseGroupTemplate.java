package cc.blynk.server.core.model.widgets.ui.tiles.group;

import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SwitchWith3LabelsGroupTemplate.class, name = "SWITCH_3LABELS")
})
public abstract class BaseGroupTemplate {

    public final long id;

    public volatile Widget[] widgets;

    public final String name;

    public final String icon;

    public final String description;

    public final FontSize fontSize;

    public final int tileColor;

    public BaseGroupTemplate(long id, Widget[] widgets,
                             String name, String icon, String description,
                             FontSize fontSize, int tileColor) {
        this.id = id;
        this.widgets = widgets == null ? Widget.EMPTY_WIDGETS : widgets;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.fontSize = fontSize;
        this.tileColor = tileColor;
    }
}
