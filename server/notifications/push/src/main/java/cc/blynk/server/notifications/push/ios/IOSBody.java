package cc.blynk.server.notifications.push.ios;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 14.11.17.
 */
class IOSBody {

    private final String body;
    private final int deviceId;
    private final String sound;
    private String title;

    IOSBody(String body, int deviceId) {
        this.body = body;
        this.deviceId = deviceId;
        this.sound = "default";
    }

    void setTitle(String title) {
        this.title = title;
    }
}
