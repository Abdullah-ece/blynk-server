package cc.blynk.server.core.model.profile;

import cc.blynk.server.core.model.serialization.View;
import cc.blynk.server.notifications.push.enums.Priority;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    public final ConcurrentHashMap<String, String> androidTokens;

    @JsonView({View.Private.class, View.HttpAPIField.class})
    public final ConcurrentHashMap<String, String> iOSTokens;

    public final boolean notifyWhenOffline;

    public final int notifyWhenOfflineIgnorePeriod;

    public final Priority priority;

    public NotificationSettings() {
        this.androidTokens = new ConcurrentHashMap<>();
        this.iOSTokens = new ConcurrentHashMap<>();
        this.notifyWhenOffline = false;
        this.notifyWhenOfflineIgnorePeriod = 0;
        this.priority = Priority.high;
    }

    @JsonCreator
    public NotificationSettings(@JsonProperty("androidTokens") ConcurrentHashMap<String, String> androidTokens,
                                @JsonProperty("iOSTokens") ConcurrentHashMap<String, String> iOSTokens,
                                @JsonProperty("notifyWhenOffline") boolean notifyWhenOffline,
                                @JsonProperty("notifyWhenOfflineIgnorePeriod") int notifyWhenOfflineIgnorePeriod,
                                @JsonProperty("priority") Priority priority) {
        this.androidTokens = androidTokens == null ? new ConcurrentHashMap<>() : androidTokens;
        this.iOSTokens = iOSTokens == null ? new ConcurrentHashMap<>() : iOSTokens;
        this.notifyWhenOffline = notifyWhenOffline;
        this.notifyWhenOfflineIgnorePeriod = notifyWhenOfflineIgnorePeriod;
        this.priority = priority == null ? Priority.high : priority;
    }

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
