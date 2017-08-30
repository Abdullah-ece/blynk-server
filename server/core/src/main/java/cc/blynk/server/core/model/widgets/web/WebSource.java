package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.widgets.outputs.graph.GraphType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 30.08.17.
 */
public class WebSource {

    public final String label;

    public final SourceType sourceType;

    public final String color;

    public final GraphType graphType;

    public final boolean connectMissingPointsEnabled;

    public final DataStream dataStream;

    @JsonCreator
    public WebSource(@JsonProperty("label") String label,
                     @JsonProperty("sourceType") SourceType sourceType,
                     @JsonProperty("color") String color,
                     @JsonProperty("graphType") GraphType graphType,
                     @JsonProperty("connectMissingPointsEnabled") boolean connectMissingPointsEnabled,
                     @JsonProperty("dataStream") DataStream dataStream) {
        this.label = label;
        this.sourceType = sourceType;
        this.color = color;
        this.graphType = graphType;
        this.connectMissingPointsEnabled = connectMissingPointsEnabled;
        this.dataStream = dataStream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebSource)) return false;

        WebSource webSource = (WebSource) o;

        if (connectMissingPointsEnabled != webSource.connectMissingPointsEnabled) return false;
        if (label != null ? !label.equals(webSource.label) : webSource.label != null) return false;
        if (sourceType != webSource.sourceType) return false;
        if (color != null ? !color.equals(webSource.color) : webSource.color != null) return false;
        if (graphType != webSource.graphType) return false;
        return dataStream != null ? dataStream.equals(webSource.dataStream) : webSource.dataStream == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (sourceType != null ? sourceType.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (graphType != null ? graphType.hashCode() : 0);
        result = 31 * result + (connectMissingPointsEnabled ? 1 : 0);
        result = 31 * result + (dataStream != null ? dataStream.hashCode() : 0);
        return result;
    }
}
