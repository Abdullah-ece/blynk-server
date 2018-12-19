package cc.blynk.server.notifications.push.android;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.05.17.
 */
class AndroidPushNotificationBody {

    public final String message;
    public final int deviceId;
    public final String title;

    AndroidPushNotificationBody(String title, String message, int deviceId) {
        this.title = title;
        this.message = message;
        this.deviceId = deviceId;
    }

}
