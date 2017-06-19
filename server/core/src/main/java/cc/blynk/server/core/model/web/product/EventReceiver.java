package cc.blynk.server.core.model.web.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 5/12/17.
 */
public class EventReceiver {

    public final int id;

    public final MetadataType type;

    public final String value;

    @JsonCreator
    public EventReceiver(@JsonProperty("id") int id,
                         @JsonProperty("type") MetadataType type,
                         @JsonProperty("value") String value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }
}
