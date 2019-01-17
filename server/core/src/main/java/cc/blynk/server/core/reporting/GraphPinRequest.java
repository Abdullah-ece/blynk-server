package cc.blynk.server.core.reporting;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType;
import cc.blynk.server.core.model.widgets.outputs.graph.Period;

import static cc.blynk.server.core.model.widgets.outputs.graph.Period.LIVE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.10.15.
 */
public class GraphPinRequest {

    public static final GraphPinRequest EMPTY_REQUEST = new GraphPinRequest(-1, -1, null, Period.DAY, -1);

    public final int dashId;

    public final int deviceId;

    public final PinType pinType;

    public final short pin;

    private final Period graphPeriod;

    public final int count;

    public final GraphGranularityType type;

    public final int skipCount;

    public long from;

    public long to;

    public GraphPinRequest(int dashId, int deviceId, DataStream dataStream,
                           Period period, int skipCount) {
        this.dashId = dashId;
        this.deviceId = deviceId;
        if (dataStream == null) {
            this.pinType = PinType.VIRTUAL;
            this.pin = (short) DataStream.NO_PIN;
        } else {
            this.pinType = (dataStream.pinType == null ? PinType.VIRTUAL : dataStream.pinType);
            this.pin = dataStream.pin;
        }
        this.graphPeriod = period;
        this.count = period.numberOfPoints;
        this.type = period.granularityType;
        this.skipCount = skipCount;
    }

    public boolean isLiveData() {
        return graphPeriod == LIVE;
    }

    public boolean isValid() {
        return deviceId != -1;
    }

    @Override
    public String toString() {
        return "GraphPinRequest{"
                + "dashId=" + dashId
                + ", deviceId=" + deviceId
                + ", pinType=" + pinType
                + ", pin=" + pin
                + ", graphPeriod=" + graphPeriod
                + ", count=" + count
                + ", type=" + type
                + ", skipCount=" + skipCount
                + '}';
    }
}
