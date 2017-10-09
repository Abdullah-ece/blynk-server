package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.SortOrder;
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

    public final SourceType sourceType;

    public final String color;

    public final boolean connectMissingPointsEnabled;

    public final DataStream dataStream;

    public final int maxRows;

    public final String[] groupBy;

    public final String[] sortBy;

    public final SortOrder sortOrder;

    @JsonCreator
    public WebSource(@JsonProperty("label") String label,
                     @JsonProperty("sourceType") SourceType sourceType,
                     @JsonProperty("color") String color,
                     @JsonProperty("connectMissingPointsEnabled") boolean connectMissingPointsEnabled,
                     @JsonProperty("dataStream") DataStream dataStream,
                     @JsonProperty("maxRows") int maxRows,
                     @JsonProperty("groupByFields") String[] groupBy,
                     @JsonProperty("sortBy") String[] sortBy,
                     @JsonProperty("sortOrder") SortOrder sortOrder) {

        this.label = label;
        this.sourceType = sourceType;
        this.color = color;
        this.connectMissingPointsEnabled = connectMissingPointsEnabled;
        this.dataStream = dataStream;
        this.maxRows = maxRows;
        this.groupBy = groupBy;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebSource)) {
            return false;
        }
        WebSource webSource = (WebSource) o;
        return connectMissingPointsEnabled == webSource.connectMissingPointsEnabled &&
                maxRows == webSource.maxRows &&
                Objects.equals(label, webSource.label) &&
                sourceType == webSource.sourceType &&
                Objects.equals(color, webSource.color) &&
                Objects.equals(dataStream, webSource.dataStream) &&
                Arrays.equals(groupBy, webSource.groupBy) &&
                Arrays.equals(sortBy, webSource.sortBy) &&
                sortOrder == webSource.sortOrder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, sourceType, color,
                connectMissingPointsEnabled, dataStream,
                maxRows, groupBy, sortBy, sortOrder);
    }
}
