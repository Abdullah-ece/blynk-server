package cc.blynk.integration.tcp;

import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.model.tcp.TestAppClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static cc.blynk.integration.TestUtil.b;
import static cc.blynk.integration.TestUtil.hardware;
import static cc.blynk.integration.TestUtil.internal;
import static cc.blynk.integration.TestUtil.ok;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class BlynkInternalTest extends SingleServerInstancePerTest {

    @Test
    public void appConnectedEvent() throws Exception {
        clientPair.appClient.updateDash("{\"id\":1, \"name\":\"test board\", \"isAppConnectedOn\":true}");
        clientPair.appClient.verifyResult(ok(1));

        TestAppClient appClient = new TestAppClient(properties);
        appClient.start();

        appClient.login(getUserName(), "1", "Android", "1.13.3");
        appClient.verifyResult(ok(1));

        clientPair.hardwareClient.verifyResult(internal(7777, "acon"));
    }

    @Test
    public void appDisconnectedEvent() throws Exception {
        clientPair.appClient.updateDash("{\"id\":1, \"name\":\"test board\", \"isAppConnectedOn\":true}");
        clientPair.appClient.verifyResult(ok(1));

        clientPair.appClient.stop().await();

        clientPair.hardwareClient.verifyResult(internal(7777, "adis"));
    }

    @Test
    public void testBuffInIsHandled() throws Exception {
        clientPair.hardwareClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 12 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"));
        clientPair.hardwareClient.verifyResult(ok(1));

        clientPair.appClient.send("hardware 1-0 vw 1 12");
        clientPair.hardwareClient.verifyResult(hardware(1, "vw 1 12"));

        clientPair.appClient.send("hardware 1-0 vw 1 123");
        clientPair.hardwareClient.never(hardware(2, "vw 1 123"));

        clientPair.hardwareClient.send("internal " + b("ver 0.3.1 h-beat 10 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"));
        clientPair.hardwareClient.verifyResult(ok(2));

        clientPair.appClient.send("hardware 1-0 vw 1 12");
        clientPair.hardwareClient.verifyResult(hardware(3, "vw 1 12"));

        clientPair.hardwareClient.send("internal " + b("ver 0.3.1 h-beat 10 buff-in 0 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"));
        clientPair.hardwareClient.verifyResult(ok(3));

        clientPair.appClient.send("hardware 1-0 vw 1 12");
        clientPair.hardwareClient.verifyResult(hardware(4, "vw 1 12"));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 245; i++) {
            sb.append("a");
        }

        String s = sb.toString();

        clientPair.appClient.send("hardware 1-0 vw 1 " + s);
        clientPair.hardwareClient.never(hardware(5, "vw 1 " + s));
    }

}
