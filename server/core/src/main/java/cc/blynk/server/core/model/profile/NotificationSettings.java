package cc.blynk.server.core.model.profile;

import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.notifications.push.enums.Priority;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.12.18.
 */
public class NotificationSettings {

    private static final int MAX_PUSH_BODY_SIZE = 255;

    @JsonView({View.Private.class, View.HttpAPIField.class})
    public final ConcurrentHashMap<String, String> androidTokens = new ConcurrentHashMap<>();

    @JsonView({View.Private.class, View.HttpAPIField.class})
    public final ConcurrentHashMap<String, String> iOSTokens = new ConcurrentHashMap<>();

    public boolean notifyWhenOffline;

    public int notifyWhenOfflineIgnorePeriod;

    public Priority priority = Priority.normal;

    public String soundUri;

    public static boolean isWrongBody(String body) {
        return body == null || body.isEmpty() || body.length() > MAX_PUSH_BODY_SIZE;
    }

    public boolean hasNoToken() {
        return iOSTokens.size() == 0 && androidTokens.size() == 0;
    }

    public void clear(String uid) {
        if (uid == null || uid.isEmpty()) {
            this.androidTokens.clear();
            this.iOSTokens.clear();
        } else {
            this.androidTokens.remove(uid);
            this.iOSTokens.remove(uid);
        }
    }

}
