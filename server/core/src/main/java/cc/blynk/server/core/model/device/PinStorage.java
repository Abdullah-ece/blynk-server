package cc.blynk.server.core.model.device;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.storage.DeviceStorageKeyDeserializer;
import cc.blynk.server.core.model.storage.PinStorageValueDeserializer;
import cc.blynk.server.core.model.storage.key.DevicePropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.server.core.model.storage.value.PinStorageValue;
import cc.blynk.server.core.model.storage.value.SinglePinStorageValue;
import cc.blynk.server.core.model.widgets.MultiPinWidget;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public final class PinStorage {

    @JsonDeserialize(keyUsing = DeviceStorageKeyDeserializer.class,
            contentUsing = PinStorageValueDeserializer.class)
    public final Map<DeviceStorageKey, PinStorageValue> values = new HashMap<>();

    public volatile long dataReceivedAt;

    void removePinValue(PinType pinType, short pin, boolean removeProperties) {
        this.values.remove(new DeviceStorageKey(pin, pinType));
        if (removeProperties) {
            for (WidgetProperty widgetProperty : WidgetProperty.values()) {
                values.remove(new DevicePropertyStorageKey(pinType, pin, widgetProperty));
            }
        }
    }

    //property users always virtual pins
    public void updateValue(int deviceId, DashBoard dash, short pin, WidgetProperty widgetProperty, String value) {
        updateValue(deviceId, dash, new DevicePropertyStorageKey(PinType.VIRTUAL, pin, widgetProperty), value);
    }

    public void updateValue(int deviceId, DashBoard dash, short pin, PinType pinType, String value) {
        updateValue(deviceId, dash, new DeviceStorageKey(pin, pinType), value);
    }

    public void updateValue(int deviceId, DashBoard dash, DeviceStorageKey key, String value) {
        updateValue(deviceId, dash, key, value, System.currentTimeMillis());
    }

    public void updateValue(int deviceId, DashBoard dash, DataStream dataStream, String value, long now) {
        updateValue(deviceId, dash, new DeviceStorageKey(dataStream.pin, dataStream.pinType), value, now);
    }

    public void updateValue(int deviceId, DashBoard dash, short pin, PinType pinType,  String value, long now) {
        updateValue(deviceId, dash, new DeviceStorageKey(pin, pinType), value, now);
    }

    public void updateValue(int deviceId, DashBoard dash, DeviceStorageKey key, String value, long now) {
        PinStorageValue pinStorageValue = values.get(key);
        if (pinStorageValue == null) {
            pinStorageValue = initStorageValueForStorageKey(deviceId, dash, key);
            values.put(key, pinStorageValue);
        }
        pinStorageValue.update(value);
        this.dataReceivedAt = now;
    }

    void sendPinStorageSyncs(Channel appChannel, int deviceId) {
        for (Map.Entry<DeviceStorageKey, PinStorageValue> entry : values.entrySet()) {
            DeviceStorageKey key = entry.getKey();
            if (appChannel.isWritable()) {
                PinStorageValue pinStorageValue = entry.getValue();
                pinStorageValue.sendAppSync(appChannel, deviceId, key);
            }
        }
    }

    //multi value widgets has always priority over single value widgets.
    //for example, we have 2 widgets on the same pin, one it terminal, another is value display.
    //so for that pin we have to return multivalue storage
    private PinStorageValue initStorageValueForStorageKey(int deviceId, DashBoard dash, DeviceStorageKey key) {
        if (!(key instanceof DevicePropertyStorageKey)) {
            for (Widget widget : dash.widgets) {
                if (widget instanceof OnePinWidget) {
                    OnePinWidget onePinWidget = (OnePinWidget) widget;
                    //pim matches and widget assigned to device selector
                    if (onePinWidget.isAssignedToDeviceSelector() && key.isSame(onePinWidget)) {
                        DeviceSelector deviceSelector = dash.getDeviceSelector(onePinWidget.deviceId);
                        if (deviceSelector != null && ArrayUtil.contains(deviceSelector.deviceIds, deviceId)) {
                            if (widget.isMultiValueWidget()) {
                                return widget.getPinStorageValue();
                            }
                        }
                    }
                } else if (widget instanceof MultiPinWidget) {
                    MultiPinWidget multiPinWidget = (MultiPinWidget) widget;
                    if (multiPinWidget.isAssignedToDeviceSelector() && key.isSame(multiPinWidget)) {
                        DeviceSelector deviceSelector = dash.getDeviceSelector(multiPinWidget.deviceId);
                        if (deviceSelector != null && ArrayUtil.contains(deviceSelector.deviceIds, deviceId)) {
                            if (widget.isMultiValueWidget()) {
                                return widget.getPinStorageValue();
                            }
                        }
                    }
                } else if (widget instanceof DeviceTiles) {
                    DeviceTiles deviceTiles = (DeviceTiles) widget;
                    for (TileTemplate template : deviceTiles.templates) {
                        if (ArrayUtil.contains(template.deviceIds, deviceId)) {
                            for (Widget tileWidget : template.widgets) {
                                if (tileWidget instanceof OnePinWidget) {
                                    if (key.isSame((OnePinWidget) tileWidget)) {
                                        if (tileWidget.isMultiValueWidget()) {
                                            return tileWidget.getPinStorageValue();
                                        }
                                    }
                                } else if (tileWidget instanceof MultiPinWidget) {
                                    if (key.isSame((MultiPinWidget) tileWidget)) {
                                        if (tileWidget.isMultiValueWidget()) {
                                            return tileWidget.getPinStorageValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return new SinglePinStorageValue();
    }

    public PinStorageValue get(short pin, PinType pinType) {
        return values.get(new DeviceStorageKey(pin, pinType));
    }

    public void setDataReceivedAt(long now) {
        this.dataReceivedAt = now;
    }

    public void erase() {
        this.values.clear();
        this.dataReceivedAt = 0;
    }
}
