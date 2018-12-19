package cc.blynk.server.notifications.push.ios;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 14.11.17.
 */
class IOSPushNotificationBody {

    public final String body;
    public final int deviceId;
    public final String sound;
    public final String title;

    IOSPushNotificationBody(String title, String body, int deviceId) {
        this.title = title;
        this.body = body;
        this.deviceId = deviceId;
        this.sound = "default";
    }
}
