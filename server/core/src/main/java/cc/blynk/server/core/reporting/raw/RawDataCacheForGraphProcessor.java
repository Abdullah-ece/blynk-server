package cc.blynk.server.core.reporting.raw;

import cc.blynk.server.core.reporting.GraphPinRequest;
import cc.blynk.server.core.reporting.WebGraphRequest;
import cc.blynk.server.db.dao.RawEntry;
import cc.blynk.utils.structure.LimitedArrayDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static cc.blynk.utils.FileUtils.SIZE_OF_REPORT_ENTRY;

/**
 * Raw data storage for graph LIVE stream.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.01.17.
 */
public class RawDataCacheForGraphProcessor {

    private static final Logger log = LogManager.getLogger(RawDataCacheForGraphProcessor.class);

    private static final int GRAPH_CACHE_SIZE = 60;

    public final ConcurrentHashMap<BaseReportingKey, LimitedArrayDeque<RawEntry>> rawStorage;

    public RawDataCacheForGraphProcessor() {
        rawStorage = new ConcurrentHashMap<>();
    }

    public void collect(BaseReportingKey baseReportingKey, RawEntry graphCacheValue) {
        LimitedArrayDeque<RawEntry> cache = rawStorage.get(baseReportingKey);
        if (cache == null) {
            cache = new LimitedArrayDeque<>(GRAPH_CACHE_SIZE);
            rawStorage.put(baseReportingKey, cache);
        }
        cache.add(graphCacheValue);
    }

    public Collection<RawEntry> getLiveGraphData(WebGraphRequest webGraphRequest) {
        Collection<RawEntry> entries = rawStorage.get(
                new BaseReportingKey(webGraphRequest.deviceId, webGraphRequest.pinType, webGraphRequest.pin));
        if (entries == null) {
            return Collections.emptyList();
        }
        return entries;
    }

    //todo remove
    public ByteBuffer getLiveGraphData(GraphPinRequest graphPinRequest) {
        LimitedArrayDeque<RawEntry> cache = rawStorage.get(
                new BaseReportingKey(
                        graphPinRequest.deviceId,
                        graphPinRequest.pinType,
                        graphPinRequest.pin
                )
        );

        if (cache != null && cache.size() > graphPinRequest.skipCount) {
            return toByteBuffer(cache, graphPinRequest.count, graphPinRequest.skipCount);
        }

        return null;
    }

    private ByteBuffer toByteBuffer(LimitedArrayDeque<RawEntry> cache, int count, int skipCount) {
        int size = cache.size();
        int expectedMinimumLength = count + skipCount;
        int diff = size - expectedMinimumLength;
        int startReadIndex = Math.max(0, diff);
        int expectedResultSize = diff < 0 ? count + diff : count;
        if (expectedResultSize <= 0) {
            return null;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(expectedResultSize * SIZE_OF_REPORT_ENTRY);

        int i = 0;
        int counter = 0;
        for (RawEntry rawEntry : cache) {
            if (startReadIndex <= i && counter < expectedResultSize) {
                counter++;
                byteBuffer.putDouble(rawEntry.value)
                        .putLong(rawEntry.ts);
                log.trace("Returning point value {}, ts : {}", rawEntry.value, rawEntry.ts);
            }
            i++;
        }
        return byteBuffer;
    }
}
