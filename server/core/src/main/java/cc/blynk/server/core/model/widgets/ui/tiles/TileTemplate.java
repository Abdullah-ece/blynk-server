package cc.blynk.server.core.model.widgets.ui.tiles;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_INTS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_WIDGETS;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 02.10.17.
 */
public class TileTemplate {

    public final long id;

    public volatile Widget[] widgets;

    public final int[] deviceIds;

    public final String name;

    public final TileMode mode;

    @JsonProperty("pin")
    public final DataStream dataStream;

    public final String valueName;

    public final String valueSuffix;

    public final int color;

    public final TextAlignment alignment;

    public final boolean disableWhenOffline;

    public final boolean showDeviceName;

    @JsonCreator
    public TileTemplate(@JsonProperty("id") long id,
                        @JsonProperty("widgets") Widget[] widgets,
                        @JsonProperty("deviceIds") int[] deviceIds,
                        @JsonProperty("name") String name,
                        @JsonProperty("mode") TileMode mode,
                        @JsonProperty("pin") DataStream dataStream,
                        @JsonProperty("valueName") String valueName,
                        @JsonProperty("valueSuffix") String valueSuffix,
                        @JsonProperty("color") int color,
                        @JsonProperty("alignment") TextAlignment alignment,
                        @JsonProperty("disableWhenOffline") boolean disableWhenOffline,
                        @JsonProperty("showDeviceName") boolean showDeviceName) {
        this.id = id;
        this.widgets = widgets == null ? EMPTY_WIDGETS : widgets;
        this.deviceIds = deviceIds == null ? EMPTY_INTS : deviceIds;
        this.name = name;
        this.mode = mode;
        this.dataStream = dataStream;
        this.valueName = valueName;
        this.valueSuffix = valueSuffix;
        this.color = color;
        this.alignment = alignment;
        this.disableWhenOffline = disableWhenOffline;
        this.showDeviceName = showDeviceName;
    }

    public int getPrice() {
        int sum = 0;
        for (Widget widget : widgets) {
            sum += widget.getPrice();
        }
        return sum;
    }
}
