package cc.blynk.server.db.dao.descriptor;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.10.17.
 */
public class ColumnValueDTO {

    public final String name;
    public final Object value;

    ColumnValueDTO(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
