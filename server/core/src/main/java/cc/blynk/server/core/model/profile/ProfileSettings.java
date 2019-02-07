package cc.blynk.server.core.model.profile;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.others.rtc.StringToZoneId;
import cc.blynk.server.core.model.widgets.others.rtc.ZoneIdToString;
import cc.blynk.utils.DateTimeUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.12.18.
 */
public class ProfileSettings {

    public volatile NotificationSettings notificationSettings = new NotificationSettings();

    @JsonSerialize(using = ZoneIdToString.class)
    @JsonDeserialize(using = StringToZoneId.class, as = ZoneId.class)
    public volatile ZoneId tzName;

    public void update(ProfileSettings profileSettings) {
        this.notificationSettings = new NotificationSettings(
                this.notificationSettings.androidTokens, //this field is preserved
                this.notificationSettings.iOSTokens,     //this field is preserved
                profileSettings.notificationSettings.notifyWhenOffline,
                profileSettings.notificationSettings.notifyWhenOfflineIgnorePeriod,
                profileSettings.notificationSettings.priority
        );
        this.tzName = profileSettings.tzName;
    }

    public long getTime() {
        ZoneId zone;
        if (tzName != null) {
            zone = tzName;
        } else {
            zone = DateTimeUtils.UTC;
        }

        LocalDateTime ldt = LocalDateTime.now(zone);
        return ldt.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
