package cc.blynk.server.db.dao.table;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class ColumnEntry {
    public final Column column;
    public final Object value;

    ColumnEntry(Column column, Object value) {
        this.column = column;
        this.value = value;
    }
}
