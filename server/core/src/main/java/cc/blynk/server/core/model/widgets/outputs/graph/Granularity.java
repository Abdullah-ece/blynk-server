package cc.blynk.server.core.model.widgets.outputs.graph;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.08.15.
 */
public enum Granularity {

    MINUTE("reporting_average_minute", 60 * 1000),
    FIVE_MINUTE("reporting_average_5_minute", 5 * 60 * 1000),
    FIFTEEN_MINUTE("reporting_average_15_minute", 15 * 60 * 1000),
    HOURLY("reporting_average_hourly", 60 * 60 * 1000),
    DAILY("reporting_average_daily", 24 * 60 * 60 * 1000);

    public final String tableName;
    public final long period;

    //cached value for values field to avoid allocations
    private static final Granularity[] values = values();

    Granularity(String tableName, long period) {
        this.tableName = tableName;
        this.period = period;
    }

    public static Granularity[] getValues() {
        return values;
    }
}
