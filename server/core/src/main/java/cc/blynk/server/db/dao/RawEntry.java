package cc.blynk.server.db.dao;

import java.sql.Timestamp;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.10.17.
 */
public class RawEntry {

    private final long key;

    private final double value;

    public RawEntry(Timestamp key, double value) {
        this.key = key.getTime();
        this.value = value;
    }

    public long getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }
}
