package cc.blynk.server.core.model.widgets.ui.reporting.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.05.18.
 */
public class MonthlyReportType extends DailyReportType {

    public final int dayOfTheMonth;

    @JsonCreator
    public MonthlyReportType(@JsonProperty("atTime") long atTime,
                             @JsonProperty("durationType") ReportDurationType durationType,
                             @JsonProperty("startTs") long startTs,
                             @JsonProperty("endTs") long endTs,
                             @JsonProperty("dayOfTheMonth") int dayOfTheMonth) {
        super(atTime, durationType, startTs, endTs);
        this.dayOfTheMonth = dayOfTheMonth;
    }
}
