package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.widgets.web.SelectedColumnDTO;
import cc.blynk.server.core.model.widgets.web.SourceType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class DataQueryRequestDTO {

    public final SourceType sourceType;
    public final PinType pinType;
    public final byte pin;
    public final SelectedColumnDTO[] selectedColumns;

    public final long from;
    public final long to;

    public final SelectedColumnDTO[] groupByFields;
    public final SelectedColumnDTO[] sortByFields;
    public final SortOrder sortOrder;

    public final int offset;
    public final int limit;

    public transient TableDescriptor tableDescriptor;
    public int deviceId;

    public DataQueryRequestDTO(int deviceId,
                               PinType pinType,
                               byte pin,
                               SelectedColumnDTO[] selectedColumns,
                               long from, long to,
                               SourceType sourceType,
                               SelectedColumnDTO[] groupByFields,
                               SelectedColumnDTO[] sortByFields,
                               SortOrder sortOrder,
                               int offset, int limit,
                               TableDescriptor tableDescriptor) {
        this(pinType, pin, selectedColumns, from, to, sourceType,
                groupByFields, sortByFields, sortOrder, offset, limit);
        this.deviceId = deviceId;
    }

    @JsonCreator
    public DataQueryRequestDTO(@JsonProperty("pinType") PinType pinType,
                               @JsonProperty("pin") byte pin,
                               @JsonProperty("selectedColumns") SelectedColumnDTO[] selectedColumns,
                               @JsonProperty("from") long from,
                               @JsonProperty("to") long to,
                               @JsonProperty("sourceType") SourceType sourceType,
                               @JsonProperty("groupByFields") SelectedColumnDTO[] groupByFields,
                               @JsonProperty("sortByFields") SelectedColumnDTO[] sortByFields,
                               @JsonProperty("sortOrder") SortOrder sortOrder,
                               @JsonProperty("offset") int offset,
                               @JsonProperty("limit") int limit) {

        //todo remove hardcode
        if (tableDescriptor == null) {
            if (pinType == PinType.VIRTUAL && pin == 100) {
                this.tableDescriptor = TableDescriptor.KNIGHT_INSTANCE;
            } else {
                this.tableDescriptor = TableDescriptor.BLYNK_DEFAULT_INSTANCE;
            }
        }

        this.pinType = pinType;
        this.pin = pin;
        this.selectedColumns = selectedColumns;
        this.from = from;
        this.to = to;
        this.sourceType = sourceType == null ? SourceType.RAW_DATA : sourceType;
        this.groupByFields = groupByFields;
        this.sortByFields = sortByFields;
        this.sortOrder = sortOrder;
        this.offset = offset;
        this.limit = limit;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isNotValid() {
        return pinType == null || pin == -1;
    }

    public String name() {
        return "" + Character.toUpperCase(pinType.pintTypeChar) + pin;
    }
}
