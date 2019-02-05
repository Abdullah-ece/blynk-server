package cc.blynk.server.core.model.widgets.ui.tiles.group;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.utils.IntArray.EMPTY_INTS;

public final class Group {

    public final long id;

    public final String name;

    public final long templateId;

    public final int[] deviceIds;

    public final DataStream[] controlDataStreams;

    public final DataStream[] viewDataStreams;

    @JsonCreator
    public Group(@JsonProperty("id") long id,
                 @JsonProperty("name") String name,
                 @JsonProperty("templateId") long templateId,
                 @JsonProperty("deviceIds") int[] deviceIds,
                 @JsonProperty("controlDataStreams") DataStream[] controlDataStreams,
                 @JsonProperty("viewDataStreams") DataStream[] viewDataStreams) {
        this.id = id;
        this.name = name;
        this.templateId = templateId;
        this.deviceIds = deviceIds == null ? EMPTY_INTS : deviceIds;
        this.controlDataStreams = controlDataStreams == null ? DataStream.EMPTY_DATA_STREAMS : controlDataStreams;
        this.viewDataStreams = viewDataStreams == null ? DataStream.EMPTY_DATA_STREAMS : viewDataStreams;
    }

    public void updateValue(short pin, PinType pinType, String value) {
        for (DataStream dataStream : controlDataStreams) {
            if (dataStream.isSame(pin, pinType)) {
                dataStream.value = value;
            }
        }
    }

}
