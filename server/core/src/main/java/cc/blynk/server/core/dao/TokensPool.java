package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The Blynk project
 * Created by Andrew Zakordonets
 * Date : 12/05/2015.
 */
public final class TokensPool {

    private static final Logger log = LogManager.getLogger(TokensPool.class);

    private final int TOKEN_EXPIRATION_PERIOD_MILLIS;
    private final ConcurrentMap<String, User> holder;

    public TokensPool(int expirationPeriodMillis) {
        this.holder = new ConcurrentHashMap<>();
        this.TOKEN_EXPIRATION_PERIOD_MILLIS = expirationPeriodMillis;
    }

    public void addToken(String token, User user) {
        log.info("Adding token for {} user to the pool", user.email);
        cleanupOldTokens();
        holder.put(token, user);
    }

    public User getUser(String token) {
        cleanupOldTokens();
        return holder.get(token);
    }

    public void removeToken(String token) {
        holder.remove(token);
    }

    public int size() {
        return holder.size();
    }

    private void cleanupOldTokens() {
        final long now = System.currentTimeMillis();
        for (Iterator<Map.Entry<String, User>> iterator = holder.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, User> entry = iterator.next();
            if (entry.getValue().lastModifiedTs + TOKEN_EXPIRATION_PERIOD_MILLIS < now) {
                iterator.remove();
            }
        }
    }

}
