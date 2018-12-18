package cc.blynk.server.core.reporting.raw;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.12.18.
 */
public final class BaseReportingValue {

    public final long ts;
    public final double value;

    public BaseReportingValue(long ts, double value) {
        this.ts = ts;
        this.value = value;
    }
}
