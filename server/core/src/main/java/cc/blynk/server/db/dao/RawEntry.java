package cc.blynk.server.db.dao;

import java.nio.ByteBuffer;
import java.util.Collection;

import static cc.blynk.utils.ByteUtils.REPORTING_RECORD_SIZE_BYTES;

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

    public static ByteBuffer convertToByteBuffer(Collection<RawEntry> rawEntries) {
        if (rawEntries == null || rawEntries.size() == 0) {
            return ByteBuffer.allocate(0);
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(rawEntries.size() * REPORTING_RECORD_SIZE_BYTES);
        for (RawEntry rawEntry : rawEntries) {
            byteBuffer.putDouble(rawEntry.getValue());
            byteBuffer.putLong(rawEntry.getTs());
        }
        return byteBuffer;
    }

    public static byte[] convert(Collection<RawEntry> rawEntries) {
        return convertToByteBuffer(rawEntries).array();
    }
}
