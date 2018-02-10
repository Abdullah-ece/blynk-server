package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.06.17.
 */
public class CommentDTO {

    public final String comment;

    @JsonCreator
    public CommentDTO(@JsonProperty("comment") String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
