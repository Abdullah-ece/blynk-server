package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.db.DBManager;
import cc.blynk.utils.TokenGeneratorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 14.10.16.
 */
public class TokenManager {

    private static final Logger log = LogManager.getLogger(TokenManager.class);

    private final RegularTokenManager regularTokenManager;
    private final SharedTokenManager sharedTokenManager;
    private final DBManager dbManager;
    private final String host;

    public TokenManager(Collection<Organization> orgs,
                        Collection<User> allUsers,
                        DBManager dbManager, String host) {
        this.regularTokenManager = new RegularTokenManager(orgs);
        this.sharedTokenManager = new SharedTokenManager(allUsers);
        this.dbManager = dbManager;
        this.host = host;
    }

    public void deleteDevice(Device device) {
        String token = device.token;
        if (token != null) {
            regularTokenManager.deleteDeviceToken(token);
            dbManager.removeToken(token);
        }
    }

    public void deleteSharedToken(String sharedToken) {
        sharedTokenManager.deleteSharedToken(sharedToken);
    }

    public TokenValue getTokenValueByToken(String token) {
        return regularTokenManager.getTokenValue(token);
    }

    public SharedTokenValue getUserBySharedToken(String token) {
        return sharedTokenManager.getUserByToken(token);
    }

    public void assignTempToken(TemporaryTokenValue temporaryTokenValue) {
        String newToken = TokenGeneratorUtil.generateNewToken();
        temporaryTokenValue.device.token = newToken;
        regularTokenManager.cache.put(newToken, temporaryTokenValue);
        log.debug("Generated temp token for user {}, dashId {}, deviceId {} is {}.",
                temporaryTokenValue.user.email, temporaryTokenValue.dash.id, temporaryTokenValue.device.id, newToken);
    }

    public void assignToken(int orgId, User user, DashBoard dash, Device device, String newToken) {
        String oldToken = regularTokenManager.assignToken(orgId, user, dash, device, newToken);

        dbManager.assignServerToToken(newToken, host, user.email, dash.id, device.id);
        if (oldToken != null) {
            dbManager.removeToken(oldToken);
        }
    }

    public String refreshToken(int orgId, User user, DashBoard dash, Device device) {
        String newToken = TokenGeneratorUtil.generateNewToken();
        assignToken(orgId, user, dash, device, newToken);
        return newToken;
    }

    public String refreshSharedToken(User user, DashBoard dash) {
        String newToken = TokenGeneratorUtil.generateNewToken();
        sharedTokenManager.assignToken(user, dash, newToken);
        return newToken;
    }

    public void updateRegularCache(String token, TokenValue tokenValue) {
        regularTokenManager.cache.put(token,
                new TokenValue(tokenValue.orgId, tokenValue.user, tokenValue.dash, tokenValue.device));
    }

    public boolean clearTemporaryTokens() {
        long now = System.currentTimeMillis();
        return regularTokenManager.cache.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    public void deleteOrg(int orgId) {
        //todo remove from DB?
        regularTokenManager.cache.entrySet().removeIf(entry -> entry.getValue().belongsToOrg(orgId));
    }
}
