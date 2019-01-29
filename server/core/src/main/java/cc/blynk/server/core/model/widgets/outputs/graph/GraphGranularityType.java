package cc.blynk.server.core.model.widgets.outputs.graph;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.08.15.
 */
public enum GraphGranularityType {

    MINUTE("reporting_average_minute", 60 * 1000),
    FIVE_MINUTE("reporting_average_5_minute", 5 * 60 * 1000),
    FIFTEEN_MINUTE("reporting_average_15_minute", 15 * 60 * 1000),
    HOURLY("reporting_average_hourly", 60 * 60 * 1000),
    DAILY("daily", 24 * 60 * 60 * 1000);

    public final String tableName;
    public final long period;

    //cached value for values field to avoid allocations
    private static final GraphGranularityType[] values = values();

    GraphGranularityType(String tableName, long period) {
        this.tableName = tableName;
        this.period = period;
    }

    public static GraphGranularityType[] getValues() {
        return values;
    }
}
