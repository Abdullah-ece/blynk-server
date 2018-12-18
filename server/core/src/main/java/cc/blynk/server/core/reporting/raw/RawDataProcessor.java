package cc.blynk.server.core.reporting.raw;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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

    public void collect(BaseReportingKey key, long ts, double value) {
        Queue<BaseReportingValue> queue = rawStorage.get(key);
        if (queue == null) {
            queue = new ArrayDeque<>();
            rawStorage.put(key, queue);
        }
        queue.add(new BaseReportingValue(ts, value));
    }

}
