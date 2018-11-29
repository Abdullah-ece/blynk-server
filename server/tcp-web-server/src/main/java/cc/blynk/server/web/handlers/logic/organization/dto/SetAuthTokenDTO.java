package cc.blynk.server.web.handlers.logic.organization.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetAuthTokenDTO {

    public final int orgId;

    public final int deviceId;

    public final String token;

    @JsonCreator
    public SetAuthTokenDTO(@JsonProperty("orgId") int orgId,
                           @JsonProperty("deviceId") int deviceId,
                           @JsonProperty("token") String token) {
        this.orgId = orgId;
        this.deviceId = deviceId;
        this.token = token;
    }

    public boolean isValid() {
        return token != null && token.length() == 32;
    }
}
