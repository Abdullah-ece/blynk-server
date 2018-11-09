package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
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

    final ConcurrentHashMap<String, TokenValue> cache;

    RegularTokenManager(Collection<Organization> orgs) {
        ///in average user has 2 devices
        this.cache = new ConcurrentHashMap<>(orgs.size() == 0 ? 16 : orgs.size() * 2);
        for (Organization org : orgs) {
            for (Device device : org.devices) {
                if (device.token != null) {
                    cache.put(device.token, new TokenValue(org.id, null, null, device));
                }
            }
        }
    }

    String assignToken(int orgId, User user, DashBoard dash, Device device, String newToken) {
        // Clean old token from cache if exists.
        String oldToken = deleteDeviceToken(device.token);

        //assign new token
        device.token = newToken;
        TokenValue tokenValue = new TokenValue(orgId, user, dash, device);
        cache.put(newToken, tokenValue);

        //device activated when new token is assigned
        device.activatedAt = System.currentTimeMillis();
        device.activatedBy = user.email;

        user.lastModifiedTs = System.currentTimeMillis();

        log.debug("Generated token for user {}, dashId {}, deviceId {} is {}.",
                user.email, dash.id, device.id, newToken);

        return oldToken;
    }

    String deleteDeviceToken(String deviceToken) {
        if (deviceToken != null) {
            cache.remove(deviceToken);
            return deviceToken;
        }
        return null;
    }

    TokenValue getTokenValue(String token) {
        return cache.get(token);
    }

}
