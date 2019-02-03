package cc.blynk.server.core.stats.model;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.ByteBufAllocatorMetricProvider;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 5/18/17.
 */
final class MemoryStat {

    public long heapBytes;

    public long directBytes;

    MemoryStat() {
    }

    public void update(ByteBufAllocator byteBufAllocator) {
        long directMemory = 0;
        long heapMemory = 0;

        if (byteBufAllocator instanceof ByteBufAllocatorMetricProvider) {
            ByteBufAllocatorMetric metric = ((ByteBufAllocatorMetricProvider) byteBufAllocator).metric();
            directMemory = metric.usedDirectMemory();
            heapMemory = metric.usedHeapMemory();
        }

        this.directBytes = directMemory;
        this.heapBytes = heapMemory;
    }

    public void reset() {
        this.heapBytes   = 0;
        this.directBytes = 0;
    }
}
