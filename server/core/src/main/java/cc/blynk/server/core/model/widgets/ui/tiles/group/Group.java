package cc.blynk.server.core.model.widgets.ui.tiles.group;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.ui.tiles.GroupFunctionValue;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.utils.IntArray.EMPTY_INTS;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.02.19.
 */
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

    public boolean contains(int deviceId) {
        return ArrayUtil.contains(this.deviceIds, deviceId);
    }

    public void updateDataSteamForFunctionValue(GroupFunctionValue groupFunctionValue) {
        DataStream dataStream = getDataStreamByFunctionValue(groupFunctionValue);
        if (dataStream != null) {
            dataStream.value = String.valueOf(groupFunctionValue.result());
        }
    }

    private DataStream getDataStreamByFunctionValue(GroupFunctionValue groupFunctionValue) {
        if (this.id == groupFunctionValue.groupId) {
            for (DataStream dataStream : this.viewDataStreams) {
                if (groupFunctionValue.isSame(dataStream)) {
                    return dataStream;
                }
            }
        }
        return null;
    }

    public void updateControlDataStream(short pin, PinType pinType, String value) {
        DataStream controlDataStream = DataStream.getDataStream(this.controlDataStreams, pin, pinType);
        if (controlDataStream != null) {
            controlDataStream.value = value;
        }
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
