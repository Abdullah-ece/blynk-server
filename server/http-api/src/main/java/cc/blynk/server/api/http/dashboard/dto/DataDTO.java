package cc.blynk.server.api.http.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.17.
 */
public class DataDTO<T> {

    public final T data;

    @JsonCreator
    public DataDTO(@JsonProperty("data") T data) {
        this.data = data;
    }

}
