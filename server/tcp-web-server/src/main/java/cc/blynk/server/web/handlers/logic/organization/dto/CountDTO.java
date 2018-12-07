package cc.blynk.server.web.handlers.logic.organization.dto;

import cc.blynk.server.core.model.serialization.JsonParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CountDTO {

    public final int orgCount;

    public final int subOrgCount;

    @JsonCreator
    public CountDTO(@JsonProperty("orgCount") int orgCount,
                    @JsonProperty("subOrgCount") int subOrgCount) {
        this.orgCount = orgCount;
        this.subOrgCount = subOrgCount;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
