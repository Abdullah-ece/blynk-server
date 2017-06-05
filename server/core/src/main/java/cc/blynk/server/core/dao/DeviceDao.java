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

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class DeviceDao {

    private static final Logger log = LogManager.getLogger(DeviceDao.class);

    private final ConcurrentMap<DeviceKey, Device> devices;

    public DeviceDao(ConcurrentMap<UserKey, User> users) {
        devices = new ConcurrentHashMap<>();
        for (User user : users.values()) {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                for (Device device : dashBoard.devices) {
                    devices.put(new DeviceKey(user.email, user.orgId, dashBoard.id, device.id), device);
                }
            }

        }
        log.info("Devices number is {}", devices.size());
    }

    public void add(String email, int orgId, int dashId, Device device) {
        devices.put(new DeviceKey(email, orgId, dashId, device.id), device);
    }

    public Collection<Device> getAllByUser(User user) {
        if (user.isSuperAdmin()) {
            return getAll();
        }

        List<Device> result = new ArrayList<>();
        for (Map.Entry<DeviceKey, Device> entry : devices.entrySet()) {
            DeviceKey key = entry.getKey();
            if (user.orgId == key.orgId && key.email.equals(user.email)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public Collection<Device> getAll() {
        return devices.values();
    }

}
