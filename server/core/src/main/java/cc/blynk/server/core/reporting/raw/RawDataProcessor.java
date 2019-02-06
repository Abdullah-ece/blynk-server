package cc.blynk.server.core.reporting.raw;

import cc.blynk.server.core.model.enums.PinType;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static cc.blynk.utils.NumberUtil.NO_RESULT;

/**
 * Simply stores every record in memory that should be stored in reporting DB lately.
 * Could cause OOM at high request rate. However we don't use it very high loads.
 * So this is fine for now.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.01.17.
 */
public class RawDataProcessor {

    public final Map<BaseReportingKey, Queue<BaseReportingValue>> rawStorage;

    public RawDataProcessor() {
        rawStorage = new ConcurrentHashMap<>();
    }

    public void collect(int deviceId, short pin, PinType pinType, long ts, double value) {
        if (pinType != null && value != NO_RESULT) {
            collect(new BaseReportingKey(deviceId, pinType, pin), ts, value);
        }
    }

    public void collect(BaseReportingKey key, long ts, double value) {
        Queue<BaseReportingValue> queue = rawStorage.get(key);
        if (queue == null) {
            queue = new ArrayDeque<>();
            rawStorage.put(key, queue);
        }
        queue.add(new BaseReportingValue(ts, value));
    }

}
