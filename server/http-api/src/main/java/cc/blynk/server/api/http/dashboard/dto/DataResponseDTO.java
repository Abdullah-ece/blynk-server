package cc.blynk.server.api.http.dashboard.dto;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.17.
 */
public class DataResponseDTO {

    private final Object[] response;
    private transient int index = 0;

    public DataResponseDTO(int length) {
        this.response = new Object[length];
    }

    public void add(Object o) {
        this.response[index++] = new DataDTO(o);
    }

    public Object[] data() {
        return response;
    }

}
