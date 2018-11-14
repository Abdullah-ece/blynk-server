package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.db.DBManager;
import cc.blynk.utils.TokenGeneratorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 14.10.16.
 */
public class DeviceTokenManager {

    private static final Logger log = LogManager.getLogger(DeviceTokenManager.class);

    protected final ConcurrentHashMap<String, DeviceTokenValue> cache;
    private final DBManager dbManager;
    private final String host;

    public DeviceTokenManager(Collection<Organization> orgs,
                              DBManager dbManager, String host) {
        ///in average user has 2 devices
        this.cache = new ConcurrentHashMap<>(orgs.size() == 0 ? 16 : orgs.size() * 2);
        for (Organization org : orgs) {
            for (Device device : org.devices) {
                if (device.token != null) {
                    cache.put(device.token, new DeviceTokenValue(org.id, device));
                }
            }
        }
        this.dbManager = dbManager;
        this.host = host;
    }

    public void deleteDevice(Device device) {
        if (device != null) {
            String token = device.token;
            if (token != null) {
                deleteDeviceToken(token);
                dbManager.removeToken(token);
            }
        }
    }

    private String deleteDeviceToken(String deviceToken) {
        if (deviceToken != null) {
            cache.remove(deviceToken);
            return deviceToken;
        }
        return null;
    }

    DeviceTokenValue getTokenValueByToken(String token) {
        return cache.get(token);
    }

    void assignTempToken(ProvisionTokenValue provisionTokenValue) {
        String newToken = TokenGeneratorUtil.generateNewToken();
        provisionTokenValue.device.token = newToken;
        cache.put(newToken, provisionTokenValue);
        log.debug("Generated provision token for orgId {} user {}, deviceId {} is {}.",
                provisionTokenValue.orgId, provisionTokenValue.user.email, provisionTokenValue.device.id, newToken);
    }

    String assignNewToken(int orgId, String email, Device device) {
        String newToken = TokenGeneratorUtil.generateNewToken();
        assignNewToken(orgId, email, device, newToken);
        return newToken;
    }

    void assignNewToken(int orgId, String email, Device device, String newToken) {
        String oldToken = assignToken(orgId, device, newToken);
        //device activated when new token is assigned
        device.activatedAt = System.currentTimeMillis();
        device.activatedBy = email;
        device.updatedAt = device.activatedAt;

        dbManager.assignServerToToken(newToken, host, email, device.id);
        if (oldToken != null) {
            dbManager.removeToken(oldToken);
        }
    }

    private String assignToken(int orgId, Device device, String newToken) {
        // Clean old token from cache if exists.
        String oldToken = deleteDeviceToken(device.token);

        //assign new token
        device.token = newToken;
        DeviceTokenValue tokenValue = new DeviceTokenValue(orgId, device);
        cache.put(newToken, tokenValue);

        log.debug("Generated token for orgId {} deviceId {} is {}.", orgId, device.id, newToken);

        return oldToken;
    }

}
