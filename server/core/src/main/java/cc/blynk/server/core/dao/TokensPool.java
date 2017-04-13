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
    private final ConcurrentMap<TokenHolder, User> holder;

    public TokensPool(int expirationPeriodMillis) {
        this.holder = new ConcurrentHashMap<>();
        this.TOKEN_EXPIRATION_PERIOD_MILLIS = expirationPeriodMillis;
    }

    public void addToken(String token, User user) {
        log.info("Adding token for {} user to the pool", user.email);
        cleanupOldTokens();
        holder.put(new TokenHolder(token), user);
    }

    public User getUser(String token) {
        cleanupOldTokens();
        return holder.get(new TokenHolder(token));
    }

    public void removeToken(String token) {
        holder.remove(new TokenHolder(token));
    }

    public int size() {
        return holder.size();
    }

    private void cleanupOldTokens() {
        final long now = System.currentTimeMillis();
        for (Iterator<Map.Entry<TokenHolder, User>> iterator = holder.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<TokenHolder, User> entry = iterator.next();
            if (entry.getKey().createdAt + TOKEN_EXPIRATION_PERIOD_MILLIS < now) {
                iterator.remove();
            }
        }
    }

    private class TokenHolder {
        private long createdAt;
        private String token;

        public TokenHolder(String token) {
            this.createdAt = System.currentTimeMillis();
            this.token = token;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TokenHolder)) return false;

            TokenHolder that = (TokenHolder) o;

            return !(token != null ? !token.equals(that.token) : that.token != null);

        }

        @Override
        public int hashCode() {
            return token != null ? token.hashCode() : 0;
        }
    }

}
