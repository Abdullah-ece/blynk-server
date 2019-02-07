package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class DataQueryRequestDTO {

    public final AggregationFunctionType sourceType;
    public final PinType pinType;
    public final short pin;
    public final SelectedColumn[] selectedColumns;
    public final SelectedColumn[] groupByFields;
    public final SelectedColumn[] sortByFields;
    public final SortOrder sortOrder;

    public final int offset;
    public final int limit;

    public final long from;
    public final long to;

    public int deviceId;

    public DataQueryRequestDTO(AggregationFunctionType sourceType,
                               int deviceId,
                               PinType pinType,
                               short pin,
                               SelectedColumn[] selectedColumns,
                               SelectedColumn[] groupByFields,
                               SelectedColumn[] sortByFields,
                               SortOrder sortOrder,
                               int offset, int limit,
                               long from, long to) {
        this(sourceType, pinType, pin, selectedColumns,
                groupByFields, sortByFields, sortOrder, offset, limit, from, to);
        this.deviceId = deviceId;
    }

    @JsonCreator
    public DataQueryRequestDTO(@JsonProperty("sourceType") AggregationFunctionType sourceType,
                               @JsonProperty("pinType") PinType pinType,
                               @JsonProperty("pin") short pin,
                               @JsonProperty("selectedColumns") SelectedColumn[] selectedColumns,
                               @JsonProperty("groupByFields") SelectedColumn[] groupByFields,
                               @JsonProperty("sortByFields") SelectedColumn[] sortByFields,
                               @JsonProperty("sortOrder") SortOrder sortOrder,
                               @JsonProperty("offset") int offset,
                               @JsonProperty("limit") int limit,
                               @JsonProperty("from") long from,
                               @JsonProperty("to") long to) {

        this.sourceType = sourceType;
        this.pinType = pinType;
        this.pin = pin;
        this.selectedColumns = selectedColumns;
        this.groupByFields = groupByFields;
        this.sortByFields = sortByFields;
        this.sortOrder = sortOrder;
        this.offset = offset;
        this.limit = limit;
        this.from = from;
        this.to = to;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isNotValid() {
        return pinType == null || pin == -1;
    }

}
