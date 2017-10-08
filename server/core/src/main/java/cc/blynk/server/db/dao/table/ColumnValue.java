package cc.blynk.server.db.dao.table;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class ColumnValue {

    public final String name;
    public final Object value;

    ColumnValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
