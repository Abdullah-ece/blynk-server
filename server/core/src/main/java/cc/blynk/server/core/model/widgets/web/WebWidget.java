package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.utils.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 02.12.15.
 */
public abstract class WebWidget extends Widget {

    public DataStream dataStream;

    @Override
    public boolean updateIfSame(int deviceId, byte pin, PinType type, String value) {
        if (dataStream.isSame(pin, type)) {
            dataStream.value = value;
            return true;
        }
        return false;
    }

    @Override
    public void updateIfSame(Widget widget) {
    }

    @Override
    public String getValue(byte pin, PinType type) {
        return dataStream.value;
    }

    @Override
    public String getJsonValue() {
        if (dataStream.value == null) {
            return "[]";
        }
        return JsonParser.valueToJsonAsString(dataStream.value);
    }

    @Override
    public void append(StringBuilder sb, int deviceId) {
        append(sb, dataStream.pin, dataStream.pinType, getModeType());
    }

    @Override
    public boolean isSame(int deviceId, byte pin, PinType type) {
        return dataStream.isSame(pin, type);
    }

    //HAVE IN MIND : value is not compared as it is updated in realtime.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebWidget)) return false;
        if (!super.equals(o)) return false;

        WebWidget webWidget = (WebWidget) o;

        return dataStream != null ? dataStream.equals(webWidget.dataStream) : webWidget.dataStream == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataStream != null ? dataStream.hashCode() : 0);
        return result;
    }
}
