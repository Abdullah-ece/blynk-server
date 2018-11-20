package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.Widget;

import java.util.Arrays;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_WEB_SOURCES;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 02.12.15.
 */
public abstract class WebWidget extends Widget {

    public WebSource[] sources = EMPTY_WEB_SOURCES;

    @Override
    //deviceId is not actually used here. this api is left from mobile
    public boolean updateIfSame(int deviceId, short pin, PinType type, String value) {
        for (WebSource source : sources) {
            DataStream dataStream = source.dataStream;
            if (dataStream != null && dataStream.isSame(pin, type)) {
                dataStream.value = value;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getJsonValue() {
        return null;
    }

    @Override
    public void append(StringBuilder sb, int deviceId) {
        //append(sb, dataStream.pin, dataStream.pinType, getModeType());
    }

    @Override
    public boolean isSame(int deviceId, short pin, PinType type) {
        for (WebSource source : sources) {
            DataStream dataStream = source.dataStream;
            if (dataStream != null && dataStream.isSame(pin, type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateValue(Widget oldWidget) {
        if (oldWidget instanceof WebWidget) {
            WebWidget oldWebWidget = (WebWidget) oldWidget;
            for (WebSource oldSource : oldWebWidget.sources) {
                DataStream oldDataStream = oldSource.dataStream;
                if (oldDataStream != null) {
                    updateIfSame(0, oldDataStream.pin, oldDataStream.pinType, oldDataStream.value);
                }
            }
        }
    }

    @Override
    public boolean isAssignedToDevice(int deviceId) {
        return false;
    }

    @Override
    public void erase() {
    }

    //HAVE IN MIND : value is not compared as it is updated in realtime.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebWidget)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        WebWidget webWidget = (WebWidget) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(sources, webWidget.sources);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(sources);
        return result;
    }
}
