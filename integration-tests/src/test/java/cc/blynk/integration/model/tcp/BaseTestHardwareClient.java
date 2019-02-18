package cc.blynk.integration.model.tcp;

import cc.blynk.client.core.BaseClient;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.utils.StringUtils;
import org.mockito.Mockito;

import java.util.Random;

import static cc.blynk.server.core.protocol.enums.Command.HARDWARE;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOGIN;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_LOG_EVENT;
import static cc.blynk.server.core.protocol.enums.Command.HARDWARE_SYNC;
import static cc.blynk.server.core.protocol.enums.Command.SET_WIDGET_PROPERTY;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.02.18.
 */
public abstract class BaseTestHardwareClient extends BaseClient {

    public final SimpleClientHandler responseMock = Mockito.mock(SimpleClientHandler.class);
    private int msgId = 0;

    BaseTestHardwareClient(String host, int port, Random messageIdGenerator) {
        super(host, port, messageIdGenerator);
    }

    public void never(Object exceptedResult) throws Exception {
        verify(responseMock, Mockito.never()).channelRead(any(), eq(exceptedResult));
    }

    public void verifyResult(Object exceptedResult, int times) throws Exception {
        verify(responseMock, timeout(500).times(times)).channelRead(any(), eq(exceptedResult));
    }

    public void verifyResult(Object exceptedResult) throws Exception {
        verifyResult(exceptedResult, 1);
    }

    public String getBody() throws Exception {
        return TestUtil.getBody(responseMock, 1);
    }

    public String getBody(int expectedMessageOrder) throws Exception {
        return TestUtil.getBody(responseMock, expectedMessageOrder);
    }

    public void reset() {
        Mockito.reset(responseMock);
        msgId = 0;
    }

    public void send(String line) {
        send(produceMessageBaseOnUserInput(line, ++msgId));
    }

    public void send(short command, Object body) {
        send(command, ++msgId, body);
    }

    public void login(String token) {
        send(HARDWARE_LOGIN, token);
    }

    public void hardware(int pin, int value) {
        hardware(pin, String.valueOf(value));
    }

    public void hardware(int pin, String value) {
        send(HARDWARE, "vw" + BODY_SEPARATOR + pin + BODY_SEPARATOR + value);
    }

    public void setProperty(int pin, String property, String value) {
        send(SET_WIDGET_PROPERTY, "" + pin + " " + property + " " + value);
    }

    public void setProperty(int pin, String property, String... value) {
        send(SET_WIDGET_PROPERTY, "" + pin + " " + property + " " + String.join(StringUtils.BODY_SEPARATOR_STRING, value));
    }

    public void sync() {
        send(HARDWARE_SYNC);
    }

    public void sync(PinType pinType, int pin) {
        send(HARDWARE_SYNC, "" + pinType.pintTypeChar + "r" + BODY_SEPARATOR + pin);
    }

    public void sync(PinType pinType, int pin1, int pin2) {
        send(HARDWARE_SYNC, "" + pinType.pintTypeChar + "r" + BODY_SEPARATOR + pin1 + BODY_SEPARATOR + pin2);
    }

    public void logEvent(String eventName) {
        send(HARDWARE_LOG_EVENT, eventName);
    }

    public void logEvent(String eventName, String description) {
        send(HARDWARE_LOG_EVENT, eventName + BODY_SEPARATOR + description);
    }

}
