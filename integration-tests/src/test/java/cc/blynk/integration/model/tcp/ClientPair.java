package cc.blynk.integration.model.tcp;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/4/2015.
 */
public class ClientPair {

    public final TestAppClient appClient;

    public final TestHardClient hardwareClient;

    public final String token;

    public int latestDeviceId;

    public ClientPair(TestAppClient appClient, TestHardClient hardwareClient, String token) {
        this.appClient = appClient;
        this.hardwareClient = hardwareClient;
        this.token = token;
    }

    public ClientPair(TestAppClient appClient, TestHardClient hardwareClient, String token, int latestDeviceId) {
        this(appClient, hardwareClient, token);
        this.latestDeviceId = latestDeviceId;
    }

    public void stop() {
        appClient.stop();
        hardwareClient.stop();
    }

}
