package cc.blynk.server.core.model.profile;

import cc.blynk.server.core.model.serialization.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.12.18.
 */
public class ProfileSettings {

    public volatile NotificationSettings notificationSettings = new NotificationSettings();

    public void update(ProfileSettings profileSettings) {
        this.notificationSettings = new NotificationSettings(
                this.notificationSettings.androidTokens, //this field is preserved
                this.notificationSettings.iOSTokens,     //this field is preserved
                profileSettings.notificationSettings.notifyWhenOffline,
                profileSettings.notificationSettings.notifyWhenOfflineIgnorePeriod,
                profileSettings.notificationSettings.priority,
                profileSettings.notificationSettings.soundUri
        );
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
