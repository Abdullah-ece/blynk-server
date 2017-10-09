package cc.blynk.server.db.dao.table;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.web.SourceType;
import cc.blynk.server.internal.ParseUtil;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class DataQueryRequest {

    public final int deviceId;
    public final PinType pinType;
    public final byte pin;
    public final String columnLabel;
    public final long from;
    public final long to;
    public final SourceType sourceType;
    public final int offset;
    public final int limit;
    public final TableDescriptor tableDescriptor;

    public DataQueryRequest(int deviceId, String dataStream,
                            long from, long to, SourceType sourceType, int offset, int limit) {

        String[] split = dataStream.split("\\."); //expecting something like "V1.Load Weight"
        String dataStreamName = split[0]; //"V1" for example.

        if (dataStreamName.equalsIgnoreCase("V100")) {
            this.tableDescriptor = TableDescriptor.KNIGHT_INSTANCE;
        } else {
            this.tableDescriptor = TableDescriptor.BLYNK_DEFAULT_INSTANCE;
        }

        this.deviceId = deviceId;
        this.pinType = PinType.getPinType(dataStreamName.charAt(0));
        this.pin = ParseUtil.parseByte(dataStreamName.substring(1));
        this.columnLabel = split.length == 2 ? split[1] : null;
        this.from = from;
        this.to = to;
        this.sourceType = sourceType == null ? SourceType.RAW_DATA : sourceType;
        this.offset = offset;
        this.limit = limit;
    }
}
