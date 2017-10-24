package cc.blynk.server.db.dao.descriptor;

import cc.blynk.server.core.model.enums.PinType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDataMapper {

    private static final Logger log = LogManager.getLogger(TableDataMapper.class);

    public final TableDescriptor tableDescriptor;
    public final Object[] data;

    private static final int BLYNK_PARAMS_COUNT = 4;

    public TableDataMapper(TableDescriptor tableDescriptor,
                           int deviceId, byte pin, PinType pinType, LocalDateTime ts, Object[] values) {
        this.tableDescriptor = tableDescriptor;
        data = new Object[BLYNK_PARAMS_COUNT + values.length];

        data[0] = deviceId;
        data[1] = pin;
        data[2] = pinType.ordinal();
        data[3] = ts;

        init(values, BLYNK_PARAMS_COUNT);
    }

    private void init(Object[] values, int skipColumnIndex) {
        for (int i = 0; i < values.length; i++) {
            Column column = tableDescriptor.columns[i + skipColumnIndex];
            Object value = column.parse(values[i]);
            log.trace("In {}, out {}. Type {}", values[i], value, value.getClass().getSimpleName());
            data[i + skipColumnIndex] = value;
        }
    }

}
