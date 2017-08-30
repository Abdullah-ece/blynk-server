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

    public final SourceType sourceType;

    public final int color;

    public final GraphType graphType;

    public final boolean connectMissingPointsEnabled;

    public final DataStream dataStream;

    @JsonCreator
    public WebSource(@JsonProperty("sourceType") SourceType sourceType,
                     @JsonProperty("color") int color,
                     @JsonProperty("graphType") GraphType graphType,
                     @JsonProperty("connectMissingPointsEnabled") boolean connectMissingPointsEnabled,
                     @JsonProperty("dataStream") DataStream dataStream) {
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

        WebSource that = (WebSource) o;

        if (color != that.color) return false;
        if (connectMissingPointsEnabled != that.connectMissingPointsEnabled) return false;
        if (sourceType != that.sourceType) return false;
        if (graphType != that.graphType) return false;
        return dataStream != null ? dataStream.equals(that.dataStream) : that.dataStream == null;
    }

    @Override
    public int hashCode() {
        int result = sourceType != null ? sourceType.hashCode() : 0;
        result = 31 * result + color;
        result = 31 * result + (graphType != null ? graphType.hashCode() : 0);
        result = 31 * result + (connectMissingPointsEnabled ? 1 : 0);
        result = 31 * result + (dataStream != null ? dataStream.hashCode() : 0);
        return result;
    }
}
