package cc.blynk.server.http.web.model;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 20.06.17.
 */
public class WebEmail {

    public final String email;

    @JsonCreator
    public WebEmail(@JsonProperty("email") String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
