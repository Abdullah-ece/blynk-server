package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.Widget;

import java.util.Arrays;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 02.12.15.
 */
public abstract class WebWidget extends Widget {

    public WebSource[] sources;

    @Override
    //deviceId is not actually used here. this api is left from mobile
    public boolean updateIfSame(int deviceId, byte pin, PinType type, String value) {
        for (WebSource source : sources) {
            if (source.dataStream.isSame(pin, type)) {
                source.dataStream.value = value;
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
    public boolean isSame(int deviceId, byte pin, PinType type) {
        return false;
    }

    @Override
    public void updateValue(Widget oldWidget) {
        if (oldWidget instanceof WebWidget) {
            WebWidget oldWebWidget = (WebWidget) oldWidget;
            for (WebSource oldSource : oldWebWidget.sources) {
                DataStream oldDataStream = oldSource.dataStream;
                updateIfSame(0, oldDataStream.pin, oldDataStream.pinType, oldDataStream.value);
            }
        }
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
