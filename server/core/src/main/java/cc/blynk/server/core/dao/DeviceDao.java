package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.HardwareInfo;
import cc.blynk.server.core.model.exceptions.DeviceNotFoundException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.blynk.server.core.model.device.HardwareInfo.DEFAULT_HARDWARE_BUFFER_SIZE;
import static cc.blynk.utils.StringUtils.truncateFileName;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public final class DeviceDao {

    private static final Logger log = LogManager.getLogger(DeviceDao.class);

    public final ConcurrentMap<Integer, DeviceValue> devices;
    private final AtomicInteger deviceSequence;
    private final DeviceTokenManager deviceTokenManager;

    public DeviceDao(Collection<Organization> orgs, DeviceTokenManager deviceTokenManager) {
        devices = new ConcurrentHashMap<>();

        int maxDeviceId = 0;
        for (Organization org : orgs) {
            for (Product product : org.products) {
                for (Device device : product.devices) {
                    maxDeviceId = Math.max(maxDeviceId, device.id);
                    hotfixForAiriusMissingHardwareInfo(product, device);
                    devices.put(device.id, new DeviceValue(org.id, product, device));
                }
            }
        }

        this.deviceSequence = new AtomicInteger(maxDeviceId);
        this.deviceTokenManager = deviceTokenManager;
        log.info("Devices count is {}, sequence is {}", devices.size(), deviceSequence.get());
    }

    private static void hotfixForAiriusMissingHardwareInfo(Product product, Device device) {
        String tmplId = product.getFirstTemplateId();
        if (device.hardwareInfo == null) {
            log.warn("Missing hardwareInfo for deviceId {}, fix {}.", device.id, tmplId);
            HardwareInfo hardwareInfo = new HardwareInfo(
                    "0.7.0", "0.5.4", BoardType.TI_CC3220.label,
                    "cpuType", ConnectionType.WI_FI.name(),
                    "Dec  7 2018 20:20:31", tmplId, DEFAULT_HARDWARE_BUFFER_SIZE, 1024
            );
            device.setHardwareInfo(hardwareInfo);
        } else if (device.hardwareInfo.templateId == null) {
            log.warn("Missing hardwareInfo.templateId for deviceId {}, fix {}.", device.id, tmplId);
            device.hardwareInfo.templateId = tmplId;
        }
    }

    public int getId() {
        return deviceSequence.incrementAndGet();
    }

    public void createWithPredefinedIdAndToken(int orgId, String email, Product product, Device device) {
        devices.put(device.id, new DeviceValue(orgId, product, device));
        deviceTokenManager.assignNewToken(orgId, email, product, device, device.token);
    }

    public Device createWithPredefinedId(int orgId, String email, Product product, Device device) {
        devices.put(device.id, new DeviceValue(orgId, product, device));
        deviceTokenManager.assignNewToken(orgId, email, product, device);
        return device;
    }

    public Device create(int orgId, String email, Product product, Device device) {
        device.id = deviceSequence.incrementAndGet();
        return createWithPredefinedId(orgId, email, product, device);
    }

    public DeviceValue delete(int deviceId) {
        DeviceValue deviceValue = devices.remove(deviceId);
        //also removes deivce from the product
        deviceTokenManager.deleteDevice(deviceValue);
        return deviceValue;
    }

    public Device getById(int deviceId) {
        DeviceValue deviceValue = devices.get(deviceId);
        if (deviceValue == null) {
            return null;
        }
        return deviceValue.device;
    }

    public DeviceValue getDeviceValueById(int deviceId) {
        return devices.get(deviceId);
    }

    public Device getByIdOrThrow(int deviceId) {
        DeviceValue deviceValue = devices.get(deviceId);
        if (deviceValue == null) {
            log.error("Device with id {} not found.", deviceId);
            throw new DeviceNotFoundException();
        }
        return deviceValue.device;
    }

    public Collection<DeviceValue> getAll() {
        return devices.values();
    }

    public boolean productHasDevices(int productId) {
        for (var deviceEntry : devices.entrySet()) {
            DeviceValue deviceValue = deviceEntry.getValue();
            Device device = deviceValue.device;
            if (device.productId == productId) {
                return true;
            }
        }
        return false;
    }

    public List<Device> getAllByProductId(int productId) {
        List<Device> result = new ArrayList<>();
        for (var deviceEntry : devices.entrySet()) {
            DeviceValue deviceValue = deviceEntry.getValue();
            Device device = deviceValue.device;
            if (device.productId == productId) {
                result.add(device);
            }
        }
        return result;
    }

    public List<Device> getByProductIdAndFilter(int orgId, int productId, int[] deviceIds) {
        List<Device> result = new ArrayList<>();
        for (int deviceId : deviceIds) {
            DeviceValue deviceValue = devices.get(deviceId);
            if (deviceValue != null && deviceValue.orgId == orgId && deviceValue.product.id == productId) {
                Device device = deviceValue.device;
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
        for (DeviceValue deviceValue : devices.values()) {
            Device device = deviceValue.device;
            if (device.hasOwner(ownerEmail)) {
                result.add(device);
            }
        }
        return result;
    }

    public DeviceValue getDeviceTokenValue(String token) {
        return deviceTokenManager.getTokenValueByToken(token);
    }

    public void deleteAllTokensForOrg(int orgId) {
        deviceTokenManager.cache.entrySet().removeIf(entry -> entry.getValue().belongsToOrg(orgId));
    }

    public boolean clearTemporaryTokens() {
        long now = System.currentTimeMillis();
        return deviceTokenManager.cache.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    public String assignNewToken(int orgId, String email, Product product, Device device) {
        return deviceTokenManager.assignNewToken(orgId, email, product, device);
    }

    public void assignNewToken(int orgId, String email, Product product, Device device, String newToken) {
        deviceTokenManager.assignNewToken(orgId, email, product, device, newToken);
    }

    public void assignTempToken(int orgId, User user, Device tempDevice) {
        deviceTokenManager.assignTempToken(new ProvisionTokenValue(orgId, user, tempDevice));
    }
}
