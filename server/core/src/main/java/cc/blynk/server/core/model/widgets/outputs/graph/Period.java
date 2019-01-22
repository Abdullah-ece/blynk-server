package cc.blynk.server.core.model.widgets.outputs.graph;

import static cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType.DAILY;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType.HOURLY;
import static cc.blynk.server.core.model.widgets.outputs.graph.GraphGranularityType.MINUTE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.08.15.
 */
public enum Period {

    LIVE(60, MINUTE),
    FIFTEEN_MINUTES(15, MINUTE),
    THIRTY_MINUTES(30, MINUTE),
    ONE_HOUR(60, MINUTE),
    THREE_HOURS(3 * 60, MINUTE),
    SIX_HOURS(6 * 60, MINUTE),
    TWELVE_HOURS(12 * 60, MINUTE),

    DAY(24 * 60, MINUTE),
    @Deprecated
    THREE_DAYS(24 * 3, HOURLY),
    WEEK(24 * 7, HOURLY),
    TWO_WEEKS(24 * 14, HOURLY),
    MONTH(30 * 24, HOURLY),
    THREE_MONTHS(3 * 30 * 24, HOURLY),
    @Deprecated
    ALL(12 * 30 * 24, HOURLY),

    N_DAY(24, HOURLY),
    TWO_DAYS(2 * 24, HOURLY),
    N_THREE_DAYS(3 * 24, HOURLY),
    N_WEEK(7, DAILY),
    N_TWO_WEEKS(14, DAILY),
    N_MONTH(30, DAILY),
    N_THREE_MONTHS(3 * 30, DAILY),
    SIX_MONTHS(6 * 30, DAILY),
    ONE_YEAR(12 * 30, DAILY),

    CUSTOM(-1, DAILY);

    public final int numberOfPoints;
    public final GraphGranularityType granularityType;
    public final long millis;

    Period(int numberOfPoints, GraphGranularityType granularityType) {
        this.numberOfPoints = numberOfPoints;
        this.granularityType = granularityType;
        this.millis = numberOfPoints * getMillisForGranularity(granularityType);
    }

    private static long getMillisForGranularity(GraphGranularityType granularityType) {
        switch (granularityType) {
            case MINUTE:
                return 60 * 1000L;
            case HOURLY:
                return 60 * 60 * 1000L;
            default:
                return 24 * 60 * 60 * 1000L;
        }
    }

    private static final Period[] CUSTOM_DATE_PERIODS = new Period[] {
            FIFTEEN_MINUTES,
            THIRTY_MINUTES,
            ONE_HOUR,
            THREE_HOURS,
            SIX_HOURS,
            TWELVE_HOURS,
            N_DAY,
            TWO_DAYS,
            N_THREE_DAYS,
            N_WEEK,
            N_TWO_WEEKS,
            N_MONTH,
            N_THREE_MONTHS,
            SIX_MONTHS,
            ONE_YEAR
    };

    public static Period calcGraphPeriod(long from, long to) {
        long diff = to - from;
        for (var graphPeriod : CUSTOM_DATE_PERIODS) {
            if (diff < graphPeriod.millis) {
                return graphPeriod;
            }
        }

        return Period.ONE_YEAR;
    }
}
