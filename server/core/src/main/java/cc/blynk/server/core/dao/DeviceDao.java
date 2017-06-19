package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class DeviceDao {

    private static final Logger log = LogManager.getLogger(DeviceDao.class);

    private final ConcurrentMap<DeviceKey, Device> devices;
    private final AtomicInteger deviceSequence;

    public DeviceDao(ConcurrentMap<UserKey, User> users) {
        devices = new ConcurrentHashMap<>();

        int maxDeviceId = 0;
        for (User user : users.values()) {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                for (Device device : dashBoard.devices) {
                    maxDeviceId = Math.max(maxDeviceId, device.id);
                    devices.put(new DeviceKey(user.orgId, device.id), device);
                }
            }

        }

        this.deviceSequence = new AtomicInteger(maxDeviceId);
        log.info("Devices number is {}", devices.size());
    }

    public Device create(int orgId, Device device) {
        device.id = deviceSequence.incrementAndGet();
        devices.put(new DeviceKey(orgId, device.id), device);
        return device;
    }

    public Device delete(int orgId, int deviceId) {
        return devices.remove(new DeviceKey(orgId, deviceId));
    }

    public Device getById(int deviceId) {
        return devices.get(new DeviceKey(0, deviceId));
    }

    public Collection<Device> getAllByUser(User user) {
        List<Device> result = new ArrayList<>();
        for (Map.Entry<DeviceKey, Device> entry : devices.entrySet()) {
            DeviceKey key = entry.getKey();
            if (user.orgId == key.orgId) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public Collection<Device> getAll() {
        return devices.values();
    }

    public List<Device> getAllByProductId(int productId) {
        List<Device> result = new ArrayList<>();
        for (Map.Entry<DeviceKey, Device> entry : devices.entrySet()) {
            Device device = entry.getValue();
            if (device.productId == productId) {
                result.add(device);
            }
        }
        return result;
    }

}
