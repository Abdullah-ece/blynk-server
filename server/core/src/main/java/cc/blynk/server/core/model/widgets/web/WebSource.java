package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.08.17.
 */
public class WebSource {

    public final String label;

    public final String color;

    public final boolean connectMissingPointsEnabled;

    public final AggregationFunctionType sourceType;

    public final DataStream dataStream;

    public final SelectedColumn[] selectedColumns;

    public final SelectedColumn[] groupByFields;

    public final SelectedColumn[] sortByFields;

    public final SortOrder sortOrder;

    public final int limit;

    public final boolean autoscale;

    public final LineGraphType lineGraphType;

    public final boolean enableYAxis;

    @JsonCreator
    public WebSource(@JsonProperty("label") String label,
                     @JsonProperty("color") String color,
                     @JsonProperty("connectMissingPointsEnabled") boolean connectMissingPointsEnabled,
                     @JsonProperty("sourceType") AggregationFunctionType sourceType,
                     @JsonProperty("dataStream") DataStream dataStream,
                     @JsonProperty("selectedColumns") SelectedColumn[] selectedColumns,
                     @JsonProperty("groupByFields") SelectedColumn[] groupByFields,
                     @JsonProperty("sortByFields") SelectedColumn[] sortByFields,
                     @JsonProperty("sortOrder") SortOrder sortOrder,
                     @JsonProperty("limit") int limit,
                     @JsonProperty("autoscale") boolean autoscale,
                     @JsonProperty("lineGraphType") LineGraphType lineGraphType,
                     @JsonProperty("enableYAxis") boolean enableYAxis) {

        this.label = label;
        this.color = color;
        this.connectMissingPointsEnabled = connectMissingPointsEnabled;
        this.sourceType = sourceType;
        this.dataStream = dataStream;
        this.selectedColumns = selectedColumns;
        this.groupByFields = groupByFields;
        this.sortByFields = sortByFields;
        this.sortOrder = sortOrder;
        this.limit = limit;
        this.autoscale = autoscale;
        this.lineGraphType = lineGraphType;
        this.enableYAxis = enableYAxis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSource webSource = (WebSource) o;
        return connectMissingPointsEnabled == webSource.connectMissingPointsEnabled
                && limit == webSource.limit
                && autoscale == webSource.autoscale
                && enableYAxis == webSource.enableYAxis
                && Objects.equals(label, webSource.label)
                && Objects.equals(color, webSource.color)
                && sourceType == webSource.sourceType
                && Objects.equals(dataStream, webSource.dataStream)
                && Arrays.equals(selectedColumns, webSource.selectedColumns)
                && Arrays.equals(groupByFields, webSource.groupByFields)
                && Arrays.equals(sortByFields, webSource.sortByFields)
                && sortOrder == webSource.sortOrder
                && lineGraphType == webSource.lineGraphType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, color, connectMissingPointsEnabled,
                sourceType, dataStream, selectedColumns,
                groupByFields, sortByFields, sortOrder, limit, autoscale, lineGraphType, enableYAxis);
    }
}
