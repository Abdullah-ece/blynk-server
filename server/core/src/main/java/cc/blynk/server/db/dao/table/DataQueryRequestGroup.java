package cc.blynk.server.db.dao.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 09.10.17.
 */
public class DataQueryRequestGroup {

    public final DataQueryRequest[] dataQueryRequests;

    @JsonCreator
    public DataQueryRequestGroup(@JsonProperty("dataQueryRequests") DataQueryRequest[] dataQueryRequests) {
        this.dataQueryRequests = dataQueryRequests;
    }

    public void setDeviceId(int deviceId) {
        for (DataQueryRequest dataQueryRequest : dataQueryRequests) {
            dataQueryRequest.setDeviceId(deviceId);
        }
    }

    public boolean isNotValid() {
        if (dataQueryRequests == null || dataQueryRequests.length == 0) {
            return true;
        }

        for (DataQueryRequest dataQueryRequest : dataQueryRequests) {
            if (dataQueryRequest.isNotValid()) {
                return true;
            }
        }

        return false;
    }
}
