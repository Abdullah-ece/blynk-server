package cc.blynk.server.internal.token;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.18.
 */
public final class UploadTempToken extends BaseToken implements Serializable {

    private static final long UPLOAD_EXPIRE_TIME = TimeUnit.MINUTES.toMillis(5);

    public UploadTempToken(String email) {
        super(email, UPLOAD_EXPIRE_TIME);
    }

    @Override
    public String toString() {
        return "UploadTempToken{"
                + "email='" + email + '\''
                + '}';
    }

}
