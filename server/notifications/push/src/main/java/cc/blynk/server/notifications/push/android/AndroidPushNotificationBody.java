package cc.blynk.server.notifications.push.android;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 06.05.17.
 */
class AndroidPushNotificationBody {

    private final String message;
    private final int deviceId;

    AndroidPushNotificationBody(String message, int deviceId) {
        this.message = message;
        this.deviceId = deviceId;
    }

}
