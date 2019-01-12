package cc.blynk.server.db.dao;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.10.17.
 */
public class RawEntry {

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
}
