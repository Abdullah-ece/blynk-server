package cc.blynk.server.web.handlers.logic.organization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenDTO {

    public final String token;

    @JsonCreator
    public TokenDTO(@JsonProperty("token") String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "{\"token\":\"" + token + "\"}";
    }
}
