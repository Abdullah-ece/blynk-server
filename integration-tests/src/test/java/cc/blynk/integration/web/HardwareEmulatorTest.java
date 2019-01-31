package cc.blynk.integration.web;

import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import org.junit.Ignore;
import org.junit.Test;

import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.deviceConnected;
import static cc.blynk.integration.TestUtil.ok;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class HardwareEmulatorTest {

    @Test
    @Ignore
    public void connectHardwareWithProvisionTokenOnTheLocalhost() throws Exception {
        String host = "localhost";
        String user = "iot@blynk.cc";
        String pass = "qa-superpower";

        TestAppClient appClient = new TestAppClient(host, 9443);
        appClient.start();
        appClient.login(user, pass);
        appClient.verifyResult(ok(1));

        Device newDevice = new Device();
        newDevice.name = "My New Device";
        newDevice.boardType = BoardType.ESP32_Dev_Board;

        appClient.getProvisionToken(newDevice);
        Device deviceFromApi = appClient.parseDevice(2);
        assertNotNull(deviceFromApi);
        assertNotNull(deviceFromApi.token);

        for(int i=0; i< 3000;i++) {
            TestHardClient newHardClient = new TestHardClient(host, 8080);
            newHardClient.start();
            newHardClient.send("login " + deviceFromApi.token);

            verify(newHardClient.responseMock, timeout(500)).channelRead(any(), eq(ok(1)));
            newHardClient.send("internal " + b("ver 0.3.1 tmpl TMPL0001 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 build 111"));
            newHardClient.verifyResult(ok(2));
            newHardClient.send("ping");
            newHardClient.verifyResult(ok(3));
            Thread.sleep(100);
        }

        appClient.verifyResult(deviceConnected(2, deviceFromApi.id));
    }
}
