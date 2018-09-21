package cc.blynk.server.core.model.dto;

import cc.blynk.server.core.model.device.Device;

public class IdNameDTO {

    public final int id;
    public final String name;

    public IdNameDTO(Device device) {
        this(device.id, device.name);
    }

    public IdNameDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
