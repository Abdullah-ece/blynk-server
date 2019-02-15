package cc.blynk.server.db.dao;

import cc.blynk.server.core.model.enums.PinType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 31.10.17.
 */
public final class RawEntryWithPin {

    public final long ts;

    public final double value;

    public final short pin;

    public final PinType pinType;

    public RawEntryWithPin(long ts, double value, short pin, PinType pinType) {
        this.ts = ts;
        this.value = value;
        this.pin = pin;
        this.pinType = pinType;
    }
}
