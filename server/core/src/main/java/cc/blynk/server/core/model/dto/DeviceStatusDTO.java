package cc.blynk.server.core.model.dto;

import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.device.Status;

import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.09.18.
 */
public class DeviceStatusDTO {

    public final int id;

    public final int productId;

    public final String name;

    public final BoardType boardType;

    public final String token;

    public final ConnectionType connectionType;

    public final Status status;

    public final long disconnectTime;

    public final long connectTime;

    public final HardwareInfo hardwareInfo;

    public final String iconName;

    public final boolean isUserIcon;

    public DeviceStatusDTO(Device device) {
        this.id = device.id;
        this.productId = device.productId;
        this.name = device.name;
        this.boardType = device.boardType;
        this.token = device.token;
        this.connectionType = device.connectionType;
        this.status = device.status;
        this.disconnectTime = device.disconnectTime;
        this.connectTime = device.connectTime;
        this.hardwareInfo = device.hardwareInfo;
        this.iconName = device.iconName;
        this.isUserIcon = device.isUserIcon;
    }

    public static DeviceStatusDTO[] transform(List<Device> devices) {
        DeviceStatusDTO[] deviceStatusDTO = new DeviceStatusDTO[devices.size()];
        for (int i = 0; i < devices.size(); i++) {
            deviceStatusDTO[i] = new DeviceStatusDTO(devices.get(i));
        }
        return deviceStatusDTO;
    }

    public static DeviceStatusDTO[] transform(Device[] devices) {
        DeviceStatusDTO[] deviceStatusDTO = new DeviceStatusDTO[devices.length];
        for (int i = 0; i < devices.length; i++) {
            deviceStatusDTO[i] = new DeviceStatusDTO(devices[i]);
        }
        return deviceStatusDTO;
    }
}
