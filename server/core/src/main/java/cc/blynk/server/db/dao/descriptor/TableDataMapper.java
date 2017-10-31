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

    private TableDataMapper(TableDescriptor tableDescriptor,
                            int deviceId, byte pin, PinType pinType,
                            LocalDateTime now,
                            int size) {
        this.tableDescriptor = tableDescriptor;
        this.data = new Object[size];

        this.data[0] = deviceId;
        this.data[1] = pin;
        this.data[2] = pinType.ordinal();
        this.data[3] = now;
    }

    public TableDataMapper(TableDescriptor tableDescriptor,
                           int deviceId, byte pin, PinType pinType, LocalDateTime now, Object value) {
        this(tableDescriptor, deviceId, pin, pinType, now, BLYNK_PARAMS_COUNT + 1);
        init(value, 0);
    }

    public TableDataMapper(TableDescriptor tableDescriptor,
                           int deviceId, byte pin, PinType pinType, LocalDateTime now, Object[] values) {
        this(tableDescriptor, deviceId, pin, pinType, now, BLYNK_PARAMS_COUNT + values.length);
        init(values);
    }

    private void init(Object[] values) {
        for (int i = 0; i < values.length; i++) {
            init(values[i], i);
        }
    }

    private void init(Object value, int i) {
        Column column = tableDescriptor.columns[i + BLYNK_PARAMS_COUNT];
        Object castedValue = column.parse(value);
        log.trace("In {}, out {}. Type {}", value, castedValue, castedValue.getClass().getSimpleName());
        data[i + BLYNK_PARAMS_COUNT] = castedValue;
    }

}
