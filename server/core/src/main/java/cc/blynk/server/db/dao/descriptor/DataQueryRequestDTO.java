package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.SortOrder;
import cc.blynk.server.core.model.widgets.web.SelectedColumn;
import cc.blynk.server.core.model.widgets.web.SourceType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.server.db.dao.descriptor.TableDescriptor.getTableByPin;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class DataQueryRequestDTO {

    public final SourceType sourceType;
    public final PinType pinType;
    public final byte pin;
    public final SelectedColumn[] selectedColumns;
    public final SelectedColumn[] groupByFields;
    public final SelectedColumn[] sortByFields;
    public final SortOrder sortOrder;

    public final int offset;
    public final int limit;

    public final long from;
    public final long to;

    public transient TableDescriptor tableDescriptor;
    public int deviceId;

    public DataQueryRequestDTO(SourceType sourceType,
                               int deviceId,
                               PinType pinType,
                               byte pin,
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
    public DataQueryRequestDTO(@JsonProperty("sourceType") SourceType sourceType,
                               @JsonProperty("pinType") PinType pinType,
                               @JsonProperty("pin") byte pin,
                               @JsonProperty("selectedColumns") SelectedColumn[] selectedColumns,
                               @JsonProperty("groupByFields") SelectedColumn[] groupByFields,
                               @JsonProperty("sortByFields") SelectedColumn[] sortByFields,
                               @JsonProperty("sortOrder") SortOrder sortOrder,
                               @JsonProperty("offset") int offset,
                               @JsonProperty("limit") int limit,
                               @JsonProperty("from") long from,
                               @JsonProperty("to") long to) {

        this.sourceType = sourceType == null ? SourceType.RAW_DATA : sourceType;
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

        //todo remove hardcode
        if (this.tableDescriptor == null) {
            this.tableDescriptor = getTableByPin(pin, pinType);
        }
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isNotValid() {
        return pinType == null || pin == -1;
    }

}
