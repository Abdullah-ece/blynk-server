package cc.blynk.server.core.model;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.db.dao.descriptor.TableDescriptor;
import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static cc.blynk.server.db.dao.descriptor.TableDescriptor.getTableByPin;
import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.07.15.
 */
public class DataStream implements CopyObject<DataStream> {

    public static final int NO_PIN = -1;

    public final int id;

    public final byte pin;

    public final boolean pwmMode;

    public final boolean rangeMappingOn;

    public final PinType pinType;

    public volatile String value;

    public final float min;

    public final float max;

    public final String label;

    public final MeasurementUnit units;

    public final TableDescriptor tableDescriptor;

    @JsonCreator
    public DataStream(@JsonProperty("id") int id,
                      @JsonProperty("pin") byte pin,
                      @JsonProperty("pwmMode") boolean pwmMode,
                      @JsonProperty("rangeMappingOn") boolean rangeMappingOn,
                      @JsonProperty("pinType") PinType pinType,
                      @JsonProperty("value") String value,
                      @JsonProperty("min") float min,
                      @JsonProperty("max") float max,
                      @JsonProperty("label") String label,
                      @JsonProperty("units") MeasurementUnit units) {
        this.id = id;
        this.pin = pin;
        this.pwmMode = pwmMode;
        this.rangeMappingOn = rangeMappingOn;
        this.pinType = pinType;
        this.value = value;
        this.min = min;
        this.max = max;
        this.label = label;
        this.units = units;
        this.tableDescriptor = getTableByPin(pin, pinType);
    }

    public DataStream(DataStream dataStream) {
        this(dataStream.id, dataStream.pin, dataStream.pwmMode, dataStream.rangeMappingOn,
                dataStream.pinType, dataStream.value,
                dataStream.min, dataStream.max, dataStream.label, dataStream.units);
    }

    public DataStream(byte pin, PinType pinType) {
        this(0, pin, false, false, pinType, null, 0, 255, null, null);
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
        return this.pin == pin && (type == this.pinType || (this.pwmMode && type == PinType.ANALOG));
    }

    public String makeHardwareBody() {
        return pwmMode ? makeHardwareBody(PinType.ANALOG, pin, value) : makeHardwareBody(pinType, pin, value);
    }

    public static boolean isValid(byte pin, PinType pinType) {
        return pin != NO_PIN && pinType != null;
    }

    public boolean isValid() {
        return isValid(pin, pinType);
    }

    public boolean isNotEmpty() {
        return value != null;
    }

    public boolean notEmptyAndIsValid() {
        return isNotEmpty() && isValid();
    }

    @Override
    public DataStream copy() {
        return new DataStream(this);
    }

    //HAVE IN MIND : value is not compared as it is updated in realtime.

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStream that = (DataStream) o;
        return id == that.id
                && pin == that.pin
                && pwmMode == that.pwmMode
                && rangeMappingOn == that.rangeMappingOn
                && Float.compare(that.min, min) == 0
                && Float.compare(that.max, max) == 0
                && pinType == that.pinType
                && Objects.equals(value, that.value)
                && Objects.equals(label, that.label)
                && units == that.units;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pin, pwmMode, rangeMappingOn, pinType, value, min, max, label, units);
    }
}
