package cc.blynk.server.db.dao.table;

import java.sql.JDBCType;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class Column {

    public final String columnName;
    public final JDBCType type;

    public Column(String columnName, JDBCType type) {
        this.columnName = columnName;
        this.type = type;
    }
}
