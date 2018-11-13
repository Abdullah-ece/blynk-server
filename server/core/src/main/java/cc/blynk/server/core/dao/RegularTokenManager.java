package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.09.15.
 */
class RegularTokenManager {

    private static final Logger log = LogManager.getLogger(RegularTokenManager.class);

    final ConcurrentHashMap<String, DeviceTokenValue> cache;

    RegularTokenManager(Collection<Organization> orgs) {
        ///in average user has 2 devices
        this.cache = new ConcurrentHashMap<>(orgs.size() == 0 ? 16 : orgs.size() * 2);
        for (Organization org : orgs) {
            for (Device device : org.devices) {
                if (device.token != null) {
                    cache.put(device.token, new DeviceTokenValue(org.id, device));
                }
            }
        }
    }

    String assignToken(int orgId, Device device, String newToken) {
        // Clean old token from cache if exists.
        String oldToken = deleteDeviceToken(device.token);

        //assign new token
        device.token = newToken;
        DeviceTokenValue tokenValue = new DeviceTokenValue(orgId, device);
        cache.put(newToken, tokenValue);

        log.debug("Generated token for deviceId {} is {}.", device.id, newToken);

        return oldToken;
    }

    String deleteDeviceToken(String deviceToken) {
        if (deviceToken != null) {
            cache.remove(deviceToken);
            return deviceToken;
        }
        return null;
    }

    DeviceTokenValue getTokenValue(String token) {
        return cache.get(token);
    }

}
