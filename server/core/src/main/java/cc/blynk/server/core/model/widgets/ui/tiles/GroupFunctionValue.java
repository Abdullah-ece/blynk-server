package cc.blynk.server.core.model.widgets.ui.tiles;

import cc.blynk.server.core.dao.functions.AggregationFunction;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.widgets.ui.tiles.group.Group;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.NumberUtil;

import static cc.blynk.utils.NumberUtil.NO_RESULT;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.02.19.
 */
public final class GroupFunctionValue {

    public final long groupId;

    private final short pin;

    private final PinType pinType;

    private final int[] deviceIds;

    private final AggregationFunction aggregationFunction;

    GroupFunctionValue(Group group, DataStream dataStream) {
        this(group.id, group.deviceIds,
                dataStream.pin, dataStream.pinType,
                dataStream.aggregationFunctionType.produce());
    }

    private GroupFunctionValue(long groupId, int[] deviceIds, short pin,
                               PinType pinType, AggregationFunction aggregationFunction) {
        this.groupId = groupId;
        this.pin = pin;
        this.pinType = pinType;
        this.deviceIds = deviceIds;
        this.aggregationFunction = aggregationFunction;
    }

    public boolean isSame(short pin, PinType pinType) {
        return this.pin == pin && this.pinType == pinType;
    }

    public boolean isSame(DataStream dataStream) {
        return isSame(dataStream.pin, dataStream.pinType);
    }

    public boolean isSame(DeviceStorageKey key, int deviceId) {
        return isSame(key.pin, key.pinType) && ArrayUtil.contains(deviceIds, deviceId);
    }

    public void apply(String value) {
        double parsed = NumberUtil.parseDouble(value);
        if (parsed != NO_RESULT) {
            aggregationFunction.apply(parsed);
        }
    }

    public double result() {
        return aggregationFunction.getResult();
    }

}
