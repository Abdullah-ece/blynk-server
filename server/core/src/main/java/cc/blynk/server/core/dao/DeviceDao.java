package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class DeviceDao {

    private static final Logger log = LogManager.getLogger(DeviceDao.class);

    public final ConcurrentMap<DeviceKey, Device> devices;

    public DeviceDao(ConcurrentMap<UserKey, User> users) {
        devices = new ConcurrentHashMap<>();
        for (User user : users.values()) {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                for (Device device : dashBoard.devices) {
                    devices.put(new DeviceKey(user.email, dashBoard.id, device.id), device);
                }
            }

        }
        log.info("Devices number is {}", devices.size());
    }

    public void add(String email, int dashId, Device device) {
        devices.put(new DeviceKey(email, dashId, device.id), device);
    }

}
