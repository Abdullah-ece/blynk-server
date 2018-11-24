package cc.blynk.server.core.model;

import cc.blynk.server.core.model.enums.PinType;

public interface UpdateInterface {

    boolean updateWidgetsValue(int deviceId, short pin, PinType type, String value);

}
