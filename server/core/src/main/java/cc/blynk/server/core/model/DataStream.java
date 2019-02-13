package cc.blynk.server.core.model;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.web.product.metafields.MeasurementUnit;
import cc.blynk.server.core.model.widgets.outputs.graph.AggregationFunctionType;
import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static cc.blynk.utils.StringUtils.BODY_SEPARATOR;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 03.07.15.
 */
public class DataStream implements CopyObject<DataStream> {

    public static final int NO_PIN = -1;
    public static final DataStream[] EMPTY_DATA_STREAMS = {};

    public final int id;

    public final short pin;

    public final boolean pwmMode;

    public final boolean rangeMappingOn;

    public final PinType pinType;

    public volatile String value;

    public final float min;

    public final float max;

    public final String label;

    public final MeasurementUnit units;

    public final AggregationFunctionType aggregationFunctionType;

    @JsonCreator
    public DataStream(@JsonProperty("id") int id,
                      @JsonProperty("pin") short pin,
                      @JsonProperty("pwmMode") boolean pwmMode,
                      @JsonProperty("rangeMappingOn") boolean rangeMappingOn,
                      @JsonProperty("pinType") PinType pinType,
                      @JsonProperty("value") String value,
                      @JsonProperty("min") float min,
                      @JsonProperty("max") float max,
                      @JsonProperty("label") String label,
                      @JsonProperty("units") MeasurementUnit units,
                      @JsonProperty("aggregationFunctionType") AggregationFunctionType aggregationFunctionType) {
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
        //todo temp solution, remove later
        this.aggregationFunctionType =
                aggregationFunctionType == null ? AggregationFunctionType.AVG : aggregationFunctionType;
    }

    public DataStream(DataStream dataStream) {
        this(dataStream.id, dataStream.pin, dataStream.pwmMode, dataStream.rangeMappingOn,
                dataStream.pinType, dataStream.value,
                dataStream.min, dataStream.max, dataStream.label,
                dataStream.units, dataStream.aggregationFunctionType);
    }

    public DataStream(short pin, PinType pinType) {
        this(0, pin, false, false, pinType, null, 0, 255, null, null, null);
    }

    public DataStream(short pin, PinType pinType, AggregationFunctionType aggregationFunctionType) {
        this(0, pin, false, false, pinType, null, 0, 255, null, null, aggregationFunctionType);
    }

    public static String makeReadingHardwareBody(char pinType, short pin) {
        return "" + pinType + 'r' + BODY_SEPARATOR + pin;
    }

    public static String makeHardwareBody(char pinType, String pin, String value) {
        return "" + pinType + 'w' + BODY_SEPARATOR + pin + BODY_SEPARATOR + value;
    }

    public static String makeHardwareBody(PinType pinType, short pin, String value) {
        return makeHardwareBody(pinType.pintTypeChar, pin, value);
    }

    public static String makeHardwareBody(char pinTypeChar, short pin, String value) {
        return "" + pinTypeChar + 'w' + BODY_SEPARATOR + pin + BODY_SEPARATOR + value;
    }

    public static String makeHardwareBody(boolean pwmMode, PinType pinType, short pin, String value) {
        return pwmMode ? makeHardwareBody(PinType.ANALOG, pin, value) : makeHardwareBody(pinType, pin, value);
    }

    public static String makePropertyHardwareBody(short pin, WidgetProperty property, String value) {
        return "" + pin + BODY_SEPARATOR + property.label + BODY_SEPARATOR + value;
    }

    public static boolean isValid(short pin, PinType pinType) {
        return pin != NO_PIN && pinType != null;
    }

    public String makeHardwareBody() {
        return pwmMode ? makeHardwareBody(PinType.ANALOG, pin, value) : makeHardwareBody(pinType, pin, value);
    }

    public boolean isSame(short pin, PinType type) {
        return this.pin == pin && (type == this.pinType || (this.pwmMode && type == PinType.ANALOG));
    }

    public static DataStream getDataStream(DataStream[] dataStreams, short pin, PinType pinType) {
        for (DataStream dataStream : dataStreams) {
            if (dataStream.isSame(pin, pinType)) {
                return dataStream;
            }
        }
        return null;
    }

    public boolean isValid() {
        return isValid(pin, pinType);
    }

    public boolean isValidForGroups() {
         return isValid() && this.aggregationFunctionType != null;
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

        if (id != that.id) {
            return false;
        }
        if (pin != that.pin) {
            return false;
        }
        if (pwmMode != that.pwmMode) {
            return false;
        }
        if (rangeMappingOn != that.rangeMappingOn) {
            return false;
        }
        if (Float.compare(that.min, min) != 0) {
            return false;
        }
        if (Float.compare(that.max, max) != 0) {
            return false;
        }
        if (pinType != that.pinType) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        if (label != null ? !label.equals(that.label) : that.label != null) {
            return false;
        }
        return units == that.units;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) pin;
        result = 31 * result + (pwmMode ? 1 : 0);
        result = 31 * result + (rangeMappingOn ? 1 : 0);
        result = 31 * result + (pinType != null ? pinType.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (units != null ? units.hashCode() : 0);
        return result;
    }
}
