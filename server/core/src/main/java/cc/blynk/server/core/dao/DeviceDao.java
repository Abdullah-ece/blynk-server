package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.exceptions.DeviceNotFoundException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.blynk.utils.StringUtils.truncateFileName;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class DeviceDao {

    private static final Logger log = LogManager.getLogger(DeviceDao.class);

    public final ConcurrentMap<DeviceKey, Device> devices;
    private final AtomicInteger deviceSequence;

    public DeviceDao(Collection<Organization> orgs) {
        devices = new ConcurrentHashMap<>();

        int maxDeviceId = 0;
        for (Organization org : orgs) {
            for (Device device : org.devices) {
                maxDeviceId = Math.max(maxDeviceId, device.id);
                devices.put(new DeviceKey(org.id, device.productId, device.id), device);
            }
        }

        this.deviceSequence = new AtomicInteger(maxDeviceId);
        log.info("Devices count is {}, sequence is {}", devices.size(), deviceSequence.get());
    }

    public int getId() {
        return deviceSequence.incrementAndGet();
    }

    public Device createWithPredefinedId(int orgId, Device device) {
        devices.put(new DeviceKey(orgId, device.productId, device.id), device);
        return device;
    }

    public Device create(int orgId, Device device) {
        device.id = deviceSequence.incrementAndGet();
        return createWithPredefinedId(orgId, device);
    }

    public Device delete(int deviceId) {
        return devices.remove(new DeviceKey(0, 0, deviceId));
    }

    public Device getById(int deviceId) {
        return devices.get(new DeviceKey(0, 0, deviceId));
    }

    public Device getByIdOrThrow(int deviceId) {
        Device device = devices.get(new DeviceKey(0, 0, deviceId));
        if (device == null) {
            log.error("Device with id {} not found.", deviceId);
            throw new DeviceNotFoundException("Requested device not exists.");
        }
        return device;
    }

    public Collection<Device> getAll() {
        return devices.values();
    }

    public boolean productHasDevices(int productId) {
        for (Map.Entry<DeviceKey, Device> entry : devices.entrySet()) {
            Device device = entry.getValue();
            if (device.productId == productId) {
                return true;
            }
        }
        return false;
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

    public List<Device> getByProductIdAndFilter(int productId, int[] deviceIds) {
        List<Device> result = new ArrayList<>();
        for (Map.Entry<DeviceKey, Device> entry : devices.entrySet()) {
            Device device = entry.getValue();
            if (device.productId == productId && ArrayUtil.contains(deviceIds, device.id)) {
                result.add(device);
            }
        }
        return result;
    }

    public String getDeviceName(int deviceId) {
        Device device = getById(deviceId);
        if (device != null) {
            return truncateFileName(device.name);
        }
        return "";
    }

    public String getCSVDeviceName(int deviceId) {
        Device device = getById(deviceId);
        if (device == null) {
            return String.valueOf(deviceId);
        }

        String deviceName = device.name;
        if (deviceName == null || deviceName.isEmpty()) {
            return String.valueOf(deviceId);
        }

        return StringUtils.escapeCSV(deviceName);
    }

    public List<Device> getDevicesOwnedByUser(String ownerEmail) {
        List<Device> result = new ArrayList<>();
        for (Device device : devices.values()) {
            if (device.hasOwner(ownerEmail)) {
                result.add(device);
            }
        }
        return result;
    }

}
