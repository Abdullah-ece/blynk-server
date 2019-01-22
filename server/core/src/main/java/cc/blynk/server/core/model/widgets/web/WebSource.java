package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

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

        if (connectMissingPointsEnabled != webSource.connectMissingPointsEnabled) {
            return false;
        }
        if (limit != webSource.limit) {
            return false;
        }
        if (autoscale != webSource.autoscale) {
            return false;
        }
        if (enableYAxis != webSource.enableYAxis) {
            return false;
        }
        if (label != null ? !label.equals(webSource.label) : webSource.label != null) {
            return false;
        }
        if (color != null ? !color.equals(webSource.color) : webSource.color != null) {
            return false;
        }
        if (sourceType != webSource.sourceType) {
            return false;
        }
        if (dataStream != null ? !dataStream.equals(webSource.dataStream) : webSource.dataStream != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(selectedColumns, webSource.selectedColumns)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(groupByFields, webSource.groupByFields)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(sortByFields, webSource.sortByFields)) {
            return false;
        }
        if (sortOrder != webSource.sortOrder) {
            return false;
        }
        return lineGraphType == webSource.lineGraphType;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (connectMissingPointsEnabled ? 1 : 0);
        result = 31 * result + (sourceType != null ? sourceType.hashCode() : 0);
        result = 31 * result + (dataStream != null ? dataStream.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(selectedColumns);
        result = 31 * result + Arrays.hashCode(groupByFields);
        result = 31 * result + Arrays.hashCode(sortByFields);
        result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
        result = 31 * result + limit;
        result = 31 * result + (autoscale ? 1 : 0);
        result = 31 * result + (lineGraphType != null ? lineGraphType.hashCode() : 0);
        result = 31 * result + (enableYAxis ? 1 : 0);
        return result;
    }
}
