package cc.blynk.server.core.model.storage;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.WidgetProperty;
import cc.blynk.server.core.model.storage.key.DevicePropertyStorageKey;
import cc.blynk.server.core.model.storage.key.DeviceStorageKey;
import cc.blynk.utils.NumberUtil;
import cc.blynk.utils.StringUtils;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.11.18.
 */
public class DeviceStorageKeyDeserializer extends KeyDeserializer {

    @Override
    public DeviceStorageKey deserializeKey(String key, DeserializationContext ctx) {
        //parsing "v24"
        //or
        //parsing "v24-property"
        String[] split = StringUtils.split3(StringUtils.DEVICE_SEPARATOR, key);

        PinType pinType = PinType.getPinType(split[0].charAt(0));
        short pin = NumberUtil.parsePin(split[0].substring(1));

        if (split.length == 2) {
            WidgetProperty widgetProperty = WidgetProperty.getProperty(split[1]);
            if (widgetProperty == null) {
                widgetProperty = WidgetProperty.LABEL;
            }
            return new DevicePropertyStorageKey(pinType, pin, widgetProperty);
        } else {
            return new DeviceStorageKey(pin, pinType);
        }
    }
}
