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
public class MobileGraphRequest {

    public static final MobileGraphRequest EMPTY_REQUEST = new MobileGraphRequest(-1, -1, null, Period.DAY, -1);

    public final int dashId;

    public final int deviceId;

    public final PinType pinType;

    public final short pin;

    private final Period graphPeriod;

    public final int limit;

    public final GraphGranularityType type;

    public final int offset;

    public final long from;

    public final long to;

    public MobileGraphRequest(int dashId, int deviceId,
                              DataStream dataStream,
                              Period period, int page) {
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
        this.type = period.granularityType;

        this.limit = period.numberOfPoints;
        this.offset = period.numberOfPoints * page;

        long now = System.currentTimeMillis();
        this.from = now - period.millis;
        this.to = now;
    }

    public boolean isLiveData() {
        return graphPeriod == LIVE;
    }

    public boolean isValid() {
        return deviceId != -1;
    }

    @Override
    public String toString() {
        return "MobileGraphRequest{"
                + "dashId=" + dashId
                + ", deviceId=" + deviceId
                + ", pinType=" + pinType
                + ", pin=" + pin
                + ", graphPeriod=" + graphPeriod
                + ", limit=" + limit
                + ", type=" + type
                + ", offset=" + offset
                + '}';
    }
}
