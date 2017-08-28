package cc.blynk.server.core.model;

import cc.blynk.server.core.model.enums.PinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.07.15.
 */
public class DataStream {

    public static final int NO_PIN = -1;

    public final byte pin;

    public final boolean pwmMode;

    public final boolean rangeMappingOn;

    public final PinType pinType;

    public volatile String value;

    public final int min;

    public final int max;

    public final String label;

    @JsonCreator
    public DataStream(@JsonProperty("pin") byte pin,
                      @JsonProperty("pwmMode") boolean pwmMode,
                      @JsonProperty("rangeMappingOn") boolean rangeMappingOn,
                      @JsonProperty("pinType") PinType pinType,
                      @JsonProperty("value") String value,
                      @JsonProperty("min") int min,
                      @JsonProperty("max") int max,
                      @JsonProperty("label") String label) {
        this.pin = pin;
        this.pwmMode = pwmMode;
        this.rangeMappingOn = rangeMappingOn;
        this.pinType = pinType;
        this.value = value;
        this.min = min;
        this.max = max;
        this.label = label;
    }

    public DataStream(byte pin, PinType pinType) {
        this(pin, false, false, pinType, null, 0, 255, null);
    }

    public static String makeReadingHardwareBody(char pinType, byte pin) {
        return "" + pinType + 'r' + BODY_SEPARATOR + pin;
    }

    public static String makeHardwareBody(char pinType, String pin, String value) {
        return "" + pinType + 'w' + BODY_SEPARATOR + pin + BODY_SEPARATOR + value;
    }

    public static String makeHardwareBody(PinType pinType, byte pin, String value) {
        return makeHardwareBody(pinType.pintTypeChar, pin, value);
    }

    public static String makeHardwareBody(char pinTypeChar, byte pin, String value) {
        return "" + pinTypeChar + 'w' + BODY_SEPARATOR + pin + BODY_SEPARATOR + value;
    }

    public static String makeHardwareBody(boolean pwmMode, PinType pinType, byte pin, String value) {
        return pwmMode ? makeHardwareBody(PinType.ANALOG, pin, value) : makeHardwareBody(pinType, pin, value);
    }

    public boolean isSame(byte pin, PinType type) {
        return this.pin == pin && ((this.pwmMode && type == PinType.ANALOG) || (type == this.pinType));
    }

    public String makeHardwareBody() {
        return pwmMode ? makeHardwareBody(PinType.ANALOG, pin, value) : makeHardwareBody(pinType, pin, value);
    }

    public boolean isNotValid() {
        return pin == NO_PIN || pinType == null;
    }

    public boolean notEmpty() {
        return value != null && !isNotValid();
    }

    //HAVE IN MIND : value is not compared as it is updated in realtime.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pin)) return false;

        Pin pin1 = (Pin) o;

        if (pin != pin1.pin) return false;
        if (pwmMode != pin1.pwmMode) return false;
        if (rangeMappingOn != pin1.rangeMappingOn) return false;
        if (min != pin1.min) return false;
        if (max != pin1.max) return false;
        if (pinType != pin1.pinType) return false;
        return label != null ? label.equals(pin1.label) : pin1.label == null;
    }

    @Override
    public int hashCode() {
        int result = (int) pin;
        result = 31 * result + (pwmMode ? 1 : 0);
        result = 31 * result + (rangeMappingOn ? 1 : 0);
        result = 31 * result + (pinType != null ? pinType.hashCode() : 0);
        result = 31 * result + min;
        result = 31 * result + max;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }
}
