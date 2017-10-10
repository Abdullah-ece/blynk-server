package cc.blynk.server.db.dao.descriptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class TableDataMapper {

    private static final Logger log = LogManager.getLogger(TableDataMapper.class);

    public final TableDescriptor tableDescriptor;
    public final ColumnValueDTO[] data;

    public TableDataMapper(TableDescriptor tableDescriptor, String[] values) {
        this.tableDescriptor = tableDescriptor;
        data = new ColumnValueDTO[values.length];
        for (int i = 0; i < values.length; i++) {
            Column column = tableDescriptor.columns[i];
            Object value = column.parse(values[i]);
            log.trace("In {}, out {}. Type {}", values[i], value, value.getClass().getSimpleName());
            data[i] = new ColumnValueDTO(column.columnName, value);
        }
    }

}
