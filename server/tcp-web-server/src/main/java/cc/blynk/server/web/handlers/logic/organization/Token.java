package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.core.model.serialization.JsonParser;

public class Token {

    public String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
