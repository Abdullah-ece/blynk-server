package cc.blynk.server.core.model.widgets.outputs.graph;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;

import static cc.blynk.server.core.model.widgets.outputs.graph.Period.LIVE;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.N_DAY;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.N_MONTH;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.N_THREE_MONTHS;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.N_WEEK;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.ONE_HOUR;
import static cc.blynk.server.core.model.widgets.outputs.graph.Period.SIX_HOURS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_GRAPH_DATA_STREAMS;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.08.15.
 */
public class Superchart extends Widget {

    private static final Period[] DEFAULT_PERIODS = new Period[] {
            LIVE, ONE_HOUR, SIX_HOURS, N_DAY, N_WEEK, N_MONTH, N_THREE_MONTHS
    };

    public GraphDataStream[] dataStreams = EMPTY_GRAPH_DATA_STREAMS;

    public Period period;

    public TextAlignment textAlignment;

    public FontSize fontSize;

    public Stacking stacking;

    public boolean showTitle;

    public boolean showLegend;

    public boolean yAxisValues;

    public boolean xAxisValues;

    public boolean showXAxis;

    public boolean allowFullScreen;

    public boolean overrideYAxis;

    public boolean hideGradient;

    public float yAxisMin;

    public float yAxisMax;

    public boolean isPercentMaxMin;

    public String goalText;

    public GoalLine goalLine;

    public LineType lineType;

    public Period[] selectedPeriods = DEFAULT_PERIODS;

    public boolean hasLivePeriodsSelected() {
        for (Period period : selectedPeriods) {
            if (period == LIVE) {
                return true;
            }
        }
        return false;
    }

    @Override
    //do not performs any direct pin operations
    public PinMode getModeType() {
        return null;
    }

    @Override
    public int getPrice() {
        return 900;
    }

    @Override
    public void updateValue(Widget oldWidget) {
    }

    @Override
    public void erase() {
    }

    @Override
    public boolean isAssignedToDevice(int deviceId) {
        return false;
    }
}
