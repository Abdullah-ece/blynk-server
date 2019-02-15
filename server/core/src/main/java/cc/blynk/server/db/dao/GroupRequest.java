package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.outputs.graph.Granularity;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 15/02/2019.
 *
 */
public final class GroupRequest {

    public final Granularity granularity;

    public final AggregationFunctionType aggregationFunctionType;

    public final int[] deviceIds;

    public final short pin;

    public final PinType pinType;

    public GroupRequest(Granularity granularity,
                        AggregationFunctionType aggregationFunctionType,
                        int[] deviceIds, short pin, PinType pinType) {
        this.granularity = granularity;
        this.aggregationFunctionType = aggregationFunctionType;
        this.deviceIds = deviceIds;
        this.pin = pin;
        this.pinType = pinType;
    }
}
