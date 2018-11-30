package cc.blynk.server.internal.token;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class OTADownloadToken extends BaseToken implements Serializable {

    private static final long DOWNLOAD_EXPIRE_TIME = TimeUnit.DAYS.toMillis(30);

    public final int deviceId;

    public OTADownloadToken(int deviceId) {
        super(DOWNLOAD_EXPIRE_TIME);
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "OTADownloadToken{"
                + "deviceId='" + deviceId + '\''
                + '}';
    }

}
