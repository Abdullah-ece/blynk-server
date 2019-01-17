package cc.blynk.server.db.dao;

import java.nio.ByteBuffer;
import java.util.Collection;

import static cc.blynk.utils.ByteUtils.REPORTING_RECORD_SIZE_BYTES;
import static io.netty.util.internal.EmptyArrays.EMPTY_BYTES;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.10.17.
 */
public final class RawEntry {

    public final long ts;

    public final double value;

    public RawEntry(long ts, double value) {
        this.ts = ts;
        this.value = value;
    }

    public long getTs() {
        return ts;
    }

    public double getValue() {
        return value;
    }

    public static byte[] convert(Collection<RawEntry> rawEntries) {
        if (rawEntries == null || rawEntries.size() == 0) {
            return EMPTY_BYTES;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + rawEntries.size() * REPORTING_RECORD_SIZE_BYTES);
        byteBuffer.putInt(rawEntries.size());
        for (RawEntry rawEntry : rawEntries) {
            byteBuffer.putDouble(rawEntry.getValue());
            byteBuffer.putLong(rawEntry.getTs());
        }
        return byteBuffer.array();
    }
}
