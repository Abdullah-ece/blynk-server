package cc.blynk.server.core.model.widgets.ui.tiles.group;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GroupLabel {

    public final DataStream dataStream;

    public final AggregationFunctionType aggregationFunctionType;

    public final String name;

    public final String icon;

    public final int maximumFractionDigits;

    public final String suffix;

    public final int nameColor;

    public final int valueColor;

    public final int iconColor;

    @JsonCreator
    public GroupLabel(@JsonProperty("dataStream") DataStream dataStream,
                      @JsonProperty("aggregationFunctionType") AggregationFunctionType aggregationFunctionType,
                      @JsonProperty("name") String name,
                      @JsonProperty("icon") String icon,
                      @JsonProperty("maximumFractionDigits") int maximumFractionDigits,
                      @JsonProperty("suffix") String suffix,
                      @JsonProperty("nameColor") int nameColor,
                      @JsonProperty("valueColor") int valueColor,
                      @JsonProperty("iconColor") int iconColor) {
        this.dataStream = dataStream;
        this.aggregationFunctionType = aggregationFunctionType;
        this.name = name;
        this.icon = icon;
        this.maximumFractionDigits = maximumFractionDigits;
        this.suffix = suffix;
        this.nameColor = nameColor;
        this.valueColor = valueColor;
        this.iconColor = iconColor;
    }
}
