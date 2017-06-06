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
                    maxDeviceId = Math.max(maxDeviceId, device.globalId);
                    devices.put(new DeviceKey(user.orgId, device.globalId), device);
                }
            }

        }

        this.deviceSequence = new AtomicInteger(maxDeviceId);
        log.info("Devices number is {}", devices.size());
    }

    public void add(int orgId, Device device) {
        device.globalId = deviceSequence.incrementAndGet();
        device.id = device.globalId;
        devices.put(new DeviceKey(orgId, device.globalId), device);
    }

    public Device delete(int orgId, int globalId) {
        return devices.remove(new DeviceKey(orgId, globalId));
    }

    public Device getById(int globalId) {
        for (Device device : devices.values()) {
            if (device.globalId == globalId) {
                return device;
            }
        }
        return null;
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

}
