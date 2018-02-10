package cc.blynk.server.api.http.dashboard.dto;

import cc.blynk.server.db.dao.descriptor.DataQueryRequestDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class DataQueryRequestGroupDTO {

    public final DataQueryRequestDTO[] dataQueryRequests;

    @JsonCreator
    public DataQueryRequestGroupDTO(@JsonProperty("dataQueryRequests") DataQueryRequestDTO[] dataQueryRequests) {
        this.dataQueryRequests = dataQueryRequests;
    }

    public void setDeviceId(int deviceId) {
        for (DataQueryRequestDTO dataQueryRequest : dataQueryRequests) {
            dataQueryRequest.setDeviceId(deviceId);
        }
    }

    public boolean isNotValid() {
        if (dataQueryRequests == null || dataQueryRequests.length == 0) {
            return true;
        }

        for (DataQueryRequestDTO dataQueryRequest : dataQueryRequests) {
            if (dataQueryRequest.isNotValid()) {
                return true;
            }
        }

        return false;
    }
}
