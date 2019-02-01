package cc.blynk.server.core.model.widgets.ui.tiles.group;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SwitchWith3LabelsGroupTemplate extends BaseGroupTemplate {

    private static final GroupLabel[] EMPTY_GROUP_LABELS = {};

    public final DataStream switchDataStream;

    public final int switchColor;

    public final GroupLabel[] groupLabels;

    @JsonCreator
    public SwitchWith3LabelsGroupTemplate(@JsonProperty("id") int id,
                                          @JsonProperty("widgets") Widget[] widgets,
                                          @JsonProperty("name") String name,
                                          @JsonProperty("icon") String icon,
                                          @JsonProperty("description") String description,
                                          @JsonProperty("fontSize") FontSize fontSize,
                                          @JsonProperty("tileColor") int tileColor,
                                          @JsonProperty("switchDataStream") DataStream switchDataStream,
                                          @JsonProperty("switchColor") int switchColor,
                                          @JsonProperty("groupLabels") GroupLabel[] groupLabels) {
        super(id, widgets, name, icon, description, fontSize, tileColor);
        this.switchDataStream = switchDataStream;
        this.switchColor = switchColor;
        this.groupLabels = groupLabels == null ? EMPTY_GROUP_LABELS : groupLabels;
    }
}
