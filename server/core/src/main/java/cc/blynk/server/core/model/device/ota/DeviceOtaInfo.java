package cc.blynk.server.core.model.device.ota;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 17.08.17.
 */
public class DeviceOtaInfo {

    public final String otaInitiatedBy;

    public final long otaInitiatedAt;

    public final long otaUpdateAt;

    public volatile OTAStatus otaStatus;

    @JsonCreator
    public DeviceOtaInfo(@JsonProperty("otaInitiatedBy") String otaInitiatedBy,
                         @JsonProperty("otaInitiatedAt") long otaInitiatedAt,
                         @JsonProperty("otaUpdateAt") long otaUpdateAt,
                         @JsonProperty("otaStatus") OTAStatus otaStatus) {
        this.otaInitiatedBy = otaInitiatedBy;
        this.otaInitiatedAt = otaInitiatedAt;
        this.otaUpdateAt = otaUpdateAt;
        this.otaStatus = otaStatus == null ? OTAStatus.NEVER : otaStatus;
    }

}
