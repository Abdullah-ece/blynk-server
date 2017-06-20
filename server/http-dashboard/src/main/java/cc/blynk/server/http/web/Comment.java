package cc.blynk.server.http.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.06.17.
 */
public class Comment {

    public final String comment;

    @JsonCreator
    public Comment(@JsonProperty("comment") String comment) {
        this.comment = comment;
    }
}
