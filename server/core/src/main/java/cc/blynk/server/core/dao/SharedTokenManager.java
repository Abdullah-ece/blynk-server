package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.utils.TokenGeneratorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 22.09.15.
 */
public class SharedTokenManager {

    private static final Logger log = LogManager.getLogger(SharedTokenManager.class);

    public static final String ALL = "*";

    private final ConcurrentHashMap<String, SharedTokenValue> cache;

    public SharedTokenManager(Collection<User> users) {
        this.cache = new ConcurrentHashMap<>();
        for (User user : users) {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                if (dashBoard.sharedToken != null) {
                    cache.put(dashBoard.sharedToken, new SharedTokenValue(user, dashBoard.id));
                }
            }
        }
    }

    public String refreshSharedToken(User user, DashBoard dash) {
        String newToken = TokenGeneratorUtil.generateNewToken();
        assignToken(user, dash, newToken);
        return newToken;
    }

    private void assignToken(User user, DashBoard dash, String newToken) {
        // Clean old token from cache if exists.
        String oldToken = dash.sharedToken;
        if (oldToken != null) {
            cache.remove(oldToken);
        }

        //assign new token
        dash.sharedToken = newToken;
        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        cache.put(newToken, new SharedTokenValue(user, dash.id));

        log.info("Generated shared token for user {} and dashId {} is {}.", user.email, dash.id, newToken);
    }

    public SharedTokenValue getUserByToken(String token) {
        return cache.get(token);
    }

    public void deleteSharedToken(String sharedToken) {
        if (sharedToken != null) {
            cache.remove(sharedToken);
            log.info("Deleted {} shared token.", sharedToken);
        }
    }

}
